/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;

/**
 *
 * @author Xevia
 */
public class PredicateParser extends jellyfish.xml.XmlNodeParser<Relationship,Triple> {

    private static final Set<Integer> VALID_INDEXES = new HashSet<Integer>(Arrays.asList( 1 ));

    private XmlTripleStore xmlTripleStore;

    public PredicateParser( XmlTripleStore xmlTripleStore ) {
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
    public Relationship parse( XmlNodeInfo<Triple> nodeInfo ) {
	Element element = (Element) nodeInfo.getNode();

	String name = element.getTextContent();
	name = name.trim();

	Relationship relationship = xmlTripleStore.getRelationship( name );

	if (relationship==null) {
	    throw new RuntimeException(
		    "The relationship '"+name+"' referred to as the predicate at "+
		    nodeInfo.getLocation()+" could not be found." );
	}

	nodeInfo.getParentObject().setPredicate( relationship );

	return relationship;
    }

}
