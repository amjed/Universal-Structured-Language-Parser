/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class RelationshipCollectionParser extends jellyfish.xml.XmlNodeParser<XmlTripleStore,XmlTripleStore> {

    private static final Set<Integer> VALID_INDEXES = new HashSet<Integer>(Arrays.asList( 1 ));

    @Override
    public boolean requiresExactParentIndex() {
	return true;
    }

    @Override
    public Set<Integer> getValidParentIndexes() {
	return VALID_INDEXES;
    }

    @Override
    public XmlTripleStore parse( XmlNodeInfo<XmlTripleStore> nodeInfo ) {
	return nodeInfo.getParentObject();
    }

}
