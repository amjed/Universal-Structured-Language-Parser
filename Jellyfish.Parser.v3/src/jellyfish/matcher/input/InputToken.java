package jellyfish.matcher.input;

import java.util.*;
import jellyfish.matcher.dictionary.DictionaryEntry;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.tokenizer.Tokenizer;

public abstract class InputToken {

    public static final String WILDCARD = "*";

    public abstract boolean isWildCard();
    
    public abstract boolean isLastWildCard();

    public abstract boolean isInDictionary();

    public abstract String getStringValue();

    public abstract Integer getDictionaryEntryId();

    public static void addInputToken(InputTokenList outputList, TokenDictionary dictionary, String input)
    {
        if (input.equals(WILDCARD)) {
            if (outputList.isEmpty() || !outputList.get(outputList.size()-1).isWildCard()) {
                outputList.add(new WildCardToken(true));
            }
        } else {
            if (!outputList.isEmpty() && outputList.get(outputList.size()-1).isWildCard()) {
                ((WildCardToken)outputList.get(outputList.size()-1)).setLastWildCard(false);
            }

            DictionaryEntry entry = dictionary.getEntry(input);
            if (entry==null) {
                outputList.add(new StringValueToken(input));
            } else {
                outputList.add(new DictionaryEntryToken(dictionary, entry.getId()));
            }
        }
    }

    public static void parseInputLine(InputTokenList outputList, TokenDictionary dictionary, Tokenizer tokenizer, String inputLine)
    {
        List<String> inputTokenStrList = new ArrayList<String>();
        tokenizer.tokenize( inputTokenStrList, inputLine );
        
        outputList.clear();
        for (String inputToken:inputTokenStrList) {
            InputToken.addInputToken( outputList, dictionary, inputToken );
        }
    }

    private static class DictionaryEntryToken extends InputToken {

        private TokenDictionary dictionary;
        private int dictionaryEntryId;
        private String dictionaryValue;

        public DictionaryEntryToken(TokenDictionary dictionary, int dictionaryEntryId) {
            this.dictionary = dictionary;
            this.dictionaryEntryId = dictionaryEntryId;
            this.dictionaryValue = dictionary.getEntry(dictionaryEntryId).getWord();
        }

        @Override
        public boolean isWildCard() {
            return false;
        }

        @Override
        public boolean isLastWildCard() {
            return false;
        }

        @Override
        public boolean isInDictionary() {
            return true;
        }

        @Override
        public String getStringValue() {
            return dictionaryValue;
        }

        @Override
        public Integer getDictionaryEntryId() {
            return dictionaryEntryId;
        }

        @Override
        public String toString() {
            return "'" + dictionaryValue + "'";
        }
        
    }

    private static class StringValueToken extends InputToken {

        private String value;

        public StringValueToken(String value) {
            this.value = value;
        }

        @Override
        public boolean isWildCard() {
            return false;
        }

        @Override
        public boolean isLastWildCard() {
            return false;
        }

        @Override
        public boolean isInDictionary() {
            return false;
        }

        @Override
        public String getStringValue() {
            return value;
        }

        @Override
        public Integer getDictionaryEntryId() {
            return null;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }
        
    }

    private static class WildCardToken extends InputToken {

        private boolean lastWildCard;

        public WildCardToken(boolean lastWildCard) {
            this.lastWildCard = lastWildCard;
        }

        @Override
        public boolean isWildCard() {
            return true;
        }

        @Override
        public boolean isLastWildCard() {
            return lastWildCard;
        }

        public void setLastWildCard(boolean lastWildCard) {
            this.lastWildCard = lastWildCard;
        }
        
        @Override
        public boolean isInDictionary() {
            return false;
        }

        @Override
        public String getStringValue() {
            return "*";
        }

        @Override
        public Integer getDictionaryEntryId() {
            return null;
        }

        @Override
        public String toString() {
            if (this.lastWildCard)
                return "$";
            else
                return "*";
        }

    }

}
