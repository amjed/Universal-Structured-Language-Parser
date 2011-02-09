/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.beanbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 *
 * @author Xevia
 */
public class BoundBean implements Serializable {

    protected static Field getDeclaredField(Class<? extends BoundBean> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public BoundBean() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    protected void setField(Field f, Object newValue) {
        try {
            Object oldValue = f.get(this);
            f.set(this, newValue);
            changeSupport.firePropertyChange(f.getName(), oldValue, newValue);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }



}
