package jellyfish.common.lazyCollections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class LazyList<ValueType> implements List<ValueType> {

    private boolean initialized;
    private List<ValueType> value;
    private Initializer<List<ValueType>> initializer;

    public LazyList( Initializer<List<ValueType>> initializer ) {
        this.initialized = false;
        this.value = Collections.EMPTY_LIST;
        this.initializer = initializer;
    }

    private synchronized void initialize() {
	if (this.initialized) return;
        this.value = initializer.initialize();
        this.initialized = true;
    }

    public <T> T[] toArray( T[] a ) {
        return value.toArray( a );
    }

    public Object[] toArray() {
        return value.toArray();
    }

    public List<ValueType> subList( int fromIndex, int toIndex ) {
        if (!initialized) return value;
        return value.subList( fromIndex, toIndex );
    }

    public int size() {
        return value.size();
    }

    public ValueType set( int index, ValueType element ) {
        if (!initialized) initialize();
        return value.set( index, element );
    }

    public boolean retainAll( Collection<?> c ) {
        if (!initialized) return true;
        return value.retainAll( c );
    }

    public boolean removeAll( Collection<?> c ) {
        if (!initialized) return true;
        return value.removeAll( c );
    }

    public ValueType remove( int index ) {
        return value.remove( index );
    }

    public boolean remove( Object o ) {
        if (!initialized) return false;
        return value.remove( o );
    }

    public ListIterator<ValueType> listIterator( int index ) {
        return value.listIterator( index );
    }

    public ListIterator<ValueType> listIterator() {
        return value.listIterator();
    }

    public int lastIndexOf( Object o ) {
        return value.lastIndexOf( o );
    }

    public Iterator<ValueType> iterator() {
        return value.iterator();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public int indexOf( Object o ) {
        return value.indexOf( o );
    }

    public int hashCode() {
        return value.hashCode();
    }

    public ValueType get( int index ) {
        return value.get( index );
    }

    public boolean equals( Object o ) {
        return value.equals( o );
    }

    public boolean containsAll( Collection<?> c ) {
        return value.containsAll( c );
    }

    public boolean contains( Object o ) {
        if (!initialized) return false;
        return value.contains( o );
    }

    public void clear() {
        value.clear();
    }

    public boolean addAll( int index,
                           Collection<? extends ValueType> c ) {
        if (!initialized) initialize();
        return value.addAll( index, c );
    }

    public boolean addAll( Collection<? extends ValueType> c ) {
        if (!initialized) initialize();
        return value.addAll( c );
    }

    public void add( int index, ValueType element ) {
        if (!initialized) initialize();
        value.add( index, element );
    }

    public boolean add( ValueType e ) {
        if (!initialized) initialize();
        return value.add( e );
    }

}
