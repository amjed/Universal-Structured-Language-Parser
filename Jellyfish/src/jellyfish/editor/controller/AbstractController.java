/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.TypedQuery;
import jellyfish.common.ObservableBean;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableCollections.ObservableListHelper;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author Xevia
 */
public class AbstractController<T extends ObservableBean> extends ObservableBean {
    
    public static String ATTR_SELECTED_ITEM = "selectedItem";
    public static String ATTR_SELECTED_INDEX = "selectedItemIndex";

    protected final PersistenceContext context;
    protected final TypedQuery<T> query;
//    protected final ObservableListHelper<T> itemListHelper;
    protected final ObservableList<T> itemList;
    protected final PersistenceContext.ObservableListPersister listPersister;
    
    protected T selectedItem;
    protected int selectedItemIndex;

    public AbstractController(PersistenceContext context, TypedQuery<T> query) {
        this.context = context;
        this.query = query;

        this.itemList = ObservableCollections.observableList(Collections.synchronizedList(new ArrayList<T>()));
        this.listPersister = context.new ObservableListPersister();
        this.itemList.addObservableListListener(listPersister);
        this.itemList.addObservableListListener(new SelectedItemListListener());

        update();
    }

    final public void update()
    {
        boolean active = this.listPersister.isActive();
        this.listPersister.setActive(false);

        List<T> l = query.getResultList();
        this.itemList.retainAll(l);
        for (T t:l) {
            int i = this.itemList.indexOf(t);
            if (i>=0) {
                this.itemList.set(i, t);
                t.removePropertyChangeListeners(ObservableBeanPersister.class);
                t.addPropertyChangeListener(new ObservableBeanPersister(t));
            } else {
                this.itemList.add(t);
                t.addPropertyChangeListener(new ObservableBeanPersister(t));
            }
        }

        this.listPersister.setActive(active);
    }

    private class ObservableBeanPersister implements PropertyChangeListener {

        private T item;

        public ObservableBeanPersister(T item) {
            this.item = item;
        }

        public void propertyChange(PropertyChangeEvent evt) {
//            T newItem = context.merge(item);
//            int oldIndex = itemList.indexOf(item);
//            if (oldIndex>=0) {
//                boolean active = listPersister.isActive();
//                listPersister.setActive(false);
//                itemList.set(oldIndex, newItem);
//                listPersister.setActive(active);
//                item = newItem;
//                newItem.removePropertyChangeListeners(ObservableBeanPersister.class);
//                newItem.addPropertyChangeListener(this);
//            }
            context.save();
        }

    }

    public ObservableList<T> getItemList() {
        return itemList;
    }

    public PersistenceContext getContext() {
        return context;
    }
    
    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    protected void internSetSelectedItemIndex(int selectedItemIndex) {
        if (selectedItemIndex<0 || selectedItemIndex>=itemList.size())
            selectedItemIndex = -1;

        this.selectedItemIndex = selectedItemIndex;
    }
    
    public void setSelectedItemIndex(int selectedItemIndex) {
        int prev = getSelectedItemIndex();
        internSetSelectedItemIndex(selectedItemIndex);
        int curr = getSelectedItemIndex();
        if (prev!=curr)
            firePropertyChange(ATTR_SELECTED_INDEX, prev, curr);
    }

    private class SelectedItemListListener implements ObservableListListener {

        public void listElementsAdded(ObservableList list, int index, int length) {
            if (list.isEmpty()) {
                setSelectedItemIndex(0);
            }
        }

        public void listElementsRemoved(ObservableList list, int index, List oldElements) {
            if (list.isEmpty())
                setSelectedItemIndex(-1);
            else
                if (index==selectedItemIndex)
                    setSelectedItemIndex(selectedItemIndex-1);
        }

        public void listElementReplaced(ObservableList list, int index, Object oldElement) {
            
        }

        public void listElementPropertyChanged(ObservableList list, int index) {
        }
        
    }
    

}
