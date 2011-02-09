/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import jellyfish.triplestore.model.Language;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class LanguageParser extends jellyfish.xml.XmlNodeParser<Language,XmlTripleStore> {

    private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
	    Arrays.asList( "name", "tokenizerClass", "clausesFile"));

    @Override
    public Set<String> getRequiredAttributes() {
	return REQUIRED_ATTRIBUTES;
    }

    @Override
    public Language parse( XmlNodeInfo<XmlTripleStore> nodeInfo ) {
	
	String name = getAttributeOrDefault( nodeInfo, "name", "" );
	String tokenizerClass = getAttributeOrDefault( nodeInfo, "tokenizerClass", "" );
	String clausesFile = getAttributeOrDefault( nodeInfo, "clausesFile", "" );

	Language language = new Language( name, tokenizerClass, clausesFile );

	nodeInfo.getParentObject().addLanguage( language );

	return language;
    }


}
