/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.triplestore.model.Entity;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class EntityParser extends jellyfish.xml.XmlNodeParser<Entity,XmlTripleStore> {

    private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
	    Arrays.asList( "name" ));

    @Override
    public Set<String> getRequiredAttributes() {
	return REQUIRED_ATTRIBUTES;
    }

    @Override
    public Entity parse( XmlNodeInfo<XmlTripleStore> nodeInfo ) {

	String name = getAttributeOrDefault( nodeInfo, "name", "" );
	
	Entity entity = new Entity( name );

	nodeInfo.getParentObject().addEntity( entity );

	return entity;
    }

}
