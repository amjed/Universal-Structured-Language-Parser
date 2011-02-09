/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.parser.clauses;

import jellyfish.common.PatternExtractor;

/**
 *
 * @author Umran
 */
public class ClauseAlias
{

    private static final PatternExtractor ALIAS_EXTRACTOR = new PatternExtractor(
            "([^\\[])\\[([0-9]+)\\]");

//    public static final String ROOT_ALIAS_NAME  = "ROOT";
    public static ClauseAlias createRoot(String rootName) {
        return new ClauseAlias("/"+rootName, 0)
        {

            @Override
            public boolean isRoot() {
                return true;
            }
        };
    }

    public static ClauseAlias createEmpty() {
        return new ClauseAlias("EMPTY", 1)
        {

            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    public static ClauseAlias createSysInc() {
        return new ClauseAlias("SYS_INC", 2)
        {

            @Override
            public boolean isSysInc() {
                return true;
            }
        };
    }
    private static int hashCodeIncr = 3;
    private int hashCode;
    private String name;

    private ClauseAlias( String name, int hashCode ) {
        this.hashCode = hashCode;
        this.name = name;
    }

    public ClauseAlias( String name ) {
        this(name, hashCodeIncr++);
    }

    public boolean isRoot() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isSysInc() {
        return false;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "\""+name+"\"";
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final ClauseAlias other = (ClauseAlias) obj;
        if ( this.hashCode != other.hashCode ) {
            return false;
        }
        return true;
    }

    @Override
    final public int hashCode() {
        return this.hashCode;
    }

}
