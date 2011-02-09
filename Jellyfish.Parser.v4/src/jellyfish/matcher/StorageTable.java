/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher;

import java.util.*;

/**
 *
 * @author Xevia
 */

/*
 * A storage table is used to hold values that are mapped to nodes of
 *  the alias tree without binding the values to the nodes. Hence multiple
 *  storage tables can exist simultaniously for each tree.
 *  A storage table created though an alias node creates a storage table capable of
 *  holding values for each node in that alias tree.
 */

public class StorageTable<ValueType>
{
    private AliasTreeNode rootAlias;
    private int treeSize;
    private Map<Integer, List<ValueType>> map;

    public StorageTable(AliasTreeNode aliasTreeNode) {
        this.rootAlias = aliasTreeNode.getRoot();
        this.treeSize = this.rootAlias.getTreeSize();
        this.map = new HashMap(this.treeSize);

        initializeTable();
    }
    
    private void initializeTable() {
        ArrayDeque<AliasTreeNode> q = new ArrayDeque<AliasTreeNode>(this.treeSize);
        q.add(rootAlias);
        while ( !q.isEmpty() ) {
            AliasTreeNode node = q.removeFirst();
            for ( AliasTreeNode child : (List<AliasTreeNode>) node.getChildren() ) {
				q.addLast(child);
            }
            map.put(node.hashCode(), null);
        }
    }

    public void put( AliasTreeNode child, ValueType value ) {
        if ( !map.containsKey(child.hashCode()) ) {
            throw new RuntimeException(
                    "Child alias '" + child + "' is not part of the alias tree '" +
                    rootAlias + "'");
        }
        if ( map.get(child.hashCode()) == null ) {
            map.put(child.hashCode(), new ArrayList<ValueType>());
        }
        map.get(child.hashCode()).add(value);
    }

    public List<ValueType> get( AliasTreeNode child ) {
        return map.get(child.hashCode());
    }

    public void clear() {
        for ( Integer k : map.keySet() ) {
            map.put(k, null);
        }
    }

    public AliasTreeNode getAlias() {
        return rootAlias;
    }

    public int getSize() {
        return map.keySet().size();
    }
}
