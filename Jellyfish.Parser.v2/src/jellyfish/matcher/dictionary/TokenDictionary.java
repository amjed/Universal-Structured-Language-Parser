package jellyfish.matcher.dictionary;

import jellyfish.common.CaseInsensitiveStringComparator;
import java.util.*;

public class TokenDictionary {

    private int lastId = 0;
    private Map<String, DictionaryEntry> wordEntryMap;
    private Map<Integer, DictionaryEntry> idEntryMap;

    public TokenDictionary() {
        this.wordEntryMap = new TreeMap<String, DictionaryEntry>(new CaseInsensitiveStringComparator());
        this.idEntryMap = new TreeMap<Integer, DictionaryEntry>();
    }

    public void optimize() {
        Map<String, DictionaryEntry> newWordEntryMap = new HashMap<String, DictionaryEntry>(this.wordEntryMap.size());
        for (DictionaryEntry entry : this.wordEntryMap.values()) {
            newWordEntryMap.put(entry.getWord().toLowerCase(), entry);
        }
        Map<Integer, DictionaryEntry> newIdEntryMap = new HashMap<Integer, DictionaryEntry>(this.idEntryMap.size());
        for (DictionaryEntry entry : this.idEntryMap.values()) {
            newIdEntryMap.put(entry.getId(), entry);
        }
        this.wordEntryMap = newWordEntryMap;
        this.idEntryMap = newIdEntryMap;
    }

    public void clear() {
        wordEntryMap.clear();
        idEntryMap.clear();
    }

    public DictionaryEntry registerEntry(String word) {
        assert word!=null : "word trying to be registered in dictionary is null!";
        word = word.toLowerCase();
        if (this.wordEntryMap.containsKey(word)) {
            return this.wordEntryMap.get(word);
        } else {
            DictionaryEntry entry = new DictionaryEntry(lastId++, word);
            idEntryMap.put(entry.getId(), entry);
            wordEntryMap.put(entry.getWord(), entry);
            return entry;
        }
    }

    public DictionaryEntry getEntry(Integer id) {
        return idEntryMap.get(id);
    }

    public DictionaryEntry getEntry(String word) {
        return wordEntryMap.get(word.toLowerCase());
    }

    public Collection<DictionaryEntry> getAllEntries() {
        return Collections.unmodifiableCollection( wordEntryMap.values() );
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        for (DictionaryEntry entry : idEntryMap.values()) {
            bld.append(entry).append("\n");
        }
        return bld.toString();
    }
}
