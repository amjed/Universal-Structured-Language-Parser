/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import java.util.IdentityHashMap;
import jellyfish.matcher.AliasTreeNode;
import java.util.List;
import java.util.Map;
import jellyfish.Test;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.nodes.RootMatcherNode;
import jellyfish.matcher.nodes.VariableMatcherNode;

/**
 *
 * @author Umran
 */
public class VariableClause extends SimpleClause {

    private CompositeClause variableContentTree;

    public VariableClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
    }

    public CompositeClause getVariableContentTree() {
        return variableContentTree;
    }

    public void setVariableContentTree( CompositeClause variableContentTree ) {
        this.variableContentTree = variableContentTree;
    }
    

    @Override
    public SimpleClauseType getSimpleClauseType() {
        return SimpleClauseType.VARIABLE_CLAUSE;
    }

    @Override
    public String toString() {
        return "var("+name+")";
    }

    protected void buildMatchTree(AliasTreeNode alias, NodeAliasList prevNodes, NodeAliasList ends) {
        RootMatcherNode rootMatcherNode = new RootMatcherNode();
        NodeAliasList varEnds = null;
        
        if (variableContentTree!=null) {
            NodeAliasList varStarts = new NodeAliasList(10);
            varEnds = new NodeAliasList(10);

            varStarts.add(rootMatcherNode, new AliasMatcherMap( alias.getRoot() ));

            variableContentTree.buildMatchTree( alias, varStarts, varEnds );

            System.out.println( "Var Content Tree:" );

            for ( int j = 0; j < varEnds.size(); j++ ) {
                MatcherNode end = varEnds.getMatcherNode( j );
                AliasMatcherMap aliasMatcherMap = varEnds.getAliasMatcherMap( j );
                end.setAcceptEnding( aliasMatcherMap );
                System.out.println( "end: "+end+"="+aliasMatcherMap );
            }

            Test.printMatcherTree( rootMatcherNode,  0 );
        }

        for (int i=0; i<prevNodes.size(); ++i) {
            MatcherNode prevNode = prevNodes.getMatcherNode( i );
            AliasMatcherMap aliasMatcherMap = prevNodes.getAliasMatcherMap( i );
            Map<AliasMatcherMap, AliasMatcherMap> innerToOuterMatcherMap =
                    new IdentityHashMap<AliasMatcherMap, AliasMatcherMap>();

            VariableMatcherNode matcherNode = new VariableMatcherNode( this.name,
                                                                       innerToOuterMatcherMap );
            matcherNode = (VariableMatcherNode)prevNode.registerNextNode( matcherNode );
            matcherNode.setParseTree( rootMatcherNode );

            if (aliasMatcherMap!=null) {
                if (varEnds!=null) {
                    for ( int j = 0; j < varEnds.size(); j++ ) {
                        MatcherNode matcherEnd = varEnds.getMatcherNode( j );
                        AliasMatcherMap aliasMatcherMapEnd = varEnds.getAliasMatcherMap( j );
                        AliasMatcherMap map = AliasMatcherMap.createMap( aliasMatcherMap, aliasMatcherMapEnd );
                        ends.add( matcherNode, map );
                    }
                } else {
                    AliasMatcherMap map = AliasMatcherMap.createMap( aliasMatcherMap, alias, matcherNode);
                    ends.add( matcherNode, map );
                }
            }
            else
                ends.add( matcherNode, null );
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
