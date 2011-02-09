package jellyfish.common.persistence;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import jellyfish.common.ObservableBean;

/**
 *
 * @author Xevia
 */
@javax.persistence.MappedSuperclass
public abstract class PersistenceObject extends ObservableBean {

    private static long lastId = 0;

    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.SEQUENCE)
    private Long id;

    @javax.persistence.Version
    private long version;

    public PersistenceObject() {
        synchronized (PersistenceObject.class) {
            id = ++lastId;
        }
    }
    
    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        if (id!=null)
            return (int)(long)id;
        else
            return ((Object)this).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistenceObject)) {
            return false;
        }
        PersistenceObject other = (PersistenceObject) object;
        if (this.id!=other.id)
            return false;
        return true;
    }
    
    protected static Attribute getDeclaredField(Class<? extends PersistenceObject> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return new Attribute(f);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    protected void setAttribute(Attribute attr, Object newValue) {
        try {
            Field field = attr.getField();
            Object oldValue = field.get(this);
            field.set(this, newValue);
            changeSupport.firePropertyChange(attr.getName(), oldValue, newValue);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }


}
