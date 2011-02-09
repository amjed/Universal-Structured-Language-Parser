/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.xml;

import org.w3c.dom.Node;

/**
 *
 * @author Umran
 */
public abstract class XmlNodeParser<ReturnClass,ParentClass> {

    public abstract ReturnClass parse(String location, Node node, Node parentNode, ParentClass parentObject);

}
