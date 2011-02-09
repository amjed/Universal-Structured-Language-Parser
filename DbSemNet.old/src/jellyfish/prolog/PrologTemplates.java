/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.prolog;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author Xevia
 */
public class PrologTemplates {

    public static final String ASSERTED_PREFIX = "asserted_";


    private static String loadTemplate(String fname) throws Exception {
//        Thread current = Thread.currentThread();
//        InputStream input = current.getContextClassLoader().getResourceAsStream( fname );
        InputStream input = PrologTemplates.class.getResourceAsStream( fname );
        StringBuilder blder = new StringBuilder(5000);
        if(input != null)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String content = "";
            while((content = reader.readLine()) != null)
                blder.append( content ).append( "\n" );
            return blder.toString();
        }
        return blder.toString();
    }

    private static String setsCode = "";
    private static String headerTemplate = "";
    private static String normalTemplate = "";
    private static String symmetricTemplate = "";
    private static String transitiveTemplate = "";
    private static String transitiveSymmetricTemplate = "";

    private static Pattern nameReplacePattern = Pattern.compile( "\\{RELATIONSHIP_NAME\\}" );
    private static Pattern propPeplacePattern = Pattern.compile( "\\{RELATIONSHIP_PROPERTIES\\}" );
    
    static {
        try {
            setsCode = loadTemplate( "/jellyfish/prolog/templates/sets.pl" );
            headerTemplate = loadTemplate( "/jellyfish/prolog/templates/header.pl" );
            normalTemplate = loadTemplate( "/jellyfish/prolog/templates/normal.pl" );
            symmetricTemplate = loadTemplate( "/jellyfish/prolog/templates/symmetric.pl" );
            transitiveTemplate = loadTemplate( "/jellyfish/prolog/templates/transitive.pl" );
            transitiveSymmetricTemplate = loadTemplate( "/jellyfish/prolog/templates/transitive-symmetric.pl" );
        } catch (Exception e) {
            e.printStackTrace( System.out );
        }
    }

    public static String getSetsCode() {
        return setsCode;
    }

    public static String getHeader( String relationshipName,
            boolean transitive, boolean symmetric ) {
        String prop = "";
        if (transitive)
            prop += "TRANSITIVE";
        if (symmetric) {
            if (!prop.isEmpty())
                prop += ", ";
            prop += "SYMMETRIC";
        }
        String afterName = 
                nameReplacePattern.matcher( headerTemplate.toUpperCase() ).
                    replaceAll( relationshipName );
        return propPeplacePattern.matcher( afterName ).replaceAll( prop );
    }

    public static String getNormalCode( String relationshipName ) {
        return nameReplacePattern.matcher( normalTemplate ).replaceAll( relationshipName );
    }

    public static String getSymmetricCode( String relationshipName ) {
        return nameReplacePattern.matcher( symmetricTemplate ).replaceAll( relationshipName );
    }

    public static String getTransitiveCode( String relationshipName ) {
        return nameReplacePattern.matcher( transitiveTemplate ).replaceAll( relationshipName );
    }
    
    public static String getTransitiveSymmetricCode( String relationshipName ) {
        return nameReplacePattern.matcher( transitiveSymmetricTemplate ).replaceAll( relationshipName );
    }
    
}
