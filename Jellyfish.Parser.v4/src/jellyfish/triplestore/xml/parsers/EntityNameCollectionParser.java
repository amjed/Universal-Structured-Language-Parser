/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.common.Pair;
import jellyfish.triplestore.model.Entity;
import jellyfish.triplestore.model.Language;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class EntityNameCollectionParser
		extends jellyfish.xml.XmlNodeParser<Pair<Entity, Language>, Entity>
{

	private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
			Arrays.asList( "language" ) );
	private XmlTripleStore xmlTripleStore;

	public EntityNameCollectionParser( XmlTripleStore xmlTripleStore ) {
		this.xmlTripleStore = xmlTripleStore;
	}

	@Override
	public Set<String> getRequiredAttributes() {
		return REQUIRED_ATTRIBUTES;
	}

	@Override
	public Pair<Entity, Language> parse( XmlNodeInfo<Entity> nodeInfo ) {

		String langugageName = getAttributeOrDefault( nodeInfo, "language", "" );

		Language language = xmlTripleStore.getLanguage( langugageName );

		if ( language == null ) {
			throw new RuntimeException(
					"The language '" + langugageName + "' referenced in the language attribute at " +
					nodeInfo.getLocation() + " was not found." );
		}

		return new Pair<Entity, Language>( nodeInfo.getParentObject(), language );
	}
}
