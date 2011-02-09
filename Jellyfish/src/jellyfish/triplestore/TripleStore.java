/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

import java.util.*;
import javax.persistence.EntityManager;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.prolog.PrologEngine;
import jellyfish.prolog.PrologTemplates;
import jellyfish.triplestore.model.*;
import jellyfish.triplestore.model.Triple.TripleService;

/**
 *
 * @author Xevia
 */
public class TripleStore {


    public static String convertToProlog(EntityManager em) {
        PrologEngine engine = new PrologEngine();

        TripleService tripleService = new Triple.TripleService( em );
        List<Relationship> relationships = PersistenceUtil.getAllList( em, Relationship.class );
        for (Relationship rel: relationships) {
            List<Triple> triples = tripleService.findByPredicates( rel );
            engine.define(rel, triples);
        }

        return engine.getPrologCode();
    }

    public static PrologEngine.PrologEnglet compile(EntityManager em) throws Exception {
        PrologEngine engine = new PrologEngine();

        TripleService tripleService = new Triple.TripleService( em );
        List<Relationship> relationships = PersistenceUtil.getAllList( em, Relationship.class );
        for (Relationship rel: relationships) {
            List<Triple> triples = tripleService.findByPredicates( rel );
            engine.define(rel, triples);
        }

        System.out.println(engine.getPrologCode());

        return engine.compile();
    }
    /*
    public static StringBuilder convertToProlog(EntityManager em) {
        StringBuilder stringBuilder = new StringBuilder( 5000 );

        TripleService tripleService = new Triple.TripleService( em );

        stringBuilder.append( PrologTemplates.getSetsCode() ).append( "\n\n" );

        List<Relationship> relationships = PersistenceUtil.getAllList( em, Relationship.class );
        for (Relationship rel: relationships) {
            
            String relAssertedName = PrologTemplates.ASSERTED_PREFIX+rel.getName();

            stringBuilder.append(
                    PrologTemplates.getRelationshipHeader(
                        rel.getName(),
                        rel.isTransitive(),
                        rel.isSymmetric()
                        ) );

            List<Triple> triples = tripleService.findByPredicates( rel );
            for (Triple triple : triples) {
                stringBuilder.append( relAssertedName ).
                        append( "( " ).
                        append( triple.getSubject().getName() ).
                        append( ", " ).
                        append( triple.getObject().getName() ).
                        append( " ).\n");
            }
            stringBuilder.append( "\n" );

            stringBuilder.append( "\\* PROLOG TEMPLATE *\\\n" );
            if (rel.isTransitive() && rel.isSymmetric() ) {
                stringBuilder.append( PrologTemplates.getTransitiveSymmetricCode( rel.getName() ) );
            } else
                if (rel.isTransitive()) {
                    stringBuilder.append( PrologTemplates.getTransitiveCode( rel.getName() ) );
                } else
                    if (rel.isSymmetric()) {
                        stringBuilder.append( PrologTemplates.getSymmetricCode( rel.getName() ) );
                    } else {
                        stringBuilder.append( PrologTemplates.getNormalCode( rel.getName() ) );
                    }
            stringBuilder.append( "\n" );
        }

        return stringBuilder;
    }
    */

}
