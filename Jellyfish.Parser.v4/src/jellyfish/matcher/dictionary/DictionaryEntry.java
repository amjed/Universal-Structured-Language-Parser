package jellyfish.matcher.dictionary;

public class DictionaryEntry {

    private int id;
    private String word;

    public DictionaryEntry(int id, String word) {
        this.id = id;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return word;
    }
}
