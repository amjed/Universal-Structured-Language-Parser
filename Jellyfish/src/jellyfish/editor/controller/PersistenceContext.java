/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.triplestore.model.BaseEntity;
import jellyfish.triplestore.model.BaseEntity.BaseEntityService;
import org.jdesktop.beansbinding.Validator.Result;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author Xevia
 */
public class PersistenceContext {

    private EntityManager entityManager;
    private BaseEntityService baseEntityService;

    public PersistenceContext(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.baseEntityService = new BaseEntityService(entityManager);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public BaseEntityService getBaseEntityService() {
        return baseEntityService;
    }

    public boolean nameExists(String name) {
        long count = baseEntityService.countByName(name);
        return count>0;
    }
    
    public void save()
    {
        EntityTransaction  et = entityManager.getTransaction();
        et.begin();
        entityManager.flush();
        et.commit();
    }

    public void remove(Object entity) {
        entityManager.remove(entity);
    }

    public void refresh(Object entity) {
        entityManager.refresh(entity);
    }

    public void persist(Object entity) {
        entityManager.persist(entity);
    }

    public <T> T merge(T entity) {
        return entityManager.merge(entity);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return entityManager.getReference(entityClass, primaryKey);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return entityManager.find(entityClass, primaryKey);
    }

    public void detach(Object entity) {
        entityManager.detach(entity);
    }

    public <E> List<E> findAll(Class<E> c) {
        return PersistenceUtil.getAllList(entityManager, c);
    }
    
    public class BaseEntityValidator extends org.jdesktop.beansbinding.Validator<BaseEntity> {

        @Override
        public Result validate(BaseEntity value) {
//            System.out.println("validating entity: "+value);
            if (baseEntityService.countByName(value.getName())>0) {
                String msg = "Name '"+value.getName()+"' is already in use.";
//                System.out.println("msg = " + msg);
                return new Result(null,msg);
            }
            return null;
        }

    }

    public class BaseEntityNameValidator extends org.jdesktop.beansbinding.Validator<String> {

        @Override
        public Result validate(String value) {
//            System.out.println("validating name: "+value);
            if (baseEntityService.countByName(value)>0) {
                String msg = "Name '"+value+"' is already in use.";
//                System.out.println("msg = " + msg);
                return new Result(null,msg);
            }
            return null;
        }

    }

    public class ObservableListPersister implements ObservableListListener {

        private boolean active = true;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
        
        public void listElementsAdded(ObservableList list, int index, int length) {
            if (!active) return;
            for (int i=0; i<length; ++i) {
                entityManager.persist(list.get(index+i));
            }
            PersistenceContext.this.save();
        }

        public void listElementsRemoved(ObservableList list, int index, List oldElements) {
            if (!active) return;
            for (Object o:oldElements) {
                entityManager.remove(o);
            }
            PersistenceContext.this.save();
        }

        public void listElementReplaced(ObservableList list, int index, Object oldElement) {
            System.out.println("items replaced");
            if (!active) return;
            entityManager.remove(oldElement);
            entityManager.persist(list.get(index));
            PersistenceContext.this.save();
        }

        public void listElementPropertyChanged(ObservableList list, int index) {
            if (!active) return;
            System.out.println("element list property changed");
            list.set(index, entityManager.merge(list.get(index)));
        }
    }

}
