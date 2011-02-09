package jellyfish.matcher;

import java.util.Comparator;
import jellyfish.matcher.nodes.MatcherNode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MatchResult
{

    private MatcherNode matchingNode;
    private MatchResult previousMatch;
    private int inputMatchIndex;
    private Object matchValue;
    private AliasMatcherMap aliasMatcherMap;

    public MatchResult( MatcherNode matchingNode, MatchResult previousMatch, int inputMatchIndex,
                        Object matchValue, AliasMatcherMap aliasMatcherMap ) {
        this.matchingNode = matchingNode;
        this.previousMatch = previousMatch;
        this.inputMatchIndex = inputMatchIndex;
        this.matchValue = matchValue;
        this.aliasMatcherMap = aliasMatcherMap;
    }
    
    public MatchResult getPreviousMatch() {
        return previousMatch;
    }

    public void setPreviousMatch( MatchResult previousMatch ) {
        this.previousMatch = previousMatch;
    }

    public AliasMatcherMap getAliasMatcherMap() {
        return aliasMatcherMap;
    }

    public void setAliasMatcherMap( AliasMatcherMap aliasMatcherMap ) {
        this.aliasMatcherMap = aliasMatcherMap;
    }
    
    public MatchResult getFirstMatch() {
        if (previousMatch!=null)
            return previousMatch.getFirstMatch();
        else
            return this;
    }

    public int getInputMatchIndex() {
        return inputMatchIndex;
    }

    public Object getMatchValue() {
        return matchValue;
    }

    public MatcherNode getMatchingNode() {
        return matchingNode;
    }

    private void compileResults( Map<MatcherNode,Object> results )
    {
        System.out.println( "\tresult: "+matchingNode+" = "+matchValue+" , prev="+(previousMatch!=null?previousMatch.matchingNode:"null") );
        results.put( matchingNode, matchValue );
        if (previousMatch!=null)
            previousMatch.compileResults( results );
    }
    
    public void fillValues( List<Object> outputList ) {
        if ( previousMatch == null ) {
            outputList.clear();
        } else {
            previousMatch.fillValues(outputList);
        }
        outputList.add(this.matchValue);
    }

    public void fillValues( StorageTable<Object> storageTable ) {

        storageTable.clear();

//        AliasMatcherMap aliasMatcherMap = matchingNode.getAliasMatcherMap();
        if (aliasMatcherMap!=null) {
            Map<MatcherNode,Object> compiledResults = new TreeMap<MatcherNode, Object>(
                new Comparator<MatcherNode>() {
                    public int compare( MatcherNode o1, MatcherNode o2 ) {
                        return o1.hashCode()-o2.hashCode();
                    }
                }
            );

            compileResults( compiledResults );

            System.out.println( "compiled results = "+compiledResults );

            AliasMatcherMap.AliasMatcherIterator it = aliasMatcherMap.getIterator();
            while (it.hasNext()) {
                it.next();
                System.out.println( "filling \""+it.getAlias()+"\"="+it.getMatcher()+"="+compiledResults.get( it.getMatcher() ) );
                storageTable.put( it.getAlias(), compiledResults.get( it.getMatcher() ) );
            }
        }
    }

    @Override
    public String toString() {
        return "{(" + inputMatchIndex + ")" + matchValue + "}";
    }
}
