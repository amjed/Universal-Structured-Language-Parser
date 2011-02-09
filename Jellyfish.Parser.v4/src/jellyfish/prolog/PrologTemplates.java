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
class PrologTemplates {

    public static final String ASSERTED_PREFIX = "asserted_";
    public static final String templateLocation = PrologTemplates.class.getPackage().getName().replaceAll("\\.", "/");

    private static String loadTemplate(String fname) throws Exception {
        String resourceName = templateLocation+"/"+fname;
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
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
            setsCode = loadTemplate( "sets.pl" );
            headerTemplate = loadTemplate( "header.pl" );
            normalTemplate = loadTemplate( "normal.pl" );
            symmetricTemplate = loadTemplate( "symmetric.pl" );
            transitiveTemplate = loadTemplate( "transitive.pl" );
            transitiveSymmetricTemplate = loadTemplate( "transitive-symmetric.pl" );
        } catch (Exception e) {
            e.printStackTrace( System.out );
        }
    }

    public static String getSetsCode() {
        return setsCode;
    }

    public static String getRelationshipHeader( String relationshipName,
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

    private static String getNormalCode( String relationshipName ) {
        return nameReplacePattern.matcher( normalTemplate ).replaceAll( relationshipName );
    }

    private static String getSymmetricCode( String relationshipName ) {
        return nameReplacePattern.matcher( symmetricTemplate ).replaceAll( relationshipName );
    }

    private static String getTransitiveCode( String relationshipName ) {
        return nameReplacePattern.matcher( transitiveTemplate ).replaceAll( relationshipName );
    }
    
    private static String getTransitiveSymmetricCode( String relationshipName ) {
        return nameReplacePattern.matcher( transitiveSymmetricTemplate ).replaceAll( relationshipName );
    }
    
    public static String getRelationshipDeclaration(
            String relationshipName, boolean transitive, boolean symmetric )
    {
        if (transitive && symmetric) {
            return getTransitiveSymmetricCode(relationshipName);
        } else
            if (transitive) {
                return getTransitiveCode(relationshipName);
            } else
                if (symmetric) {
                    return getSymmetricCode(relationshipName);
                } else
                    return getNormalCode(relationshipName);

    }

    public static String getRelationshipAssertedName(
            String relationshipName )
    {
        return PrologTemplates.ASSERTED_PREFIX+relationshipName;
    }

    public static String define(
            String relationshipName, String subject, String object )
    {
        return PrologTemplates.ASSERTED_PREFIX+relationshipName+"("+subject+","+object+").\n";
    }

}
