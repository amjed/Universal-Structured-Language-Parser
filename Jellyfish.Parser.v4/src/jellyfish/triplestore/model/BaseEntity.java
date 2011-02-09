/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.PersistenceObject;
import javax.persistence.InheritanceType;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */

@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_BASEENTITY")
public abstract class BaseEntity extends PersistenceObject {
    
    public BaseEntity() {
    }
    
    public boolean isValue() {
        return false;
    }

    public boolean isRelationship() {
        return false;
    }
    
    public boolean isEntity() {
        return false;
    }

    public boolean isNamedEntity() {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


}
