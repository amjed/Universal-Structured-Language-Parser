/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import jellyfish.triplestore.model.Triple;
import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class TripleParser extends jellyfish.xml.XmlNodeParser<Triple,XmlTripleStore> {

    @Override
    public Triple parse( XmlNodeInfo<XmlTripleStore> nodeInfo ) {
	
	Triple triple = new Triple();
	
	nodeInfo.getParentObject().addTriple( triple );

	return triple;
    }

}
