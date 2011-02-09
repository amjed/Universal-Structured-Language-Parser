/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.io.PrintStream;
import java.util.*;
import java.util.ArrayList;
import jellyfish.base.ClauseBase;
import jellyfish.common.Common;
import jellyfish.matcher.VariableContext;

/**
 *
 * @author Umran
 *
 * This class defines a container clause class that contains in it sub-clauses.
 *  It defines/allows no removal of sub-clauses because of 2 reasons:
 *  1) Since clauses as loaded from a script file, no changes are expected
 *      after loading.
 * 
 */
public final class AndClause
		extends CompositeClause
{

	private class SubclauseHolder
	{

		public ClauseAlias alias;
		public Clause subclause;
		public boolean optional;
		public int maxCardinality;

		public SubclauseHolder( ClauseAlias alias, Clause subclause, boolean optional,
								int maxCardinality ) {
			this.alias = alias;
			this.subclause = subclause;
			this.optional = optional;
			this.maxCardinality = maxCardinality;
		}

		@Override
		public String toString() {
			return "{" + alias.getName() + "=" + subclause.toString() + (optional ? ":(0" : ":(1") +
				   ":" + maxCardinality + ")}";
		}
	}
	private List<SubclauseHolder> subclauses = new ArrayList<SubclauseHolder>();
	private boolean primaryClause;
	private String query = "";
	private ClauseBase clauseBase;
	private String onMatchFunctionName = "";

	public AndClause( String xmlLocation, String name, boolean primaryClause, ClauseBase clauseBase ) {
		super( xmlLocation, name );
		this.primaryClause = primaryClause;
		this.clauseBase = clauseBase;
	}

	public AndClause( String xmlLocation, String name, ClauseBase clauseBase ) {
		this( xmlLocation, name, false, clauseBase );
	}

	public String getOnMatchFunctionName() {
		return onMatchFunctionName;
	}

	public void setOnMatchFunctionName( String onMatchFunctionName ) {
		this.onMatchFunctionName = onMatchFunctionName;
	}

	public boolean isPrimaryClause() {
		return primaryClause;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery( String query ) {
		if ( !this.query.isEmpty() ) {
			throw new RuntimeException( "Multiple queries found in clause " + this.xmlLocation );
		}
		this.query = query;
	}

	@Override
	public CompositeClauseType getCompositeClauseType() {
		return CompositeClauseType.AND_CLAUSE;
	}

	public synchronized void addSubClause( Clause subClause, ClauseAlias alias, boolean optional,
										   int maxCardinality ) {
		if ( maxCardinality < 1 ) {
			throw new RuntimeException(
					"Invalid maximum cardinality for subclause alias='" + alias + "' of clause '" +
					getName() + "'" );
		}

		SubclauseHolder holder = new SubclauseHolder( alias, subClause, optional, maxCardinality );
		subclauses.add( holder );
	}

	@Override
	public boolean containsClause( Clause clause ) {
		if ( this == clause ) {
			return true;
		}
		for ( SubclauseHolder holder : subclauses ) {
			if ( holder.subclause.containsClause( clause ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void replaceClause( Clause current,
							   Clause replacement ) {
		int index = -1;
		for ( int i = 0; i < subclauses.size(); ++i ) {
			if ( subclauses.get( i ).subclause.equals( current ) ) {
				index = i;
				break;
			}
		}
		if ( index >= 0 ) {
			subclauses.get( index ).subclause = replacement;
		}
	}

	private static class NodeAliasListState
	{

		public NodeAliasList myPrevNodes;
		public NodeAliasList myNextNodes;
		public NodeAliasList subPrevNodes;
		public NodeAliasList subNextNodes;

		public NodeAliasListState( int initialSize ) {
			this.myPrevNodes = new NodeAliasList( initialSize );
			this.myNextNodes = new NodeAliasList( initialSize );
			this.subPrevNodes = new NodeAliasList( initialSize );
			this.subNextNodes = new NodeAliasList( initialSize );
		}
	}

	public void buildMatchTree( AliasTreeNode alias, VariableContext parentVarContext,
								NodeAliasList prevNodes,
								NodeAliasList ends, boolean last ) {
		boolean nonOptional = false;
		for ( SubclauseHolder holder : subclauses ) {
			if ( !holder.optional ) {
				nonOptional = true;
				break;
			}
		}

		VariableContext varContext = null;
		if ( !query.isEmpty() ) {
			//	if a query is involved, we have to mark the alias-tree to make sure that
			//		any alias-nodes added after this form a distinct sub-tree in the
			//		alias-tree. Hence, we replace the existing alias, which might belong
			//		to either this node, or one of the ancestor nodes, and add a SYS-INC
			//		alias-node. Any future alias-nodes will be added to this SYS-INC
			//		alias-node, and any references to the added aliases from the query
			//		can be searched for by the variable-context from this SYS-INC node.
			AliasTreeNode newAlias = AliasTreeNode.createSysInc();
			newAlias.setParent( alias );
			alias = newAlias;
			varContext = new VariableContext( parentVarContext, alias, clauseBase, query );
		}

		if ( !nonOptional ) {
			throw new RuntimeException(
					"No compulsory element found in the clause '" + name + "' location: " +
					getXmlLocation() );
		}

		NodeAliasListState lists = new NodeAliasListState( subclauses.size() );

		lists.myPrevNodes.clear();
		lists.myPrevNodes.addAll( prevNodes );

		for ( int k = 0; k < subclauses.size(); ++k ) {

			SubclauseHolder holder = subclauses.get( k );
			boolean isLastHolder = k == subclauses.size() - 1;

			AliasTreeNode subClauseAlias = computeChildAlias( alias, holder.alias );

			lists.myNextNodes.clear();

			if ( holder.optional ) {
				lists.myNextNodes.addAll( lists.myPrevNodes );
			}

			lists.subPrevNodes.clear();
			lists.subPrevNodes.addAll( lists.myPrevNodes );
			for ( int i = 1; i <= holder.maxCardinality; ++i ) {
				lists.subNextNodes.clear();
				holder.subclause.buildMatchTree( subClauseAlias,
												 varContext == null ? parentVarContext : varContext,
												 lists.subPrevNodes, lists.subNextNodes,
												 last && isLastHolder );
				lists.myNextNodes.addAll( lists.subNextNodes );

				NodeAliasList tmp = lists.subPrevNodes;
				lists.subPrevNodes = lists.subNextNodes;
				lists.subNextNodes = tmp;
			}

			NodeAliasList tmp = lists.myPrevNodes;
			lists.myPrevNodes = lists.myNextNodes;
			lists.myNextNodes = tmp;
		}

		ends.addAll( lists.myPrevNodes );
	}

	@Override
	public String toString() {
		return "AndClause{\"" + name + "\":" + subclauses.toString() + "}";
	}

	public void printClauseTree( PrintStream printStream, int depth ) {
		for ( SubclauseHolder holder : subclauses ) {
			printStream.print( Common.getTabs( depth ) );
			printStream.println( "AND>>" + holder.alias + (holder.optional ? ": [op] :" : " : ") +
								 holder.maxCardinality );
			holder.subclause.printClauseTree( printStream, depth + 1 );
		}
	}
}
