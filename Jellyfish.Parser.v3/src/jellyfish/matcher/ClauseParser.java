/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher;

import java.util.Set;
import jellyfish.matcher.parser.clauses.AndClause;

/**
 *
 * @author Umran
 */
public interface ClauseParser {

    public AndClause getNamedClause(String name);
    public Set<String> getClauseNames();

}
