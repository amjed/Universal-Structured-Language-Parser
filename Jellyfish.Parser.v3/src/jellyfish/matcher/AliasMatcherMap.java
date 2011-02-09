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
public final class AliasMatcherMap
{

    public static AliasMatcherMap createMap( AliasTreeNode aliasTreeNode, MatcherNode matcherNode ) {
        AliasMatcherMap aliasMatcherMap = createMap( aliasTreeNode.getRoot() );
        aliasMatcherMap.put( aliasTreeNode, matcherNode );
        return aliasMatcherMap;
    }

    public static AliasMatcherMap createMap( AliasMatcherMap prevMap, AliasTreeNode aliasTreeNode,
                                             MatcherNode matcherNode ) {
        AliasMatcherMap aliasMatcherMap = createMap( prevMap );
        aliasMatcherMap.put( aliasTreeNode, matcherNode );
        return aliasMatcherMap;
    }

    public static AliasMatcherMap createMap( AliasMatcherMap prevMap, AliasMatcherMap secondaryMap ) {
        AliasMatcherMap aliasMatcherMap = createMap( prevMap );
        for (Pair<AliasTreeNode, MatcherNode> x:secondaryMap.list)
            aliasMatcherMap.put( x.getFirst(), x.getSecond() );
        return aliasMatcherMap;
    }

    public static AliasMatcherMap createMap( AliasTreeNode rootNode ) {
	return new AliasMatcherMap( rootNode, 10 );
    }
    
    public static AliasMatcherMap createMap( AliasMatcherMap map ) {
	AliasMatcherMap aliasMatcherMap = new AliasMatcherMap( map.rootNode, map.list.size() );
        for (Pair<AliasTreeNode, MatcherNode> x:map.list)
            aliasMatcherMap.put( x.getFirst(), x.getSecond() );
	return aliasMatcherMap;
    }
//
//    private static class AliasMatcherNode {
//	AliasMatcherNode prev;
//	AliasTreeNode aliasTreeNode;
//	MatcherNode matcherNode;
//
//	public AliasMatcherNode( AliasMatcherNode prev, AliasTreeNode aliasTreeNode,
//				 MatcherNode matcherNode ) {
//	    this.prev = prev;
//	    this.aliasTreeNode = aliasTreeNode;
//	    this.matcherNode = matcherNode;
//	}
//
//    }
    
    private AliasTreeNode rootNode;
    private List<Pair<AliasTreeNode, MatcherNode>> list;
    public static int aliasMatcherMapCounter = 0;

    private AliasMatcherMap( AliasTreeNode rootNode, int listInitialSize ) {
        ++aliasMatcherMapCounter;
        this.rootNode = rootNode;
        this.list = new ArrayList<Pair<AliasTreeNode, MatcherNode>>(listInitialSize);
    }

    public int size() {
        return list.size();
    }

    public AliasTreeNode getRootNode() {
        return rootNode;
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
