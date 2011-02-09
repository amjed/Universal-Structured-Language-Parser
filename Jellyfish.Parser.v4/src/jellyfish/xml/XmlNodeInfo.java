/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.xml;

import org.w3c.dom.Node;

/**
 *
 * @author Xevia
 */
public interface XmlNodeInfo<ParentClass> {

    public String getLocation();

    public Node getNode();

    public int getNodeIndex();

    public Node getParentNode();

    public ParentClass getParentObject();
    
}
