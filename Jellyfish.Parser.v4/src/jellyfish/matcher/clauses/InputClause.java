/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.VariableContext;
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

    protected void buildMatchTree( AliasTreeNode alias, VariableContext variableContext,
				   NodeAliasList prevNodes, NodeAliasList ends, boolean last ) {
	for ( int i = 0; i < prevNodes.size(); ++i ) {
	    MatcherNode prevNode = prevNodes.getMatcherNode( i );

	    InputMatcherNode matcherNode = prevNode.registerNextNode( new InputMatcherNode( regex ) );
	    matcherNode.setAliasTreeNode( alias );

	    if ( last ) {
		matcherNode.setAcceptEnding();
	    }


	    ends.add( matcherNode );
	}
    }
    
}
