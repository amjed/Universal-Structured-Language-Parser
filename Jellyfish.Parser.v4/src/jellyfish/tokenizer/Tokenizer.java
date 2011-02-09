/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.tokenizer;

import java.util.List;

/**
 *
 * @author Umran
 */
public interface Tokenizer {
    
    public void tokenize(List<String> outputList, String input);
    public String combine(List<String> tokens);

}
