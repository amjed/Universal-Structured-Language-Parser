/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.triplestore.model.BaseEntity;
import jellyfish.triplestore.model.Entity;
import jellyfish.triplestore.model.Triple;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;

/**
 *
 * @author Xevia
 */
public class ObjectParser extends jellyfish.xml.XmlNodeParser<Entity,Triple> {

    private static final Set<Integer> VALID_INDEXES = new HashSet<Integer>(Arrays.asList( 2 ));

    private XmlTripleStore xmlTripleStore;

    public ObjectParser( XmlTripleStore xmlTripleStore ) {
	this.xmlTripleStore = xmlTripleStore;
    }
    
    @Override
    public boolean requiresExactParentIndex() {
	return true;
    }

    @Override
    public Set<Integer> getValidParentIndexes() {
	return VALID_INDEXES;
    }

    @Override
    public Entity parse( XmlNodeInfo<Triple> nodeInfo ) {
	Element element = (Element) nodeInfo.getNode();

	String name = element.getTextContent();

	Entity entity = xmlTripleStore.getEntity( name );
	name = name.trim();

	if (entity==null) {
	    throw new RuntimeException(
		    "The entity '"+name+"' referred to as the object at "+
		    nodeInfo.getLocation()+" could not be found." );
	}

	nodeInfo.getParentObject().setObject( entity );

	return entity;
    }

}
