package jellyfish.matcher.nodes;

import java.util.*;
import java.util.regex.Pattern;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public class InputMatcherNode extends MatcherNode
{

    protected String regex;
    protected String outputTxt;
    protected Pattern regexPattern;

    public InputMatcherNode( String regex ) {
	super();
	this.regex = regex;
	this.regexPattern = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
	this.outputTxt = "<"+regex+">";
    }

    public String getRegex() {
	return regex;
    }

    public Pattern getRegexPattern() {
	return regexPattern;
    }

    @Override
    public String toString() {
	return outputTxt + super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
			  List<MatchResult> finalResults, boolean matchAtEnd ) {
	if ( input.get( inputIndex ).isWildCard() ) {
	    super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
	} else {
	    String inputValue = input.get( inputIndex ).getStringValue();
	    
	    if ( regexPattern.matcher( inputValue ).find() ) {
		super.match( input, inputIndex,
			     new MatchResult( aliasTreeNode,
					      this,
					      prevMatch,
					      inputIndex,
					      outputTxt,
					      inputValue ),
			     finalResults, matchAtEnd );
	    }
	}
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
					   MatchResult prevMatch, List<MatchResult> finalResults ) {
	finalResults.add( new MatchResult( aliasTreeNode,
					   this,
					   prevMatch,
					   inputIndex,
					   outputTxt,
					   null ) );
    }
    
    protected int compareFunctionality( MatcherNode o ) {
	if ( o == null ) {
	    return -1;
	}
	int compClass = (o.getClass().getCanonicalName().compareTo(
			 this.getClass().getCanonicalName() ));
	if ( compClass == 0 ) {
	    InputMatcherNode inputNode = (InputMatcherNode) o;
	    return this.regex.compareTo( inputNode.regex );
	} else {
	    return compClass;
	}
    }
}
