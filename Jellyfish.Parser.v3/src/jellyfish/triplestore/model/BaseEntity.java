/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import java.lang.reflect.Field;
import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import javax.persistence.InheritanceType;
import java.util.*;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */

@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_BASEENTITY")
public abstract class BaseEntity extends PersistenceObject {
    
    public static final Attribute ATTR_NAME = getDeclaredField(BaseEntity.class, "name");

    @javax.persistence.Column(nullable=false,length=100,unique=true)
    private String name;

    public BaseEntity() {
    }

    public BaseEntity( String name ) {
        this.name = name;
    }
    
    public BaseEntity( BaseEntity be ) {
        this.name = be.name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        if (!name.isEmpty() && Character.isUpperCase( name.charAt(0) ) ) {
            name = Character.toLowerCase( name.charAt(0) ) + name.substring( 1 );
        }
        setAttribute(ATTR_NAME, name);
    }

    public boolean isValue() {
        return false;
    }

    public boolean isRelationship() {
        return false;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"{" + "name=" + name + '}';
    }

    public static class BaseEntityService {

        private javax.persistence.EntityManager em;
        private final ParametizedFindEntity<BaseEntity> findByName;

        public BaseEntityService( javax.persistence.EntityManager em ) {
            this.em = em;
            this.findByName = new ParametizedFindEntity<BaseEntity>( em, BaseEntity.class );
            this.findByName.addCriteria( ATTR_NAME );

        }

        public List<BaseEntity> findByName( String name ) {
            synchronized (this.findByName) {
                this.findByName.setParameter( ATTR_NAME, name );
                List<BaseEntity> entityNameList = this.findByName.getQuery().getResultList();
                return entityNameList;
            }
        }

        public long countByName( String name ) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<BaseEntity> root = cq.from(BaseEntity.class);
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get(ATTR_NAME.getName()), name));
            return em.createQuery(cq).getSingleResult().longValue();
        }
    }

}
