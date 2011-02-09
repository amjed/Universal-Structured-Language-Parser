/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.xml.parsers;

import jellyfish.triplestore.xml.XmlTripleStore;
import jellyfish.xml.XmlNodeInfo;

/**
 *
 * @author Xevia
 */
public class RootParser extends jellyfish.xml.XmlNodeParser<XmlTripleStore,Object> {

    private XmlTripleStore xmlTripleStore;

    public RootParser( XmlTripleStore xmlTripleStore ) {
	this.xmlTripleStore = xmlTripleStore;
    }

    @Override
    public XmlTripleStore parse( XmlNodeInfo<Object> nodeInfo ) {
	return xmlTripleStore;
    }

}
