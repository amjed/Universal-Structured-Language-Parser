/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.xml;

import java.text.ParseException;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jellyfish.common.PrimitiveParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Umran
 */
public abstract class XmlNodeParser<ReturnClass,ParentClass> {

    /*
     * Makes sure that the parent object is never null.
     * An runtime exception is thrown if parent object is null.
     * This is done for all nodes, except for the root node.
     */
    public boolean requiresParentObject() {
	return true;
    }

    /*
     * Specifies that the node that this parser parses is required to be in a certain given
     * indexes only. As such if it appears at another index of the parent, an exception is thrown.
     */
    public boolean requiresExactParentIndex() {
	return false;
    }

    /*
     * Returns the indexes that this node is allowed to be in.
     * First node of the parent is index 0 (zero). Comments and empty spaces aren't counted.
     */
    public Set<Integer> getValidParentIndexes() {
	return Collections.EMPTY_SET;
    }

    /*
     * Returns the attributes that this node requires defined.
     */
    public Set<String> getRequiredAttributes() {
	return Collections.EMPTY_SET;
    }

    /*
     * Obtains the value of the attribute giving the default value if the attribute is not found.
     */
    protected String getAttributeOrDefault( XmlNodeInfo<ParentClass> nodeInfo, String attrName, String defValue) {
	NamedNodeMap namedNodeMap = nodeInfo.getNode().getAttributes();
	if (namedNodeMap!=null) {
	    Node attrNode = namedNodeMap.getNamedItem( attrName );
	    if (attrNode==null)
		return defValue;

	    String value = attrNode.getNodeValue();
	    if (value==null || value.isEmpty())
		return defValue;
	    
	    return value.trim();
	} else
	    return defValue;
    }

    /*
     * Obtains the value of the attribute giving the default value if the attribute is not found.
     *
     * Please note that only attributes of primitive types are supported.
     * The default value (defValue) parameter determines how the attribute is parsed and converted
     * to the resultant type.
     */
    protected <E> E getAttributeOrDefault( XmlNodeInfo<ParentClass> nodeInfo, String attrName, E defValue)  {
	NamedNodeMap namedNodeMap = nodeInfo.getNode().getAttributes();
	if (namedNodeMap!=null) {
	    Node attrNode = namedNodeMap.getNamedItem( attrName );
	    if (attrNode==null)
		return defValue;

	    String strValue = attrNode.getNodeValue();
	    if (strValue==null || strValue.isEmpty())
		return defValue;

	    PrimitiveParser parser = PrimitiveParser.getInstance();
	    try {
		return parser.parse( strValue, (Class<E>) defValue.getClass() );
	    } catch ( ParseException ex ) {
		throw new RuntimeException( 
			"The value '" + strValue + "' in attribute '" + attrName +
			"' in node "+nodeInfo.getLocation()+" is expected to be of type " +
			defValue.getClass().getSimpleName(),
			ex );
	    }
	} else
	    return defValue;
    }

    public abstract ReturnClass parse( XmlNodeInfo<ParentClass> nodeInfo );

}
