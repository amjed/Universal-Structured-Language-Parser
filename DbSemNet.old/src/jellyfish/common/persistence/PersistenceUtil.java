/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.common.persistence;


import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.*;

/**
 *
 * @author Xevia
 */
public class PersistenceUtil {

    private static final String PERSISTENCE_UNIT = "JellyfishPU";
    private static final EntityManagerFactory entityManagerFactory =
            javax.persistence.Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }


    public static <DataType> DataType find(EntityManager em, Class<DataType> objectType, Object primaryKey) {
        return em.find( objectType, primaryKey );
    }

    public static <EntityType> TypedQuery<EntityType> getAllQuery(EntityManager em, Class<EntityType> entityClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EntityType> cq = cb.createQuery( entityClass );
        Root<EntityType> root = cq.from( entityClass );
        return em.createQuery( cq );
    }

    public static <EntityType> List<EntityType> getAllList(EntityManager em, Class<EntityType> entityClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EntityType> cq = cb.createQuery( entityClass );
        Root<EntityType> root = cq.from( entityClass );
        TypedQuery<EntityType> query = em.createQuery( cq );
        return query.getResultList();
    }

}
