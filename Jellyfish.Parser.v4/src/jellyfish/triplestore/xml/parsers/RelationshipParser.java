/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class RelationshipParser extends jellyfish.xml.XmlNodeParser<Relationship,XmlTripleStore> {

    private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
	    Arrays.asList( "name", "symmetric", "transitive" ));

    @Override
    public Set<String> getRequiredAttributes() {
	return REQUIRED_ATTRIBUTES;
    }

    @Override
    public Relationship parse( XmlNodeInfo<XmlTripleStore> nodeInfo ) {

	String name = getAttributeOrDefault( nodeInfo, "name", "" );
	boolean symmetric = getAttributeOrDefault( nodeInfo, "symmetric", Boolean.FALSE );
	boolean transitive = getAttributeOrDefault( nodeInfo, "transitive", Boolean.FALSE );

	Relationship relationship = new Relationship( name, transitive, symmetric );

	nodeInfo.getParentObject().addRelationship( relationship );

	return relationship;
    }

}
