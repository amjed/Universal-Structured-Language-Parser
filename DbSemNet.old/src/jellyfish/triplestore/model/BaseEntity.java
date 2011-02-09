/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import javax.persistence.InheritanceType;
import java.util.*;

/**
 *
 * @author Xevia
 */

@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_BASEENTITY")
public abstract class BaseEntity extends PersistenceObject {

    public static final String ATTR_NAME = "name";

    @javax.persistence.Column(nullable=false,length=100,unique=true)
    private String name;

    public BaseEntity() {
    }

    public BaseEntity( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        if (!name.isEmpty() && Character.isUpperCase( name.charAt(0) ) ) {
            name = Character.toLowerCase( name.charAt(0) ) + name.substring( 1 );
        }
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(ATTR_NAME, oldValue, name);
    }

    public boolean isValue() {
        return false;
    }

    public boolean isRelationship() {
        return false;
    }

    public static class BaseEntityService {

        private javax.persistence.EntityManager em;
        private final ParametizedFindEntity<BaseEntity> findByName;

        public BaseEntityService( javax.persistence.EntityManager em ) {
            this.em = em;
            this.findByName = new ParametizedFindEntity<BaseEntity>( em, BaseEntity.class );
            this.findByName.addCriteria( ATTR_NAME, String.class );

        }

        public List<BaseEntity> findByName( String name ) {
            synchronized (this.findByName) {
                this.findByName.setParameter( ATTR_NAME, name );
                List<BaseEntity> entityNameList = this.findByName.getQuery().getResultList();
                return entityNameList;
            }
        }
    }
    
}
