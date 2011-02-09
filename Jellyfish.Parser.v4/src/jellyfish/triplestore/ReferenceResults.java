/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

import java.util.List;

/**
 *
 * @author Xevia
 */
public interface ReferenceResults {

    List<String[]> getResults();

    Integer getVariableIndex( String var );

    String[] getVariables();

}
