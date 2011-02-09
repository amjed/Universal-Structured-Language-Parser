/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasTreeNode;
import java.io.PrintStream;

/**
 *
 * @author Umran
 */
public abstract class Clause
{

    public static enum ClauseType
    {

        COMPOSITE_CLAUSE( "Composite Clause" ),
        SIMPLE_CLAUSE( "Simple Clause" );
        String descr;

        ClauseType( String descr ) {
            this.descr = descr;
        }

        @Override
        public String toString() {
            return descr;
        }
    };

    public static enum SimpleClauseType
    {

        CONSTANT_CLAUSE( "Constant Clause" ),
        VARIABLE_CLAUSE( "Variable Clause" ),
        INPUT_CLAUSE( "Input Clause" );
        String descr;

        SimpleClauseType( String descr ) {
            this.descr = descr;
        }

        @Override
        public String toString() {
            return descr;
        }
    };

    public static enum CompositeClauseType
    {

        AND_CLAUSE( "AND Clause" ),
        OR_CLAUSE( "OR Clause" );
        String descr;

        CompositeClauseType( String descr ) {
            this.descr = descr;
        }

        @Override
        public String toString() {
            return descr;
        }
    };
    protected String xmlLocation;
    protected String name;

    public Clause( String xmlLocation, String name ) {
        this.xmlLocation = xmlLocation;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getXmlLocation() {
        return xmlLocation;
    }

    public abstract ClauseType getClauseType();

//    protected abstract void altBuildMatchTree( AliasTreeNode alias,  );

    protected abstract void buildMatchTree( AliasTreeNode alias, NodeAliasList prevNodes,
                                            NodeAliasList ends );

    @Override
    public String toString() {
        return "AbstractClause{" + "name=" + name + '}';
    }

    public abstract void printClauseTree(PrintStream printStream, int depth);
    
}
