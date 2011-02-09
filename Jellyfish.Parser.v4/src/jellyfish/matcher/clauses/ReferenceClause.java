/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.util.Map;
import jellyfish.base.DebugClauses;
import jellyfish.common.Pair;
import jellyfish.matcher.VariableContext;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.nodes.ReferenceMatcherNode;
import jellyfish.matcher.nodes.RootMatcherNode;

/**
 *
 * @author Umran
 */
public class ReferenceClause
		extends SimpleClause
{

	private CompositeClause referencedContent;
	private AliasTreeNode innerRootAlias = null;
	private RootMatcherNode innerRootMatcherNode = new RootMatcherNode();
	private NodeAliasList innerEnds = null;

	public ReferenceClause( String xmlLocation, String name, CompositeClause referencedContent ) {
		super( xmlLocation, name );
		this.referencedContent = referencedContent;
	}

	public CompositeClause getReferencedContent() {
		return referencedContent;
	}

	private synchronized void computeRootMatcherNode() {
		if ( innerRootAlias != null ) {
			return;
		}

		NodeAliasList innerStarts = new NodeAliasList( 10 );
		innerEnds = new NodeAliasList( 10 );
		innerRootAlias = AliasTreeNode.createRoot( referencedContent.getName() );

		innerStarts.add( innerRootMatcherNode );

		referencedContent.buildMatchTree( innerRootAlias, null, innerStarts, innerEnds, true );

		System.out.println( "Inner Content Tree:" );
		DebugClauses.printMatcherTree( innerRootMatcherNode );
	}

	@Override
	public SimpleClauseType getSimpleClauseType() {
		return SimpleClauseType.VARIABLE_CLAUSE;
	}

	@Override
	public String toString() {
		return "ref(" + name + ")";
	}

	protected void buildMatchTree( AliasTreeNode alias, VariableContext variableContext,
								   NodeAliasList prevNodes, NodeAliasList ends, boolean last ) {
		if ( referencedContent != null && innerRootAlias == null ) {
			computeRootMatcherNode();
		}

		//	create a copy of the inner root alias tree, with alias as the parent
		Pair<AliasTreeNode, Map<AliasTreeNode, AliasTreeNode>> copyTree = AliasTreeNode.
				copyAliasTree( innerRootAlias, alias );
		AliasTreeNode outerAlias = copyTree.getFirst();
		Map<AliasTreeNode, AliasTreeNode> innerToOuterMapping = copyTree.getSecond();
//		outerAlias.setParent( alias );

		for ( int i = 0; i < prevNodes.size(); ++i ) {
			MatcherNode prevNode = prevNodes.getMatcherNode( i );

			ReferenceMatcherNode matcherNode = new ReferenceMatcherNode( this.name,
																		 innerToOuterMapping );
			matcherNode = prevNode.registerNextNode( matcherNode );
			matcherNode.setParseTree( innerRootMatcherNode );

			if ( innerEnds == null ) {
				System.out.println( "WARNING: ref clause with no inner results found!" );
			}

			ends.add( matcherNode );

			if ( last ) {
				matcherNode.setAcceptEnding();
			}
		}
	}
}
