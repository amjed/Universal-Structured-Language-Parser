package jellyfish.common.persistence;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Xevia
 */
@javax.persistence.MappedSuperclass
public abstract class PersistenceObject implements java.io.Serializable {

    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.SEQUENCE)
    private long id;

    @javax.persistence.Version
    private long version;
    
    public long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
//
//    public void setVersion(long version) {
//        this.version = version;
//    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += id;
        return hash;
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
    
    @javax.persistence.Transient
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

}
