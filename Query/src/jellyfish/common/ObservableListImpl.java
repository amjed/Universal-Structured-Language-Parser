/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author Xevia
 */
public class ObservableListImpl<E> extends AbstractList<E>
        implements org.jdesktop.observablecollections.ObservableList<E> {

    private final boolean supportsElementPropertyChanged;
    private List<E> list;
    private List<ObservableListListener> listeners;

    public ObservableListImpl(List<E> list, boolean supportsElementPropertyChanged) {
        this.list = list;
        listeners = new CopyOnWriteArrayList<ObservableListListener>();
        this.supportsElementPropertyChanged = supportsElementPropertyChanged;
    }

    public ObservableListImpl(List<E> list) {
        this(list, false);
    }

    public ObservableListImpl() {
        this(new ArrayList<E>(), false);
    }
    
    public E get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    @Override
    public E set(int index, E element) {
        E oldValue = list.set(index, element);
        for (ObservableListListener listener : listeners) {
            listener.listElementReplaced(this, index, oldValue);
        }
        return oldValue;
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        modCount++;
        for (ObservableListListener listener : listeners) {
            listener.listElementsAdded(this, index, 1);
        }
    }

    @Override
    public E remove(int index) {
        E oldValue = list.remove(index);
        modCount++;
        for (ObservableListListener listener : listeners) {
            listener.listElementsRemoved(this, index,
                    java.util.Collections.singletonList(oldValue));
        }
        return oldValue;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (list.addAll(index, c)) {
            modCount++;
            for (ObservableListListener listener : listeners) {
                listener.listElementsAdded(this, index, c.size());
            }
        }
        return false;
    }

    @Override
    public void clear() {
        List<E> dup = new ArrayList<E>(list);
        list.clear();
        modCount++;
        if (!dup.isEmpty()) {
            for (ObservableListListener listener : listeners) {
                listener.listElementsRemoved(this, 0, dup);
            }
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    private void fireElementChanged(int index) {
        for (ObservableListListener listener : listeners) {
            listener.listElementPropertyChanged(this, index);
        }
    }

    public void addObservableListListener(ObservableListListener listener) {
        listeners.add(listener);
    }

    public void removeObservableListListener(ObservableListListener listener) {
        listeners.remove(listener);
    }

    public boolean supportsElementPropertyChanged() {
        return supportsElementPropertyChanged;
    }
}
