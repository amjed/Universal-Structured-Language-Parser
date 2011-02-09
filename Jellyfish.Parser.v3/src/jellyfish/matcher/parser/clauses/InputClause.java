/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.parser.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.util.List;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.nodes.InputMatcherNode;
import jellyfish.matcher.nodes.MatcherNode;

/**
 *
 * @author Umran
 */
public class InputClause extends SimpleClause {

    private String regex;

    public InputClause( String xmlLocation, String name, String regex )
    {
        super(xmlLocation, name);
        this.regex = regex;
    }
    
    @Override
    public SimpleClauseType getSimpleClauseType() {
        return SimpleClauseType.INPUT_CLAUSE;
    }

    @Override
    public String toString() {
        return "<" + regex + ">";
    }

    protected void buildMatchTree(AliasTreeNode alias, NodeAliasList prevNodes, NodeAliasList ends, boolean last) {
        for (int i=0; i<prevNodes.size(); ++i) {
            MatcherNode prevNode = prevNodes.getMatcherNode( i );

            InputMatcherNode matcherNode = prevNode.registerNextNode( new InputMatcherNode( regex ) );
            
            AliasMatcherMap aliasMatcherMap = prevNodes.getAliasMatcherMap( i );

            if (aliasMatcherMap!=null)
                aliasMatcherMap = AliasMatcherMap.createMap( prevNodes.getAliasMatcherMap( i ), alias,
                                                     matcherNode );

            if (last) {
                matcherNode.setAcceptEnding( aliasMatcherMap );
            }
            

            ends.add( matcherNode, aliasMatcherMap );
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
