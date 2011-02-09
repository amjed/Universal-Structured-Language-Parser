package jellyfish.matcher.nodes;

import java.util.List;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.dictionary.*;
import jellyfish.matcher.input.*;

public class ConstantMatcherNode
        extends MatcherNode
{

    protected TokenDictionary dictionary;
    protected DictionaryEntry dictionaryEntry;
    public static int constantMatcherNodeCounter = 0;

    public ConstantMatcherNode( TokenDictionary dictionary, String word ) {
        super();
        ++constantMatcherNodeCounter;
        this.dictionary = dictionary;
        this.dictionaryEntry = dictionary.registerEntry( word );
    }

    public DictionaryEntry getDictionaryEntry() {
        return dictionaryEntry;
    }

    public TokenDictionary getDictionary() {
        return dictionary;
    }

    @Override
    public String toString() {
        return "'" + dictionaryEntry.toString() + "'" + super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> output, boolean matchAtEnd ) {
        InputToken token = input.get( inputIndex );
        if ( token.isWildCard() ) {
            super.match( input, inputIndex, prevMatch, output, matchAtEnd );
        } else {
            if ( token.isInDictionary() &&
                 token.getDictionaryEntryId() == this.getDictionaryEntry().getId() ) {
                super.match( input, inputIndex,
                             new MatchResult( this, prevMatch, inputIndex, dictionaryEntry.getWord(),
                                              aliasMatcherMap ),
                             output,
                             matchAtEnd );
            }
        }
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> output ) {
        output.add( new MatchResult( this, prevMatch, inputIndex, dictionaryEntry.getWord(), aliasMatcherMap ) );
    }

    protected int compareFunctionality( MatcherNode o ) {
        if ( o == null ) {
            return -1;
        }
        int compClass = (o.getClass().getCanonicalName().compareTo(
                         this.getClass().getCanonicalName() ));
        if ( compClass == 0 ) {
            ConstantMatcherNode constNode = (ConstantMatcherNode) o;
            return this.dictionaryEntry.getId() - constNode.dictionaryEntry.getId();
        } else {
            return compClass;
        }
    }
}
