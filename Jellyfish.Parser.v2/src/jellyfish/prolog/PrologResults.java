/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.prolog;

import java.util.List;

/**
 *
 * @author Xevia
 */
public interface PrologResults {

    List<String[]> getResults();

    Integer getVariableIndex( String var );

    String[] getVariables();

}
