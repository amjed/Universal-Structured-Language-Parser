package jellyfish.base;

import java.io.File;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.xml.XmlTripleStore;

/**
 *
 * @author Umran
 */
public class DebugTripleStore
{
    public static void main( String[] args ) {
        try {
	    System.out.println( "Loading..." );
	    System.out.println( "===========" );
	    TripleStore tripleStore = new XmlTripleStore( new File( "semnet.xml" ) );

	    System.out.println( "Done Loading." );
	    System.out.println( "--------------" );
	    System.out.println( );

	    /*
	    System.out.println( "Languages:" );
	    System.out.println( "==========" );
	    for (Language language:tripleStore.getLanguages()) {
		System.out.println( language.getName()+
			"\n\ttokenizer="+language.getTokenizerClass()+
			"\n\tlanguage="+language.getClausesFile() );
	    }
	    System.out.println( "--------------" );
	    System.out.println( );
	    
	    System.out.println( "Relationships:" );
	    System.out.println( "==========" );
	    for (Relationship relationship:tripleStore.getRelationships()) {
		System.out.println( relationship.getName()+
			"\n\tsymmetric="+relationship.isSymmetric()+
			"\n\ttransitive="+relationship.isTransitive() );
	    }
	    System.out.println( "--------------" );
	    System.out.println( );

	    System.out.println( "Entities:" );
	    System.out.println( "==========" );
	    for (Entity entity:tripleStore.getEntities()) {
		System.out.println( entity.getName() );
		for (Language language:tripleStore.getLanguages()) {
		    System.out.println( "\t"+language.getName() );
		    for (EntityName entityName:tripleStore.getEntityNames( entity, language )) {
			System.out.println( "\t\t"+entityName.getName() );
		    }
		}
	    }
	    System.out.println( "--------------" );
	    System.out.println( );

	    System.out.println( "Triples:" );
	    System.out.println( "==========" );
	    for (Triple triple:tripleStore.getTriples()) {
		System.out.println( triple.getSubject()+" "+triple.getPredicate().getName()+" "+triple.getObject() );
	    }
	    System.out.println( "--------------" );
	    System.out.println( );

	    System.out.println( "Prolog Code:" );
	    System.out.println( "======================================================" );

	    ReferenceEngine referenceEngine = tripleStore.compile();
	    */
	    JellyfishBase jellyfishBase = new JellyfishBase( tripleStore );
	    
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
        }
    }
}
