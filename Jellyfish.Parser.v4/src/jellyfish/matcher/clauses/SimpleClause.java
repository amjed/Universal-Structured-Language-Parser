/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import java.io.PrintStream;
import java.io.PrintWriter;
import jellyfish.common.Common;

/**
 *
 * @author Umran
 */
public abstract class SimpleClause extends Clause {

    public SimpleClause( String xmlLocation, String name )
    {
        super(xmlLocation, name);
    }


    @Override
    public final ClauseType getClauseType() {
        return ClauseType.SIMPLE_CLAUSE;
    }

    public boolean containsClause( Clause clause ) {
	return this==clause;
    }

    public abstract SimpleClauseType getSimpleClauseType();

//    public List<NameTree> getAliases() {
//        return Collections.EMPTY_LIST;
//    }

    public void printClauseTree(PrintStream printStream, int depth) {
        printStream.println( Common.getTabs( depth ) + " - " + toString() );
    }

}
