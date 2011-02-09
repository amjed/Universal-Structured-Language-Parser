/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common.lazyCollections;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Xevia
 */
public class LazyCollections
{
    
    public static <ValueType> Collection<ValueType> createCollection(Initializer<Collection<ValueType>> initializer)
    {
        return new LazyCollection<ValueType>( initializer );
    }

    public static <ValueType> List<ValueType> createList(Initializer<List<ValueType>> initializer)
    {
        return new LazyList<ValueType>( initializer );
    }

    public static <ValueType> Set<ValueType> createSet(Initializer<Set<ValueType>> initializer)
    {
        return new LazySet<ValueType>( initializer );
    }

    public static <KeyType,ValueType> Map<KeyType,ValueType> createMap(Initializer<Map<KeyType,ValueType>> initializer)
    {
        return new LazyMap<KeyType,ValueType>( initializer );
    }
}
