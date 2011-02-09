/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.triplestore.model.Triple;

/**
 *
 * @author Xevia
 */
public class TestModel {
    
    public static void main( String[] args ) {
        try {
            EntityManager em = PersistenceUtil.getEntityManager();
            if (em==null) {
                System.out.println("ERROR: Unintialized EntityManager");
                return;
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery( Long.class );
            Root<Triple> root = cq.from( Triple.class );
            cq.select( cb.count( root ) );
            Long count = em.createQuery( cq ).getSingleResult();

            System.out.println( "count = " + count );

        } catch (Exception e) {
            e.printStackTrace( System.out );
        }

    }
}
