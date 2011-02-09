package jellyfish.prolog;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Var;
import java.util.HashMap;
import jellyfish.triplestore.ReferenceResults;
import jellyfish.triplestore.ReferenceEngine;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import jellyfish.common.CaseSensitiveStringComparator;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.tokenizer.prolog.PrologTokenizer;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.model.BaseEntity;
import jellyfish.triplestore.model.NamedEntity;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;
import jellyfish.triplestore.model.Value;

public class PrologReferenceEngine implements ReferenceEngine
{

	private PrologTokenizer tokenizer;
	private TripleStore tripleStore;
	
	private Prolog prolog = null;
	private long lastPrologUpdate = 0;

	public PrologReferenceEngine( TripleStore tripleStore ) {
		this.tokenizer = new PrologTokenizer();
		this.tripleStore = tripleStore;
	}

	public ReferenceResults query( String question )
			throws MalformedGoalException, NoSolutionException {
		ensureLastestProlog();
		
		question = processStrings( tokenizer, question );
		question = processTheory( question );
		System.out.println( "Query Question: " + question );
		PrologReferenceResults prologResults = null;
		SolveInfo si = prolog.solve( question );
		while ( si != null && si.isSuccess() ) {
			if ( prologResults == null ) {
				prologResults = new PrologReferenceResults( si );
			} else {
				prologResults.addResults( si );
			}
			try {
				si = prolog.solveNext();
			} catch ( NoMoreSolutionException ex ) {
				si = null;
			}
		}
		prolog.solveEnd();

		if (prologResults!=null)
			return prologResults;					//	filled result set
		else
			return new PrologReferenceResults();	// empty result set
	}

	public long getLatestUpdate() {
		ensureLastestProlog();
		return lastPrologUpdate;
	}

	private void ensureLastestProlog()
	{
		if (tripleStore.getLastUpdate()>lastPrologUpdate) {
			synchronized (this) {
				if (tripleStore.getLastUpdate()>lastPrologUpdate) {
					//	to avoid the triplestore updating the middle of this update...
					//		we acquire the update lock.
					java.util.concurrent.locks.ReentrantLock lock = tripleStore.getUpdateLock();
					lock.lock();
					try {
						StringBuilder builder = new StringBuilder();
						Prolog pr = new Prolog();

						String setsCode = PrologTemplates.getSetsCode();
						builder.append( setsCode );
						pr.addTheory( new Theory(setsCode) );

						List<Theory> theories = new ArrayList<Theory>();
						define( builder, theories, tripleStore );

						System.out.println( "PROLOG" );
						System.out.println( "=======" );
						System.out.println( builder.toString() );

						for ( Theory t : theories ) {
							pr.addTheory( t );
						}

						this.prolog = pr;
						this.lastPrologUpdate = tripleStore.getLastUpdate();
					} catch ( Exception ex ) {
						throw new RuntimeException( "Error while compiling triplestore to prolog code", ex);
					} finally {
						lock.unlock();
					}
				}
			}
		}
	}

	private static String convertStringToArray( String value ) {
		StringBuilder stringArray = new StringBuilder();

		stringArray.append( '[' );
		for ( int j = 0; j < value.length(); ++j ) {
			if ( j != 0 ) {
				stringArray.append( ',' );
			}
			stringArray.append( value.codePointAt( j ) );
		}
		stringArray.append( ']' );

		return stringArray.toString();
	}

	private static String processStrings( Tokenizer tokenizer, String theory ) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokenizer.tokenize( tokens, theory );

		for ( int i = 0; i < tokens.size(); ++i ) {
			String token = tokens.get( i );
			if ( (token.startsWith( "'" ) && token.endsWith( "\'" )) ||
				 (token.startsWith( "\"" ) || token.endsWith( "\"" )) ) {
				tokens.set( i, convertStringToArray( token.substring( 1, token.length() - 1 ) ) );
			}
		}

		return tokenizer.combine( tokens );
	}

	private static String processTheory( String theory ) {
		theory = theory.trim();
		if ( !theory.endsWith( "." ) ) {
			theory = theory + ".";
		}
		return theory;
	}

	private static String baseEntityToString( BaseEntity baseEntity ) {
		String ret = "";
		if ( baseEntity.isNamedEntity() ) {
			ret = ((NamedEntity) baseEntity).getName();
		} else {
			if ( baseEntity.isValue() ) {
				Value value = (Value) baseEntity;
				switch ( value.getValueType() ) {
					case STRING:
						ret = convertStringToArray( value.getStringValue().getValue() );
						break;
					case INTEGER:
						ret = Long.toString( value.getIntegerValue().getValue() );
						break;
					case FLOAT:
						ret = Double.toString( value.getFloatValue().getValue() );
						break;
				}
			} else {
				ret = baseEntity.toString();
			}
		}
		return ret;
	}
	
	private static void define( StringBuilder builder, List<Theory> theories, Relationship rel, List<Triple> triples ) throws InvalidTheoryException {
		StringBuilder sb = new StringBuilder();

		sb.append(
				PrologTemplates.getRelationshipHeader(
				rel.getName(),
				rel.isTransitive(),
				rel.isSymmetric() ) );

		// if a relationship is symmetric as well as transitive:
		//	    since a symmetric relationship is a bi-directional relationship
		//			making it transitive means that any attribute applied to one
		//			node, applies to all nodes connected to that node (directly
		//			or indirectly).
		//		to make processing easier, it is easier to break down the
		//			nodes to sets, in which application of an attribute to a node
		//			in a set, means application of the attribute to the set.
		//
		//	the following portion breaks down the graph (of bi-directionally connected
		//		nodes) to the smallest mutually exclusive sets.
		//	it then adds each of the set in the form of "pred( [ ..set.. ] )."
		if ( rel.isSymmetric() && rel.isTransitive() ) {
			List<Set<String>> atomSets = new ArrayList<Set<String>>();
			Map<String, Integer> atomToSetMap = new TreeMap( new CaseSensitiveStringComparator() );

			for ( Triple t : triples ) {
				String subject = baseEntityToString( t.getSubject() );
				String object = baseEntityToString( t.getObject() );

				int subjectSetId = -1;
				int objectSetId = -1;

				if ( atomToSetMap.containsKey( subject ) ) {
					subjectSetId = atomToSetMap.get( subject );
				}
				if ( atomToSetMap.containsKey( object ) ) {
					objectSetId = atomToSetMap.get( object );
				}

				//  if they come from the same set or are both new
				if ( subjectSetId == objectSetId ) {
					//	if both new, create a new set
					if ( subjectSetId < 0 ) {
						Set<String> set = new TreeSet<String>( new CaseSensitiveStringComparator() );
						set.add( subject );
						set.add( object );

						int index = atomSets.size();
						atomSets.add( set );

						atomToSetMap.put( subject, index );
						atomToSetMap.put( object, index );
					}
					//	otherwise, if from same set.. ignore.
				} else {
					//	if both are present... (and obviously not from the same set)
					if ( subjectSetId > 0 && objectSetId > 0 ) {
						//  let us merge the sets...

						Set<String> subjectSet = atomSets.get( subjectSetId );
						Set<String> objectSet = atomSets.get( objectSetId );

						//  include all the object set to subject set
						subjectSet.addAll( objectSet );

						//  remove the object set
						atomSets.remove( objectSetId );

						//  set all contents of object set to subject set
						for ( String atom : objectSet ) {
							atomToSetMap.put( atom, subjectSetId );
						}
					} else //  if either one is absent
					{
						//  subject set missing
						if ( subjectSetId < 0 ) {
							//	add subject to object's set
							atomSets.get( objectSetId ).add( subject );
							atomToSetMap.put( subject, objectSetId );
						} else {    // if object set missing
							//	add object to subject's set
							atomSets.get( subjectSetId ).add( object );
							atomToSetMap.put( object, subjectSetId );
						}
					}
				}
			}

			//	for each set that was found:
			for ( Set<String> atomSet : atomSets ) {
				sb.append( PrologTemplates.getRelationshipAssertedName( rel.getName() ) );
				sb.append( "( [" );
				boolean first = true;
				for ( String atom : atomSet ) {
					if ( !first ) {
						sb.append( ',' );
					} else {
						first = false;
					}
					sb.append( atom );
				}
				sb.append( "] ).\n" );
			}
		} else {
			//	other wise, just convert each triple to the form "pred( sub, obj )."
			for ( Triple t : triples ) {
				String subject = baseEntityToString( t.getSubject() );
				String object = baseEntityToString( t.getObject() );

				sb.append( PrologTemplates.getRelationshipAssertedName( t.getPredicate().getName() ) ).
						append( "( " ).
						append( subject ).
						append( " , " ).
						append( object ).
						append( " ).\n" );
			}
		}

		sb.append(
				PrologTemplates.getRelationshipDeclaration(
				rel.getName(),
				rel.isTransitive(),
				rel.isSymmetric() ) );

		String theory = sb.toString();

		builder.append( theory );
		theories.add( new Theory( theory ) );
	}

	private static void define( StringBuilder builder, List<Theory> theories, TripleStore tripleStore ) throws InvalidTheoryException {
		for ( Relationship rel : tripleStore.getRelationships() ) {
			List<Triple> relTriples = tripleStore.getTriplesByPredicate( rel );
			define( builder, theories, rel, relTriples );
		}
	}

	/*
	private static void define( StringBuilder builder, List<Theory> theories, String theory ) {
		theory = processStrings( tokenizer, theory );
		theory = processTheory( theory );
		builder.append( theory ).append( "\n" );
		try {
			theories.add( new Theory( theory ) );
		} catch ( InvalidTheoryException ex ) {
			Logger.getLogger( PrologReferenceEngineBuilder.class.getName() ).log( Level.SEVERE, null,
																				  ex );
		}
	}
	*/
	
	private static class PrologReferenceResults
			implements ReferenceResults
	{

		private String[] vars;
		private Map<String, Integer> m;
		private List<String[]> results;

		//	used to create when no results
		private PrologReferenceResults() {
			vars = new String[0];
			m = Collections.EMPTY_MAP;
			results = Collections.EMPTY_LIST;
		}

		//	used to create when results exist
		private PrologReferenceResults( SolveInfo info )
				throws NoSolutionException {
			List<Var> vs = info.getBindingVars();
			vars = new String[vs.size()];
			m = new HashMap<String, Integer>( vs.size() );
			for ( int i = 0; i < vs.size(); ++i ) {
				Var v = vs.get( i );
				vars[i] = v.getName();
				m.put( v.getName(), i );
			}
			this.results = new ArrayList<String[]>();
			addResults( info );
		}

		private void addResults( SolveInfo info )
				throws NoSolutionException {
			List<Var> vs = info.getBindingVars();
			String[] result = new String[vars.length];
			for ( Var v : vs ) {
				Integer i = m.get( v.getName() );
				if ( i == null ) {
					throw new RuntimeException( "Unknown variable '" + v.getName() +
												"' met in solution alternative: " + info );
				}
				result[i] = v.toStringFlattened();
			}
			results.add( result );
		}

		public Integer getVariableIndex( String var ) {
			return m.get( var );
		}

		public List<String[]> getResults() {
			return results;
		}

		public String[] getVariables() {
			return vars;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			for ( String v : vars ) {
				buffer.append( v ).append( "\t" );
			}
			buffer.append( "\n" );
			buffer.append( "================================\n" );
			for ( String[] result : results ) {
				for ( String v : result ) {
					buffer.append( v ).append( "\t" );
				}
				buffer.append( "\n" );
			}
			return buffer.toString();
		}
	}
}
