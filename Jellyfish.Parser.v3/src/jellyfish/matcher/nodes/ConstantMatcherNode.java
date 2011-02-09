package jellyfish.matcher.nodes;

import java.util.List;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.dictionary.*;
import jellyfish.matcher.input.*;

public class ConstantMatcherNode
        extends MatcherNode
{

    protected TokenDictionary dictionary;
    protected DictionaryEntry dictionaryEntry;
    protected AliasMatcherMap aliasMatcherMap;

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

    public AliasMatcherMap getAliasMatcherMap() {
        return aliasMatcherMap;
    }

    public void setAcceptEnding( AliasMatcherMap aliasMatcherMap ) {
	assert(aliasMatcherMap!=null);
        super.setAcceptEnding();
	this.aliasMatcherMap = aliasMatcherMap;
    }
    
    @Override
    public String toString() {
        return "'" + dictionaryEntry.toString() + "'" + super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> finalResults, boolean matchAtEnd ) {
        InputToken token = input.get( inputIndex );
        if ( token.isWildCard() ) {
            super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
        } else {
            if ( token.isInDictionary() &&
                 token.getDictionaryEntryId() == this.getDictionaryEntry().getId() ) {
                super.match( input, inputIndex,
                             new MatchResult( this, prevMatch, inputIndex, dictionaryEntry.getWord(),
                                              aliasMatcherMap ),
                             finalResults,
                             matchAtEnd );
            }
        }
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> finalResults ) {
        finalResults.add( new MatchResult( this, prevMatch, inputIndex, dictionaryEntry.getWord(), aliasMatcherMap ) );
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
