package jellyfish.common.lazyCollections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

class LazySet<ValueType> implements Set<ValueType> {

    private boolean initialized;
    private Set<ValueType> value;
    private Initializer<Set<ValueType>> initializer;

    public LazySet( Initializer<Set<ValueType>> initializer ) {
        this.initialized = false;
        this.value = Collections.EMPTY_SET;
        this.initializer = initializer;
    }

    private synchronized void initialize() {
        this.value = initializer.initialize();
        this.initialized = true;
    }

    public <T> T[] toArray( T[] a ) {
        return value.toArray( a );
    }

    public Object[] toArray() {
        return value.toArray();
    }

    public int size() {
        return value.size();
    }

    public boolean retainAll( Collection<?> c ) {
        if (!initialized)
            return true;
        return value.retainAll( c );
    }

    public boolean removeAll( Collection<?> c ) {
        if (!initialized)
            return false;
        return value.removeAll( c );
    }

    public boolean remove( Object o ) {
        if (!initialized)
            return false;
        return value.remove( o );
    }

    public Iterator<ValueType> iterator() {
        return value.iterator();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public int hashCode() {
        return value.hashCode();
    }

    public boolean equals( Object o ) {
        return value.equals( o );
    }

    public boolean containsAll( Collection<?> c ) {
        if (!initialized)
            return false;
        return value.containsAll( c );
    }

    public boolean contains( Object o ) {
        if (!initialized)
            return false;
        return value.contains( o );
    }

    public void clear() {
        value.clear();
    }

    public boolean addAll( Collection<? extends ValueType> c ) {
        if (!initialized)
            initialize();
        return value.addAll( c );
    }

    public boolean add( ValueType e ) {
        if (!initialized)
            initialize();
        return value.add( e );
    }


}
