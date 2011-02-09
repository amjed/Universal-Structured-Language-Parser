/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jellyfish.common.Pair;
import jellyfish.matcher.nodes.MatcherNode;

/**
 *
 * @author Xevia
 */
public class AliasMatcherMap
{

    public static AliasMatcherMap createMap( AliasTreeNode aliasTreeNode, MatcherNode matcherNode ) {
        AliasMatcherMap aliasMatcherMap = new AliasMatcherMap( aliasTreeNode.getRoot() );
        aliasMatcherMap.put( aliasTreeNode, matcherNode );
        return aliasMatcherMap;
    }

    public static AliasMatcherMap createMap( AliasMatcherMap prevMap, AliasTreeNode aliasTreeNode,
                                             MatcherNode matcherNode ) {
        AliasMatcherMap aliasMatcherMap = new AliasMatcherMap( prevMap );
        aliasMatcherMap.put( aliasTreeNode, matcherNode );
        return aliasMatcherMap;
    }

    public static AliasMatcherMap createMap( AliasMatcherMap prevMap, AliasMatcherMap secondaryMap ) {
        AliasMatcherMap aliasMatcherMap = new AliasMatcherMap( prevMap );
        aliasMatcherMap.addAll( secondaryMap );
        return aliasMatcherMap;
    }
    
    private AliasTreeNode rootNode;
    private List<Pair<AliasTreeNode, MatcherNode>> list;
    public static int aliasMatcherMapCounter = 0;

    public AliasMatcherMap( AliasTreeNode rootNode ) {
        ++aliasMatcherMapCounter;
        this.rootNode = rootNode;
        this.list = new ArrayList<Pair<AliasTreeNode, MatcherNode>>();
    }

    public AliasMatcherMap( AliasMatcherMap map ) {
        ++aliasMatcherMapCounter;
        this.rootNode = map.rootNode;
        this.list = new ArrayList<Pair<AliasTreeNode, MatcherNode>>( map.list );
    }

    public int size() {
        return list.size();
    }

    public AliasTreeNode getRootNode() {
        return rootNode;
    }

    public void clear() {
        list.clear();
    }

    public void put( AliasTreeNode alias, MatcherNode matcher ) {
        if ( alias == null || matcher == null ) {
            System.out.println(
                    "WARNING: Either NULL alias or NULL matcher inserted into Alias-Matcher-Map." );
            return;
        }

        if ( alias.getRoot() != this.rootNode ) {
            throw new RuntimeException(
                    "Child alias '" + alias + "' is not part of the alias tree '" +
                    rootNode + "'" );
        }

        list.add( new Pair<AliasTreeNode, MatcherNode>( alias, matcher ) );
    }

    public void addAll( AliasMatcherMap map ) {
        list.addAll( map.list );
    }

    public AliasMatcherIterator getIterator() {
        return new AliasMatcherIterator( list.iterator() );
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public static class AliasMatcherIterator
    {

        private Iterator<Pair<AliasTreeNode, MatcherNode>> iterator;
        private Pair<AliasTreeNode, MatcherNode> currentPair = null;

        private AliasMatcherIterator( Iterator<Pair<AliasTreeNode, MatcherNode>> iterator ) {
            this.iterator = iterator;
        }

        public Pair<AliasTreeNode, MatcherNode> next() {
            return currentPair = iterator.next();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public MatcherNode getMatcher() {
            return currentPair.getSecond();
        }

        public AliasTreeNode getAlias() {
            return currentPair.getFirst();
        }
    }
}
