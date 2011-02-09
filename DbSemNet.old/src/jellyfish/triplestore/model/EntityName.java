/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import java.util.*;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_ENTITYNAME")
public class EntityName extends PersistenceObject {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_ENTITY = "entity";
    public static final String ATTR_LANGUAGE = "language";

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
        Entity oldValue = this.entity;
        this.entity = entity;
        changeSupport.firePropertyChange(ATTR_ENTITY, oldValue, entity);
    }
    
    public Language getLanguage() {
        return language;
    }

    public void setLanguage( Language language ) {
        Language oldValue = this.language;
        this.language = language;
        changeSupport.firePropertyChange(ATTR_LANGUAGE, oldValue, language);
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(ATTR_NAME, oldValue, name);
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
                findEntity.addCriteria( ATTR_NAME, String.class );

                this.findByName = findEntity;
            }
        }

        private void initFindByEntityLanguage() {
            synchronized (syncFindByEntityLanguage) {
                if (this.findByEntityLanguage!=null) return;

                ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>( em, EntityName.class );
                findEntity.addCriteria( ATTR_ENTITY, String.class );
                findEntity.addCriteria( ATTR_LANGUAGE, Language.class );

                this.findByEntityLanguage = findEntity;
            }
        }

        private void initFindByNameEntityLanguage() {
            synchronized (syncFindByNameEntityLanguage) {
                if (this.findByNameEntityLanguage!=null) return;

                ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>( em, EntityName.class );
                this.findByNameEntityLanguage.addCriteria( ATTR_NAME, String.class );
                this.findByNameEntityLanguage.addCriteria( ATTR_ENTITY, Entity.class );
                this.findByNameEntityLanguage.addCriteria( ATTR_LANGUAGE, Language.class );

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
