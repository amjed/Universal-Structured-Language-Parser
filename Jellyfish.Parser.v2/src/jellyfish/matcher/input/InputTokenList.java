/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.input;

import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Xevia
 */
public class InputTokenList implements List<InputToken> {

    private List<InputToken> list;

    public InputTokenList(int capacity) {
        this.list = new ArrayList<InputToken>(capacity);
    }

    public InputTokenList() {
        this.list = new ArrayList<InputToken>();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public List<InputToken> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public int size() {
        return list.size();
    }

    public InputToken set(int index, InputToken element) {
        return null;
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public InputToken remove(int index) {
        return list.remove(index);
    }

    public boolean remove(Object o) {
        return list.remove((InputToken)o);
    }

    public ListIterator<InputToken> listIterator(int index) {
        return list.listIterator(index);
    }

    public ListIterator<InputToken> listIterator() {
        return list.listIterator();
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public Iterator<InputToken> iterator() {
        return list.iterator();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputTokenList other = (InputTokenList) obj;
        if (this.list != other.list && (this.list == null || !this.list.equals(other.list))) {
            return false;
        }
        return true;
    }

    public InputToken get(int index) {
        return list.get(index);
    }

    public boolean equals(InputToken o) {
        return list.equals(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean contains(Object o) {
        return list.contains((InputToken)o);
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(int index, Collection<? extends InputToken> c) {
//        return list.addAll(index, c);
        return false;
    }

    public boolean addAll(Collection<? extends InputToken> c) {
//        return list.addAll(c);
        return false;
    }

    public void add(int index, InputToken element) {
//        list.add(index, element);
    }

    public boolean add(InputToken e) {
        if (list.isEmpty() || !e.isWildCard() || !list.get(list.size()-1).isWildCard())
            return list.add(e);
        else
            return false;
    }

    @Override
    public String toString() {
        return list.toString();
    }

}
