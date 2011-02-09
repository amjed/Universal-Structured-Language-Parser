/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common.lazyCollections;

import java.util.Collection;
import java.util.List;

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

}
