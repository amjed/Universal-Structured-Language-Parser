/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.parser.clauses;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import jellyfish.matcher.AliasTreeNode;
import java.util.Map;
import java.util.Queue;
import jellyfish.base.DebugClauses;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.nodes.ReferenceMatcherNode;
import jellyfish.matcher.nodes.RootMatcherNode;

/**
 *
 * @author Umran
 */
public class ReferenceClause extends SimpleClause {

    private CompositeClause variableContentTree;
    private AliasTreeNode innerRootAlias = null;
    private RootMatcherNode innerRootMatcherNode = new RootMatcherNode();
    private NodeAliasList innerEnds = null;

    public ReferenceClause( String xmlLocation, String name, CompositeClause variableContentTree )
    {
        super(xmlLocation, name);
        this.variableContentTree = variableContentTree;
    }

    public CompositeClause getVariableContentTree() {
        return variableContentTree;
    }
    
    private synchronized void computeRootMatcherNode()
    {
        if (innerRootAlias!=null) return;

        NodeAliasList innerStarts = new NodeAliasList(10);
        innerEnds = new NodeAliasList(10);
        innerRootAlias = AliasTreeNode.createRoot( variableContentTree.getName() );

        innerStarts.add(innerRootMatcherNode, AliasMatcherMap.createMap( innerRootAlias ));

        variableContentTree.buildMatchTree( innerRootAlias, innerStarts, innerEnds, true );
        
        System.out.println( "Inner Content Tree:" );
        DebugClauses.printMatcherTree( innerRootMatcherNode );
    }

    @Override
    public SimpleClauseType getSimpleClauseType() {
        return SimpleClauseType.VARIABLE_CLAUSE;
    }

    @Override
    public String toString() {
        return "var("+name+")";
    }

    private class AppendStruct {
        public AliasTreeNode dstNode;
        public AliasTreeNode srcNode;

        public AppendStruct( AliasTreeNode dstNode, AliasTreeNode srcNode ) {
            this.dstNode = dstNode;
            this.srcNode = srcNode;
        }

    }

    private Map<AliasTreeNode,AliasTreeNode> append(AliasTreeNode dstParent, AliasTreeNode srcParent)
    {
        int parentTreeSize = dstParent.getTreeSize();
        Map<AliasTreeNode, AliasTreeNode> srcToDstMap = 
                new IdentityHashMap<AliasTreeNode, AliasTreeNode>( parentTreeSize );
        Queue<AppendStruct> queue = new ArrayDeque<AppendStruct>( parentTreeSize );

        queue.add( new AppendStruct( dstParent, srcParent ));

        while (!queue.isEmpty()) {
            AppendStruct struct = queue.remove();

            for (AliasTreeNode srcNode:struct.srcNode.getChildren()) {
                AliasTreeNode dstNode = new AliasTreeNode( srcNode.getName() );
                dstNode.setParent( struct.dstNode );

                srcToDstMap.put( srcNode, dstNode );

                queue.add( new AppendStruct( dstNode, srcNode) );
            }
        }
        
        return srcToDstMap;
    }

    protected void buildMatchTree(AliasTreeNode alias, NodeAliasList prevNodes, NodeAliasList ends, boolean last) {
        if (variableContentTree!=null && innerRootAlias==null) {
            computeRootMatcherNode( );
        }

	System.out.println( "building reference clause" );

        Map<AliasTreeNode,AliasTreeNode> innerToOuterAliasMap = append( alias, innerRootAlias );

        for (int i=0; i<prevNodes.size(); ++i) {
            MatcherNode prevNode = prevNodes.getMatcherNode( i );
            AliasMatcherMap aliasMatcherMap = prevNodes.getAliasMatcherMap( i );
            Map<AliasMatcherMap, AliasMatcherMap> innerToOuterMatcherMap =
                    new IdentityHashMap<AliasMatcherMap, AliasMatcherMap>();

            ReferenceMatcherNode matcherNode = new ReferenceMatcherNode( this.name,
                                                                       innerToOuterMatcherMap );
            matcherNode = prevNode.registerNextNode( matcherNode );
            matcherNode.setParseTree( innerRootMatcherNode );

            if (aliasMatcherMap!=null) {
                if (innerEnds!=null) {
		    System.out.println( "innerEnds.size()="+innerEnds.size() );
                    for ( int j = 0; j < innerEnds.size(); j++ ) {
//                        MatcherNode matcherEnd = varEnds.getMatcherNode( j );
                        AliasMatcherMap innerAliasMatcherMap = innerEnds.getAliasMatcherMap( j );

                        AliasMatcherMap outerAliasMatcherMap = AliasMatcherMap.createMap( aliasMatcherMap );

                        AliasMatcherMap.AliasMatcherIterator it = innerAliasMatcherMap.getIterator();
                        while (it.hasNext()) {
                            it.next();
                            AliasTreeNode innerAliasNode = it.getAlias();
                            AliasTreeNode outerAliasNode = innerToOuterAliasMap.get( innerAliasNode );
                            outerAliasMatcherMap.put( outerAliasNode, it.getMatcher() );
                        }

                        innerToOuterMatcherMap.put( innerAliasMatcherMap, outerAliasMatcherMap );
			
                        ends.add( matcherNode, outerAliasMatcherMap );
                    }
                } else {
                    System.out.println( "WARNING: ref clause with no inner results found!" );
		    //	a reference clause with no results matching in the inner tree
		    //	    will not allow matching for any input except for wild-cards.		    
                    AliasMatcherMap map = AliasMatcherMap.createMap( aliasMatcherMap, alias, matcherNode);
                    ends.add( matcherNode, map );
                }
            }
            else {
                System.out.println( "WARNING: Previous node's alias matcher map is NULL" );
                ends.add( matcherNode, null );
            }

            if (last) {
                matcherNode.setAcceptEnding();
            }
        }
    }

    /*
    @Override
    protected Pair<ExpandedClauses,List<AliasTreeNode>> internalExpand() {
        ExpandedClauses choices = new ExpandedClauses(1);
        choices.add(new ArrayList<ExpandedClauseHolder>(1));

        ExpandedClauseHolder holder = new ExpandedClauseHolder(this);
        choices.addToAll(holder);
        
        return Pair.create(choices, (List<AliasTreeNode>)Collections.EMPTY_LIST);
    }
    */
}
