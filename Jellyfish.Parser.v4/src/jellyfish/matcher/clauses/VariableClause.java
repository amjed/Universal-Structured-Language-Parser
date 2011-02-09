/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.VariableContext;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.nodes.RootMatcherNode;
import jellyfish.matcher.nodes.VariableMatcherNode;

/**
 *
 * @author Umran
 */
public class VariableClause extends SimpleClause {

    public VariableClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
	if (name==null || name.isEmpty()) {
	    throw new RuntimeException( "Variable at location "+xmlLocation+" has no name." );
	}
    }

    @Override
    public SimpleClauseType getSimpleClauseType() {
        return SimpleClauseType.VARIABLE_CLAUSE;
    }

    @Override
    public String toString() {
        return "var("+name+")";
    }
    
    protected void buildMatchTree(AliasTreeNode alias, VariableContext variableContext, NodeAliasList prevNodes, NodeAliasList ends, boolean last) {
        for (int i=0; i<prevNodes.size(); ++i) {
            MatcherNode prevNode = prevNodes.getMatcherNode( i );

            VariableMatcherNode matcherNode = new VariableMatcherNode( variableContext.getClauseBase().getTokenizer(), this.name );
            matcherNode = prevNode.registerNextNode( matcherNode );
	    matcherNode.setAliasTreeNode( alias );

	    /*
	     * When this function buildMatchTree is being called, the alias-tree will still be
	     *	under construction, hence the refence query defined in any parent AND-clause
	     *	would be unprocessed yet. In order to create this variable, we register
	     *	the variable's current alias with the variable context, obtain the root matcher node
	     *	which will be filled once the context is created and the query is processed.
	     */
	    System.out.println( "Registering alias: "+alias );
	    RootMatcherNode rootMatcherNode = variableContext.registerAlias( alias );
	    if (rootMatcherNode==null) {
		throw new RuntimeException( "The variable "+this+" defined at "+xmlLocation+" isn't defined in any parent query.");
	    }
            matcherNode.setParseTree( rootMatcherNode );

	    ends.add( matcherNode );

            if (last) {
                matcherNode.setAcceptEnding();
            }
        }
    }

}
