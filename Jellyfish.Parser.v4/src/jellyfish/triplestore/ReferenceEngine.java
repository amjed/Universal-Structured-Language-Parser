/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

/**
 *
 * @author Xevia
 */
public interface ReferenceEngine {

    ReferenceResults query(String question) throws Exception;
	long getLatestUpdate();

}
