/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xevia
 */
public class ObservableBean implements Serializable {

    @javax.persistence.Transient
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    protected ForwardingPropertyChangeListener forwardPropertyChangeEvents(String sourceProperty, String targetProperty)
    {
        ForwardingPropertyChangeListener changeListener = new ForwardingPropertyChangeListener(targetProperty);
        changeSupport.addPropertyChangeListener(sourceProperty, changeListener );
        return changeListener;
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return changeSupport.getPropertyChangeListeners(propertyName);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return changeSupport.getPropertyChangeListeners();
    }

    protected void firePropertyChange(PropertyChangeEvent evt) {
        changeSupport.firePropertyChange(evt);
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        changeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        changeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        changeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public boolean containsPropertyChangeListener(String propertyName, Class type)
    {
        PropertyChangeListener[] listeners = getPropertyChangeListeners(propertyName);
        for (PropertyChangeListener listener:listeners) {
            if (type.isAssignableFrom(listener.getClass()))
                return true;
        }
        return false;
    }

    public void removePropertyChangeListeners(String propertyName, Class type)
    {
        PropertyChangeListener[] listeners = getPropertyChangeListeners(propertyName);
        for (PropertyChangeListener listener:listeners) {
            if (type.isAssignableFrom(listener.getClass()))
                removePropertyChangeListener(propertyName, listener);
        }
    }
    
    public void removePropertyChangeListeners(Class type)
    {
        PropertyChangeListener[] listeners = getPropertyChangeListeners();
        for (PropertyChangeListener listener:listeners) {
            if (type.isAssignableFrom(listener.getClass()))
                removePropertyChangeListener(listener);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObservableBean other = (ObservableBean) obj;
        return this.hashCode()!=obj.hashCode();
    }

    protected class ForwardingPropertyChangeListener implements PropertyChangeListener {

        private boolean active = true;
        private String targetProperty;

        public ForwardingPropertyChangeListener(String targetProperty) {
            this.targetProperty = targetProperty;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (active && evt.getOldValue()!=evt.getNewValue())
                firePropertyChange(targetProperty, evt.getOldValue(), evt.getNewValue());
        }

    }

}
