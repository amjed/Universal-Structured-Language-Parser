/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher.nodes;

import java.util.List;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.InputTokenList;

/**
 *
 * @author Xevia
 */
public abstract class SimpleMatcherNode
	extends MatcherNode
{
    @Override
    public void setAcceptEnding( ) {
	super.setAcceptEnding();
    }

    
    protected void callSuperMatch( InputTokenList input, int inputIndex,
				   MatchResult prevMatch, List<MatchResult> finalResults,
				   boolean matchAtEnd, String matchedString, Object machedValue ) {
	if ( isEnding( input, inputIndex, matchAtEnd ) ) {
	    super.match( input, inputIndex,
			 new MatchResult( aliasTreeNode,
					  this,
					  prevMatch,
					  inputIndex,
					  matchedString,
					  machedValue ),
			 finalResults, matchAtEnd );
	} else {
	    super.match( input, inputIndex,
			 new MatchResult( aliasTreeNode,
					  this,
					  prevMatch,
					  inputIndex,
					  matchedString,
					  machedValue ),
			 finalResults, matchAtEnd );
	}
    }
    
}
