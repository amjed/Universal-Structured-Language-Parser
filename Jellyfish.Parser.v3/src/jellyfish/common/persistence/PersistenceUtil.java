/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.common.persistence;


import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 *
 * @author Xevia
 */
public class PersistenceUtil {

    private static final String PERSISTENCE_UNIT = "JellyfishPU";
    private static final EntityManagerFactory entityManagerFactory =
            javax.persistence.Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    
    public static EntityManager getEntityManager(boolean drop, boolean create) {
        HashMap hashMap = new HashMap(2);
        if (drop && create) {
            hashMap.put(org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION,
                    org.eclipse.persistence.config.PersistenceUnitProperties.DROP_AND_CREATE);
//            hashMap.put(org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINE_LABEL);
            System.out.println(hashMap);
        } else {
            if (create) {
                hashMap.put(org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION,
                        org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_ONLY);
//                hashMap.put(org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINE_LABEL);
                System.out.println(hashMap);
            } else {
                hashMap.put(org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION,
                        org.eclipse.persistence.config.PersistenceUnitProperties.NONE);
            }
        }

        EntityManagerFactory emf =
            javax.persistence.Persistence.createEntityManagerFactory(PERSISTENCE_UNIT,hashMap);
        return emf.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        return getEntityManager(false,false);
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
