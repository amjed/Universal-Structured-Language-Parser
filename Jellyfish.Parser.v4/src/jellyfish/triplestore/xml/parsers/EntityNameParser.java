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
import jellyfish.triplestore.model.EntityName;
import jellyfish.triplestore.model.Language;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;

/**
 *
 * @author Xevia
 */
public class EntityNameParser extends jellyfish.xml.XmlNodeParser<EntityName,Pair<Entity,Language>> {

    private XmlTripleStore xmlTripleStore;

    public EntityNameParser( XmlTripleStore xmlTripleStore ) {
	this.xmlTripleStore = xmlTripleStore;
    }
    
    @Override
    public EntityName parse( XmlNodeInfo<Pair<Entity, Language>> nodeInfo ) {
	Element element = (Element) nodeInfo.getNode();
	
	String name = element.getTextContent();
	name = name.trim();
	if (name.isEmpty()) {
	    throw new RuntimeException(
		    "The entity name at location "+nodeInfo.getLocation()+" is empty.");
	}

	EntityName entityName = new EntityName( nodeInfo.getParentObject().getSecond(),
						nodeInfo.getParentObject().getFirst(),
						name );
	xmlTripleStore.addEntityName( entityName );
	
	return entityName;
    }


}
