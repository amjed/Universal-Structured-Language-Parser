/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.clauses;

/**
 *
 * @author Umran
 */
public abstract class CompositeClause extends Clause {

    public CompositeClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
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
