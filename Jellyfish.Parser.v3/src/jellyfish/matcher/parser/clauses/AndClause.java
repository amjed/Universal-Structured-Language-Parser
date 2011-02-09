/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.parser.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.io.PrintStream;
import java.util.*;
import java.util.ArrayList;
import jellyfish.common.Common;

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
    private String onMatchFunctionName = "";

    public AndClause( String xmlLocation, String name, boolean primaryClause ) {
        super(xmlLocation, name);
        this.primaryClause = primaryClause;
    }

    public AndClause( String xmlLocation, String name ) {
        this(xmlLocation, name, false);
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

    @Override
    public CompositeClauseType getCompositeClauseType() {
        return CompositeClauseType.AND_CLAUSE;
    }

    public synchronized void addSubClause( Clause subClause, ClauseAlias alias, boolean optional,
                                           int maxCardinality ) {
        if ( maxCardinality < 1 ) {
            throw new RuntimeException(
                    "Invalid maximum cardinality for subclause alias='" + alias + "' of clause '" +
                    getName() + "'");
        }

        SubclauseHolder holder = new SubclauseHolder(alias, subClause, optional, maxCardinality);
        subclauses.add(holder);
    }

    @Override
    public void replaceClause( Clause current,
                               Clause replacement ) {
        int index = -1;
        for ( int i = 0; i < subclauses.size(); ++i ) {
            if ( subclauses.get(i).subclause.equals(current) ) {
                index = i;
                break;
            }
        }
        if ( index >= 0 ) {
            subclauses.get(index).subclause = replacement;
        }
    }

    private static class NodeAliasListState {
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
    
    public void buildMatchTree( AliasTreeNode alias, NodeAliasList prevNodes,
                                NodeAliasList ends, boolean last ) {
        boolean nonOptional = false;
        for ( SubclauseHolder holder : subclauses ) {
            if ( !holder.optional ) {
                nonOptional = true;
                break;
            }
        }

        if ( !nonOptional ) {
            throw new RuntimeException(
                    "No compulsory element found in the clause '" + name + "' location: " +
                    getXmlLocation() );
        }

        NodeAliasListState lists = new NodeAliasListState( subclauses.size() );

        lists.myPrevNodes.clear();
        lists.myPrevNodes.addAll( prevNodes );

        for (int k=0; k<subclauses.size(); ++k) {

            SubclauseHolder holder = subclauses.get( k );
            boolean isLastHolder = k==subclauses.size()-1;
            
            AliasTreeNode subClauseAlias = computeChildAlias( alias, holder.alias );

            lists.myNextNodes.clear();

            if ( holder.optional ) {
                lists.myNextNodes.addAll( lists.myPrevNodes );
            }

            lists.subPrevNodes.clear();
            lists.subPrevNodes.addAll( lists.myPrevNodes );
            for ( int i = 1; i <= holder.maxCardinality; ++i ) {
                lists.subNextNodes.clear();
                holder.subclause.buildMatchTree( subClauseAlias, lists.subPrevNodes,
                                                 lists.subNextNodes, last && isLastHolder );
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
    
    public void printClauseTree(PrintStream printStream, int depth) {
        for (SubclauseHolder holder:subclauses) {
            printStream.print( Common.getTabs( depth ) );
            printStream.println( "AND>>" + holder.alias + (holder.optional?": [op] :":" : ") + holder.maxCardinality );
            holder.subclause.printClauseTree( printStream, depth+1 );
        }
    }
}
