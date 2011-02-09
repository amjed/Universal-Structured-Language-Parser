package jellyfish.matcher.nodes;

import java.util.*;
import java.util.regex.Pattern;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public class InputMatcherNode
        extends MatcherNode
{

    protected String regex;
    protected Pattern regexPattern;

    public InputMatcherNode( String regex ) {
        super();
        this.regex = regex;
        this.regexPattern = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
    }

    public String getRegex() {
        return regex;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    @Override
    public String toString() {
        return "<" + regex + ">" + super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> output, boolean matchAtEnd ) {
        if ( input.get( inputIndex ).isWildCard() ) {
            super.match( input, inputIndex, prevMatch, output, matchAtEnd );
        } else {
            String inputValue = input.get( inputIndex ).getStringValue();
            if ( regexPattern.matcher( inputValue ).find() ) {
                super.match( input, inputIndex,
                             new MatchResult( this, prevMatch, inputIndex, inputValue,
                                              aliasMatcherMap ),
                             output, matchAtEnd );
            }
        }
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> output ) {
        output.add( 
                new MatchResult( this, prevMatch, inputIndex, "<" + regex + ">", aliasMatcherMap )
                );
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
