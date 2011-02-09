/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.prolog;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Var;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Xevia
 */
public interface PrologEngine {

    public PrologResults query(String question) throws MalformedGoalException, NoSolutionException;
    

}
