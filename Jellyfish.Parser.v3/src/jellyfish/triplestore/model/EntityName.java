/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import java.lang.reflect.Field;
import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import java.util.*;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_ENTITYNAME")
public class EntityName extends PersistenceObject {

    public static final Attribute ATTR_NAME = getDeclaredField(EntityName.class, "name");
    public static final Attribute ATTR_ENTITY = getDeclaredField(EntityName.class, "entity");
    public static final Attribute ATTR_LANGUAGE = getDeclaredField(EntityName.class, "language");

    @javax.persistence.ManyToOne(optional=false)
    private Language language;

    @javax.persistence.ManyToOne(optional=false)
    private Entity entity;

    private String name;

    public EntityName() {
    }
    
    public EntityName( Language language, Entity entity, String name ) {
        this.language = language;
        this.entity = entity;
        this.name = name;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity( Entity entity ) {
        setAttribute(ATTR_ENTITY, entity);
    }
    
    public Language getLanguage() {
        return language;
    }

    public void setLanguage( Language language ) {
        setAttribute(ATTR_LANGUAGE, language);
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        setAttribute(ATTR_NAME, name);
    }

    public static class EntityNameService {

        private javax.persistence.EntityManager em;

        private final Object syncFindByName = new Object();
        private ParametizedFindEntity<EntityName> findByName;

        private final Object syncFindByEntityLanguage = new Object();
        private ParametizedFindEntity<EntityName> findByEntityLanguage;

        private final Object syncFindByNameEntityLanguage = new Object();
        private ParametizedFindEntity<EntityName> findByNameEntityLanguage;

        public EntityNameService( javax.persistence.EntityManager em ) {
            this.em = em;
        }

        private void initFindByName() {
            synchronized (syncFindByName) {
                if (this.findByName!=null) return;

                ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>( em, EntityName.class );
                findEntity.addCriteria( ATTR_NAME );

                this.findByName = findEntity;
            }
        }

        private void initFindByEntityLanguage() {
            synchronized (syncFindByEntityLanguage) {
                if (this.findByEntityLanguage!=null) return;

                ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>( em, EntityName.class );
                findEntity.addCriteria( ATTR_ENTITY );
                findEntity.addCriteria( ATTR_LANGUAGE );

                this.findByEntityLanguage = findEntity;
            }
        }

        private void initFindByNameEntityLanguage() {
            synchronized (syncFindByNameEntityLanguage) {
                if (this.findByNameEntityLanguage!=null) return;

                ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>( em, EntityName.class );
                this.findByNameEntityLanguage.addCriteria( ATTR_NAME );
                this.findByNameEntityLanguage.addCriteria( ATTR_ENTITY );
                this.findByNameEntityLanguage.addCriteria( ATTR_LANGUAGE );

                this.findByNameEntityLanguage = findEntity;
            }
        }
        
        public List<EntityName> findByName( String name ) {
            if (this.findByName==null) initFindByName();
            
            synchronized (syncFindByName) {
                this.findByName.setParameter( ATTR_NAME, name );
                List<EntityName> entityNameList = this.findByName.getQuery().getResultList();
                return entityNameList;
            }
        }

        public List<EntityName> findByEntityLanguage( Entity entity, Language language ) {
            if (this.findByEntityLanguage==null) initFindByEntityLanguage();

            synchronized (syncFindByEntityLanguage) {
                this.findByEntityLanguage.setParameter( ATTR_ENTITY, entity );
                this.findByEntityLanguage.setParameter( ATTR_LANGUAGE, language );
                List<EntityName> entityNameList = this.findByEntityLanguage.getQuery().getResultList();
                return entityNameList;
            }
        }

        public synchronized List<EntityName> findByNameEntityLanguage( String name, Entity entity, Language language ) {
            if (this.findByNameEntityLanguage==null) initFindByNameEntityLanguage();
            
            synchronized (syncFindByNameEntityLanguage) {
                this.findByNameEntityLanguage.setParameter( ATTR_NAME, name );
                this.findByNameEntityLanguage.setParameter( ATTR_ENTITY, entity );
                this.findByNameEntityLanguage.setParameter( ATTR_LANGUAGE, language );
                List<EntityName> entityNameList = this.findByNameEntityLanguage.getQuery().getResultList();
                return entityNameList;
            }
        }

    }
    
    

}
