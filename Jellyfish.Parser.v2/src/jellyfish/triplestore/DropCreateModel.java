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
public class DropCreateModel {

    public static void main(String[] args) {
        try {
            EntityManager em = PersistenceUtil.getEntityManager(true, true);
            if (em==null) {
                System.out.println("ERROR: Unintialized EntityManager");
                return;
            }

            System.out.println( "TABLES CREATED" );

        } catch (Exception e) {
            e.printStackTrace( System.out );
        }
    }

}
