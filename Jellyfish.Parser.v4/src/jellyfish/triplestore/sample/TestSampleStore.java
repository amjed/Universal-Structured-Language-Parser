/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.sample;

import javax.persistence.EntityManager;
import java.util.*;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.prolog.PrologReferenceEngine;
import jellyfish.triplestore.ReferenceEngine;
import jellyfish.triplestore.ReferenceResults;
import jellyfish.triplestore.db.DatabaseTripleStore;
import jellyfish.triplestore.TripleStore;

/**
 *
 * @author Xevia
 */
public class TestSampleStore {

    public static <E> E firstOrDefault(List<E> list, E def) {
        if (list.isEmpty())
            return def;
        else
            return list.iterator().next();
    }

    public static EntityManager em;

    public static void main( String[] args ) {
        try {
            em = PersistenceUtil.getEntityManager();
            if (em==null) {
                System.out.println("ERROR: Unintialized EntityManager");
                return;
            }

			TripleStore tripleStore = new DatabaseTripleStore( em );
			ReferenceEngine engine = new PrologReferenceEngine( tripleStore );
            

            System.out.println( "Please enter input to test. Enter 'exit' to quit." );
            Scanner scan = new Scanner( System.in );

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                line = line.trim();

                if (line.equalsIgnoreCase( "exit" )) {
                    break;
                }

                if (!line.endsWith("."))
                    line = line + ".";

                try {
                    ReferenceResults results = engine.query(line);
                    if (results!=null)
                        System.out.println(results.toString());
                    else
                        System.out.println("No results.");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

//            String prolog = TripleStore.convertToProlog( em );
//            System.out.println( prolog );

            

        } catch (Exception e) {
            e.printStackTrace( System.out );
        }

    }
}
