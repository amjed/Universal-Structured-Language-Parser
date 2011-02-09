/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.io.PrintStream;
import java.util.*;
import java.util.ArrayList;
import jellyfish.common.Common;
import jellyfish.matcher.nodes.MatcherNode;

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
public final class OrClause extends CompositeClause {

    private class SubclauseHolder {
        public ClauseAlias alias;
        public Clause subclause;

        public SubclauseHolder(ClauseAlias alias, Clause subclause) {
            this.alias = alias;
            this.subclause = subclause;
        }

        @Override
        public String toString() {
            return "{"+alias.getName()+"=" + subclause.toString() + "}";
        }
    }
    
    private List<SubclauseHolder> subclauses = new ArrayList<SubclauseHolder>();

    public OrClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
    }

    @Override
    public void addSubClause(Clause subClause, ClauseAlias alias, boolean optional, int maxCardinality) {
        subclauses.add(new SubclauseHolder(alias, subClause));
    }

    @Override
    public void replaceClause( Clause current,
                               Clause replacement ) {
        int index = -1;
        for (int i=0; i<subclauses.size(); ++i)
            if (subclauses.get( i ).subclause.equals( current )) {
                index = i;
                break;
            }
        if (index>=0) {
            subclauses.get( index ).subclause = replacement;
        }
    }
    
    @Override
    public CompositeClauseType getCompositeClauseType() {
        return CompositeClauseType.OR_CLAUSE;
    }

    protected void buildMatchTree(AliasTreeNode parentAlias, NodeAliasList prevNodes, NodeAliasList ends) {
        NodeAliasList subNextNodes = new NodeAliasList(subclauses.size());
        
        for (SubclauseHolder holder:subclauses) {
            AliasTreeNode aliasTreeNode = computeChildAlias( parentAlias, holder.alias );

            subNextNodes.clear();
            holder.subclause.buildMatchTree( aliasTreeNode, prevNodes, subNextNodes );
            ends.addAll( subNextNodes );
        }
    }
    
    @Override
    public String toString() {
        return "OrClause{\"" + name + "\":" + subclauses.toString() + "}" ;
    }

    public void printClauseTree(PrintStream printStream, int depth) {
        for (SubclauseHolder holder:subclauses) {
            printStream.print( Common.getTabs( depth ) );
            printStream.println( "OR>>" + holder.alias  );
            holder.subclause.printClauseTree( printStream, depth+1 );
        }
    }
}
