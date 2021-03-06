/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.sample;

import javax.persistence.EntityManager;
import jellyfish.triplestore.model.*;
import java.util.*;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.triplestore.TripleStore;

/**
 *
 * @author Xevia
 */
public class CreateSampleStore {

    public static <E> E firstOrDefault(List<E> list, E def) {
        if (list.isEmpty())
            return def;
        else
            return list.iterator().next();
    }

    public static EntityManager em;

    public static void main( String[] args ) {
        try {
            em = PersistenceUtil.getEntityManager(true,true);
//            em = PersistenceUtil.getEntityManager();
            if (em==null) {
                System.out.println("ERROR: Unintialized EntityManager");
                return;
            }

            System.out.println(  );
            System.out.println( "Begin Populating" );
            System.out.println( "======================================" );

            em.getTransaction().begin();

            Relationship isSubsetOf = new Relationship("isSubsetOf", true, false);
            em.persist( isSubsetOf );
            
            Relationship worksUnder = new Relationship("worksUnder", true, false);
            em.persist( worksUnder );

            em.getTransaction().commit();
            em.getTransaction().begin();

            String[] entities = new String[] {
                "male",
                "female",
                "foreigner",
                "idris",
                "syima",
                "suraj",
                "kim",
                "fadhli",
                "haziq",
                "amy",
                "wan",
                "fazila",
                "umran",
            };

            for (String e:entities) {
                Entity entity = new Entity(e);
                em.persist( entity );
            }
            
            em.getTransaction().commit();
            em.getTransaction().begin();
            
            String[][] triples = new String[][] {
                {"idris","isSubsetOf","foreigner"},
                {"umran","isSubsetOf","foreigner"},
                {"suraj","isSubsetOf","foreigner"},
                
                {"idris","isSubsetOf","male"},
                {"suraj","isSubsetOf","male"},
                {"kim","isSubsetOf","male"},
                {"fadhli","isSubsetOf","male"},
                {"haziq","isSubsetOf","male"},
                {"umran","isSubsetOf","male"},

                {"syima","isSubsetOf","female"},
                {"amy","isSubsetOf","female"},
                {"wan","isSubsetOf","female"},
                {"fazila","isSubsetOf","female"},
                
                {"syima","worksUnder","idris"},
                {"suraj","worksUnder","syima"},
                {"umran","worksUnder","syima"},
                {"amy","worksUnder","syima"},
                {"kim","worksUnder","suraj"},
                {"fadhli","worksUnder","suraj"},
                {"haziq","worksUnder","suraj"},
                {"wan","worksUnder","amy"},
                {"fazila","worksUnder","amy"},
            };

            BaseEntity.BaseEntityService baseEntityService = new BaseEntity.BaseEntityService( em );

            for (String[] tr:triples) {
                String sub = tr[0];
                String pred = tr[1];
                String obj = tr[2];

//                System.out.println( sub+" "+pred+" "+obj );

                Entity subject = (Entity)firstOrDefault(baseEntityService.findByName( sub ), null);
                Relationship predicate = (Relationship)firstOrDefault(baseEntityService.findByName( pred ), null);
                Entity object = (Entity)firstOrDefault(baseEntityService.findByName( obj ), null);

                System.out.println( "\t" + subject.getName()+" "+predicate.getName()+" "+object.getName() );
                
                Triple triple = new Triple(subject, predicate, object );
                em.persist( triple );
            }

            em.getTransaction().commit();
            
            System.out.println( "======================================" );
            System.out.println( " DONE POPULATING" );
            System.out.println(  );

        } catch (Exception e) {
            e.printStackTrace( System.out );
        }

    }
}
