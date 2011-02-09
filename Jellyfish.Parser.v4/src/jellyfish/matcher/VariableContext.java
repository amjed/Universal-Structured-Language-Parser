/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import jellyfish.base.ClauseBase;
import jellyfish.base.DebugClauses;
import jellyfish.common.CaseInsensitiveStringComparator;
import jellyfish.common.PatternExtractor;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.matcher.input.InputToken;
import jellyfish.matcher.input.InputTokenList;
import jellyfish.matcher.nodes.ConstantMatcherNode;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.nodes.RootMatcherNode;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.triplestore.ReferenceEngine;
import jellyfish.triplestore.ReferenceResults;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.model.Entity;
import jellyfish.triplestore.model.EntityName;
import jellyfish.triplestore.model.Language;

/**
 *
 * @author Xevia
 *
 * Say hi to the joint between the front side - the user input parsing tree (i.e. the matcher)
 *	and the middle layer - the semantic network and its reference engine
 *
 * This class maintains a query that defines what should be in the variables that reference it.
 *
 * The query obtains data from the semantic network's entity names.
 */
public class VariableContext
{

	private final PatternExtractor QUERY_VARS = new PatternExtractor( "\\{([^\\}]*)\\}" );
	private VariableContext parentContext;
	private AliasTreeNode parentAlias;
	private ClauseBase clauseBase;
	private ReferenceEngine referenceEngine;
	
	private Map<String, InternRootMatcherNode> variableValueMap;
	private String query;
	private String processedQuery;
	private ReferenceResults referenceResults;
	private long lastUpdate;

	public VariableContext( VariableContext parentContext, AliasTreeNode parentAlias,
							ClauseBase clauseBase, String query )
	{
		this.parentContext = parentContext;
		this.parentAlias = parentAlias;
		this.clauseBase = clauseBase;
		this.referenceEngine = clauseBase.getReferenceEngine();
		assert (referenceEngine != null);
		this.variableValueMap = new TreeMap<String, InternRootMatcherNode>();
		this.query = query;
		this.processedQuery = "";
		this.referenceResults = null;
		this.lastUpdate = 0;

		System.out.println( "variable context created: " + query );
		System.out.println( "\tparent alias="+Arrays.toString( parentAlias.getCompleteName() ) );
	}
	
	public VariableContext getParentContext() {
		return parentContext;
	}

	public ClauseBase getClauseBase() {
		return clauseBase;
	}

	public String getQuery() {
		return query;
	}
	
	public RootMatcherNode registerAlias( AliasTreeNode aliasTreeNode ) {
		String id = aliasToIdentifier( aliasTreeNode );
		if ( variableValueMap.containsKey( id ) ) {
			return variableValueMap.get( id );
		} else {
			InternRootMatcherNode matcherNode = new InternRootMatcherNode(
					this, id, clauseBase.getDictionary() );
			matcherNode.setAliasTreeNode( AliasTreeNode.createRoot( id ) );
			variableValueMap.put( id, matcherNode );
			return matcherNode;
		}
	}

	private void ensureLatestResults()
	{
		System.out.println( "VariableContext('"+query+"'):"+lastUpdate+":"+referenceEngine.getLatestUpdate() );
		if (lastUpdate==0 || referenceEngine.getLatestUpdate()>lastUpdate) {
			//	synchorize to avoid multiple-threads invoking multiple unnecessary updates
			synchronized (this) {
				if (lastUpdate==0 || referenceEngine.getLatestUpdate()>lastUpdate) {
					try {
						System.out.println( "fetching results for query: '" + query + "'" );
						if (this.processedQuery.isEmpty()) {
							//	query never changes for the context, hence need to do this once only.
							this.processedQuery = processQuery( query );
						}
						System.out.println( "\tprocessedQuery = '" + processedQuery + "'" );
						referenceResults = referenceEngine.query( processedQuery );
						assert(referenceResults!=null);
						lastUpdate = referenceEngine.getLatestUpdate();
					} catch ( Exception ex ) {
						throw new RuntimeException(
								"Error while processing query:\n" + query,
								ex );
					}
				}
			}
		}
		assert(referenceResults!=null);
	}

	public ReferenceResults getReferenceResults() {
		ensureLatestResults();
		assert(referenceResults!=null);
		return referenceResults;
	}

	public long getLastUpdate() {
		ensureLatestResults();
		return lastUpdate;
	}
	
	/*
	 * Requires the alias tree to be fully developed, which by design of this class
	 *	(and the InternRootMatcherNode), would be called only after the matcher tree (and hence the
	 *	alias tree has been created, and matching user's input is being done. (Hence the first
	 *	user's input is bound to take more time to be matched.)
	 */
	private String processQuery( String query ) {
		Matcher matcher = QUERY_VARS.getPattern().matcher( query );
		StringBuffer buffer = new StringBuffer();
		while ( matcher.find() ) {
			String capture = matcher.group( 1 );
			System.out.println( "\tcapture=" + capture );
			AliasTreeNode aliasTreeNode = findFirstNearestAlias( capture );
			if ( aliasTreeNode != null ) {
				System.out.println( "\talias=" + Arrays.toString( aliasTreeNode.getCompleteName() ) );
				String identifier = aliasToIdentifier( aliasTreeNode );
				matcher.appendReplacement( buffer, identifier );
			} else {
				throw new RuntimeException( "Unrecognized alias '" + capture + "' in query '" +
											query + "'" );
			}
		}
		matcher.appendTail( buffer );
		return buffer.toString();
	}

	private AliasTreeNode findFirstNearestAlias( String name ) {
		System.out.println( "finding nearest alias to '" + name + "'" );
		String[] names = name.split( "\\." );
		System.out.println( "\tnames=" + Arrays.toString( names ) );
		AliasTreeNode aliasTreeNode = parentAlias;
		for ( int i = 0; i < names.length; ++i ) {
			List<AliasTreeNode> aliases = aliasTreeNode.getChildrenWithAlias( names[i] );
			if ( aliases != null && !aliases.isEmpty() ) {
				aliasTreeNode = aliases.get( 0 );
				System.out.println( "\t\tcheck " +
									Arrays.toString( aliasTreeNode.getCompleteName() ) );
			} else {
				return null;
			}
		}
		return aliasTreeNode;
	}

	private String aliasToIdentifier( AliasTreeNode aliasTreeNode ) {
		StringBuilder invCompleteIndexedName = new StringBuilder();

		while ( aliasTreeNode != null ) {
			if ( invCompleteIndexedName.length() > 0 ) {
				invCompleteIndexedName.append( '.' );
			}
			invCompleteIndexedName.append( aliasTreeNode.getName() );
			invCompleteIndexedName.append( aliasTreeNode.getIndex() );
			aliasTreeNode = aliasTreeNode.getNormalParent();
		}

		String id = invCompleteIndexedName.toString();
		id = id.replaceAll( "\\W", "_" );
		return "V" + id;
	}

	private static class InternConstMatcherNode
			extends ConstantMatcherNode
	{

		private Set<String> matchValueSet;

		public InternConstMatcherNode( TokenDictionary dictionary, String word ) {
			super( dictionary, word );
			this.matchValueSet = null;
		}

		public void setAcceptEnding( String entityName ) {
			if ( !super.acceptedEnding ) {
				super.setAcceptEnding();
			}
			if ( this.matchValueSet == null ) {
				this.matchValueSet = new TreeSet<String>( new CaseInsensitiveStringComparator() );
			}
			this.matchValueSet.add( entityName );
		}

		@Override
		protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
							  List<MatchResult> finalResults, boolean matchAtEnd ) {
			InputToken token = input.get( inputIndex );
			if ( token.isWildCard() ) {
				super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
			} else {
				if ( token.isInDictionary() &&
					 token.getDictionaryEntryId() == this.getDictionaryEntry().getId() ) {
					if ( matchValueSet == null || matchValueSet.isEmpty() ) {
						super.match( input, inputIndex,
									 new MatchResult( aliasTreeNode,
													  this,
													  prevMatch,
													  inputIndex,
													  dictionaryEntry.getWord(),
													  null ),
									 finalResults, matchAtEnd );
					} else {
						for ( String match : matchValueSet ) {
							super.match( input, inputIndex,
										 new MatchResult( aliasTreeNode,
														  this,
														  prevMatch,
														  inputIndex,
														  dictionaryEntry.getWord(),
														  match ),
										 finalResults, matchAtEnd );
						}
					}
				}
			}
		}

		@Override
		protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
											   MatchResult prevMatch, List<MatchResult> finalResults ) {
			if ( matchValueSet == null || matchValueSet.isEmpty() ) {
				finalResults.add( new MatchResult( aliasTreeNode,
												   this,
												   prevMatch,
												   inputIndex,
												   dictionaryEntry.getWord(),
												   null ) );
			} else {
				for ( String match : matchValueSet ) {
					finalResults.add( new MatchResult( aliasTreeNode,
													   this,
													   prevMatch,
													   inputIndex,
													   dictionaryEntry.getWord(),
													   match ) );
				}
			}
		}
	}

	private static class InternRootMatcherNode
			extends RootMatcherNode
	{
		private VariableContext variableContext;
		private String variableName;
		private TokenDictionary dictionary;
		private long lastUpdate = 0;

		public InternRootMatcherNode( VariableContext variableContext, String variableName, TokenDictionary dictionary ) {
			this.variableContext = variableContext;
			this.variableName = variableName;
			this.dictionary = dictionary;
		}

		public void dumpTree() {
			nextNodes.clear();
		}

		/*
		 * Still haven't found a way to trim the tree, then grow it again during updates..
		 *	That would probably be a perfomance booster in the future. For now, dump the whole
		 *	tree then re-build it again.
		 */
		private void buildTree()
		{
			System.out.println( "Building Root-Matcher for Variable "+variableName );
			System.out.println( "-------------------------------------------------" );
			
			Tokenizer tokenizer = variableContext.getClauseBase().getTokenizer();
			ReferenceResults results = variableContext.getReferenceResults();
			TripleStore tripleStore = variableContext.getClauseBase().getTripleStore();
			String languageName = variableContext.getClauseBase().getLanguage();

			java.util.concurrent.locks.ReentrantLock lock = tripleStore.getUpdateLock();
			//	wait for update lock, so as to make sure the state of the triple-store doesn't
			//		change in between this update.
			lock.lock();
			try {
				Language language = tripleStore.getLanguage( languageName );

				if (language==null) {
					throw new RuntimeException( "Language not found in the triplestore '"+
							languageName+"' while processing query: "+variableContext.getQuery()+"" );
				}

				assert(results!=null);
				assert(results.getVariables()!=null);

				int variableIndex = -1;
				//	find this variable in the result variables...
				for ( int i = 0; i < results.getVariables().length; ++i ) {
					String variable = results.getVariables()[i];
					if (variable.equals( this.variableName )) {
						variableIndex = i;
						break;
					}
				}

				dumpTree();

				if (variableIndex<0) {
					System.out.println( "WARNING: Variable '"+this.variableName+
							"' not found while processing query: "+variableContext.getQuery());
				} else {
					for ( String[] result : results.getResults() ) {
						//	the result returned is actually the back-end name of the entity
						//		(or the name given to the entity by the programmer), and not
						//		the front-end name of the entity (or the name used by the users
						//		to refer to the entity). These front-end names are language
						//		specific and can be queried from the semantic-net database.
						//	hopefully the data in the database doesn't change while this is being processed.
						String name = result[variableIndex];
						System.out.println( "\t\tentity=" + name );

						Entity entity = tripleStore.getEntity( name );
						if ( entity == null ) {
							System.out.println( "WARNING: While processing query '" + variableContext.getQuery() +
												"', an unknown entity '" + name + "' was found." );
							continue;
						}

						List<EntityName> entityNames = tripleStore.getEntityNames(entity, language);

						for ( EntityName entityName : entityNames ) {
							System.out.println( "\t\t\tentityname(" + language.getName() + ")=" + entityName.
									getName() );
							ArrayList<String> tokenList = new ArrayList<String>();
							tokenizer.tokenize( tokenList, entityName.getName() );
							this.growInternalTree( tokenList, name );
						}
					}
				}

				System.out.println( "variable '" + variableName + "' matcher tree:" );
				DebugClauses.printMatcherTree( this );
			} finally {
				lock.unlock();
			}
		}

		private void ensureLatestTree()
		{
			if (lastUpdate==0 || variableContext.getLastUpdate()>lastUpdate) {
				//	synchorize to avoid multiple-threads invoking multiple unnecessary updates
				synchronized (this) {
					if (lastUpdate==0 || variableContext.getLastUpdate()>lastUpdate) {
						System.out.println( "building tree" );
						buildTree();
						lastUpdate = variableContext.getLastUpdate();
					}
				}
			}
		}

		public void growInternalTree( List<String> tokens, String entityName ) {
			MatcherNode currentNode = this;
			InternConstMatcherNode lastInternalConnstantMatcherNode = null;

			for ( int i = 0; i < tokens.size(); ++i ) {
				String token = tokens.get( i );

				InternConstMatcherNode matcherNode = new InternConstMatcherNode( dictionary, token );
				matcherNode.setAliasTreeNode( new AliasTreeNode( token ) );
				matcherNode = currentNode.registerNextNode( matcherNode );

				currentNode = matcherNode;
				lastInternalConnstantMatcherNode = matcherNode;
			}

			if ( lastInternalConnstantMatcherNode != null ) {
				lastInternalConnstantMatcherNode.setAcceptEnding( entityName );
			}
		}

		private void find( List<String> tokens, int currentToken, MatcherNode currentNode,
						   String entityName, List<InternConstMatcherNode> returnList ) {
			for ( MatcherNode childNode : currentNode.getNextNodes() ) {
				InternConstMatcherNode node = (InternConstMatcherNode) childNode;
				if ( node.getDictionaryEntry().getWord().equalsIgnoreCase(
						tokens.get( currentToken ) ) ) {
					if ( tokens.size() == currentToken + 1 ) {
						if ( node.matchValueSet != null &&
							 node.matchValueSet.contains( entityName ) ) {
							returnList.add( node );
						}
					} else {
						find( tokens, currentToken + 1, node, entityName, returnList );
					}
				}
			}
		}

		private void findAll( MatcherNode currentNode, String entityName,
							  List<InternConstMatcherNode> returnList ) {
			for ( MatcherNode childNode : currentNode.getNextNodes() ) {
				InternConstMatcherNode node = (InternConstMatcherNode) childNode;

				if ( node.matchValueSet != null &&
					 node.matchValueSet.contains( entityName ) ) {
					returnList.add( node );
				}
				
				findAll( node, entityName, returnList );
			}
		}

		public void trimNode( MatcherNode node ) {
			MatcherNode pNode = node.getParentNode();
			if ( pNode != null ) {
				pNode.removeNextNode( node );
				if ( pNode.getNextNodes().isEmpty() ) {
					trimNode( pNode );
				}
			}
		}

		public void trimInternalTree( List<String> tokens, String entityName ) {

			List<InternConstMatcherNode> resultNodes = new ArrayList<InternConstMatcherNode>();
			if ( !tokens.isEmpty() ) {
				find( tokens, 0, this, entityName, resultNodes );
			}

			for ( InternConstMatcherNode node : resultNodes ) {
				assert (node.matchValueSet != null && node.matchValueSet.contains( entityName ));
				node.matchValueSet.remove( entityName );
				if ( node.matchValueSet.isEmpty() ) {
					trimNode( node );
				}
			}

		}

		public void trimInternalTree( String entityName ) {
			List<InternConstMatcherNode> resultNodes = new ArrayList<InternConstMatcherNode>();
			findAll( this, entityName, resultNodes );

			for ( InternConstMatcherNode node : resultNodes ) {
				assert (node.matchValueSet != null && node.matchValueSet.contains( entityName ));
				node.matchValueSet.remove( entityName );
				if ( node.matchValueSet.isEmpty() ) {
					trimNode( node );
				}
			}
		}

		@Override
		protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
							  List<MatchResult> finalResults, boolean matchAtEnd ) {
			ensureLatestTree();
			super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
		}

		@Override
		public void match( InputTokenList input,
						   List<MatchResult> output ) {
			ensureLatestTree();
			super.match( input, output );
		}
		
	}
}
