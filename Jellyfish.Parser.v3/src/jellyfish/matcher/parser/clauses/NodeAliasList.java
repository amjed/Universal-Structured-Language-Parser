/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.parser.clauses;

import java.util.ArrayList;
import java.util.List;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.nodes.MatcherNode;

/**
 *
 * @author Xevia
 */
public class NodeAliasList  {

    private List<MatcherNode> matcherNodeList;
    private List<AliasMatcherMap> aliasMatcherMapList;

    public NodeAliasList(int size) {
        matcherNodeList = new ArrayList<MatcherNode>(size);
        aliasMatcherMapList = new ArrayList<AliasMatcherMap>(size);
    }

    public int size() {
        return matcherNodeList.size();
    }
    
    public boolean isEmpty() {
        return matcherNodeList.isEmpty();
    }

    public MatcherNode getMatcherNode( int index ) {
        return matcherNodeList.get( index );
    }
    
    public AliasMatcherMap getAliasMatcherMap( int index ) {
        return aliasMatcherMapList.get( index );
    }
    
    public void clear() {
        matcherNodeList.clear();
        aliasMatcherMapList.clear();
    }

    public void addAll( NodeAliasList nodeAliasList ) {
        matcherNodeList.addAll( nodeAliasList.matcherNodeList );
        aliasMatcherMapList.addAll( nodeAliasList.aliasMatcherMapList );
    }
    
    public void add( MatcherNode matcherNode, AliasMatcherMap aliasMatcherMap ) {
        matcherNodeList.add( matcherNode );
        aliasMatcherMapList.add( aliasMatcherMap );
    }

    @Override
    public String toString() {
        return "nodeList=" + matcherNodeList + ", mapList=" +
               aliasMatcherMapList;
    }




}
