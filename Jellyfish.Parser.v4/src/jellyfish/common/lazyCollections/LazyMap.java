package jellyfish.common.lazyCollections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class LazyMap<KeyType,ValueType> implements Map<KeyType,ValueType> {

    private boolean initialized;
    private Map<KeyType,ValueType> value;
    private Initializer<Map<KeyType,ValueType>> initializer;

    public LazyMap( Initializer<Map<KeyType,ValueType>> initializer ) {
        this.initialized = false;
        this.value = Collections.EMPTY_MAP;
        this.initializer = initializer;
    }

    private synchronized void initialize() {
	if (this.initialized) return;
	
        this.value = initializer.initialize();
        this.initialized = true;
    }

    public Collection<ValueType> values() {
        return value.values();
    }

    public int size() {
        return value.size();
    }

    public ValueType remove( Object key ) {
        return value.remove( key );
    }

    public void putAll( Map<? extends KeyType, ? extends ValueType> m ) {
        if (!initialized) initialize();
        value.putAll( m );
    }

    public ValueType put( KeyType key, ValueType value ) {
        if (!initialized) initialize();
        return this.value.put( key, value );
    }

    public Set<KeyType> keySet() {
        return value.keySet();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public int hashCode() {
        return value.hashCode();
    }

    public ValueType get( Object key ) {
        return value.get( key );
    }

    public boolean equals( Object o ) {
        return value.equals( o );
    }

    public Set<Entry<KeyType, ValueType>> entrySet() {
        return value.entrySet();
    }

    public boolean containsValue( Object value ) {
        if (!initialized) return false;
        return this.value.containsValue( value );
    }

    public boolean containsKey( Object key ) {
        if (!initialized) return false;
        return value.containsKey( key );
    }

    public void clear() {
        value.clear();
    }



}
