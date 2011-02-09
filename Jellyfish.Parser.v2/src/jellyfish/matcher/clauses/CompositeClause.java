/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;

/**
 *
 * @author Umran
 */
public abstract class CompositeClause extends Clause {

    public CompositeClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
    }

    protected AliasTreeNode computeChildAlias( AliasTreeNode alias,
                                               ClauseAlias subClauseAlias ) {
        AliasTreeNode aliasTreeNode = null;

        if ( alias != null && subClauseAlias != null ) {
            if ( subClauseAlias.isEmpty() ) {
                // leave as null...
            } else if ( subClauseAlias.isSysInc() ) {
                aliasTreeNode = alias;
            } else {
                aliasTreeNode = new AliasTreeNode( subClauseAlias.getName() );
                aliasTreeNode.setParent( alias );
            }
        }

        return aliasTreeNode;
    }
    
    public abstract void addSubClause( Clause subClause, ClauseAlias alias,
                                       boolean optional, int maxCardinality );

    //  for use with referenced clauses - because the referenced clauses have
    //      to be filled in after the whole xml is loaded.
    public abstract void replaceClause( Clause current,
                                        Clause replacement );

    @Override
    public final ClauseType getClauseType() {
        return ClauseType.COMPOSITE_CLAUSE;
    }

    public abstract CompositeClauseType getCompositeClauseType();

}
