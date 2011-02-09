/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.xml;

import java.util.*;
import org.w3c.dom.Node;

/**
 *
 * @author Xevia
 */
public class XmlCommon {

    private Map<Short, String> nodeTypeDesc;
    private Map<Short, Boolean> nodeTypeHasName;
    
    public XmlCommon() {
        this.nodeTypeDesc = new HashMap<Short, String>(12);
        nodeTypeDesc.put(Node.ATTRIBUTE_NODE, "Attribute");
        nodeTypeDesc.put(Node.CDATA_SECTION_NODE, "CDATA Section");
        nodeTypeDesc.put(Node.COMMENT_NODE, "Comment");
        nodeTypeDesc.put(Node.DOCUMENT_FRAGMENT_NODE, "Document Fragment");
        nodeTypeDesc.put(Node.DOCUMENT_NODE, "Document");
        nodeTypeDesc.put(Node.DOCUMENT_TYPE_NODE, "Document Type");
        nodeTypeDesc.put(Node.ELEMENT_NODE, "Element");
        nodeTypeDesc.put(Node.ENTITY_NODE, "Entity");
        nodeTypeDesc.put(Node.ENTITY_REFERENCE_NODE, "Entity Reference");
        nodeTypeDesc.put(Node.NOTATION_NODE, "Notation");
        nodeTypeDesc.put(Node.PROCESSING_INSTRUCTION_NODE, "Processing Instruction");
        nodeTypeDesc.put(Node.TEXT_NODE, "Text");

        this.nodeTypeHasName = new HashMap<Short, Boolean>(12);
        nodeTypeHasName.put(Node.ATTRIBUTE_NODE, true);
        nodeTypeHasName.put(Node.CDATA_SECTION_NODE, false);
        nodeTypeHasName.put(Node.COMMENT_NODE, false);
        nodeTypeHasName.put(Node.DOCUMENT_FRAGMENT_NODE, false);
        nodeTypeHasName.put(Node.DOCUMENT_NODE, false);
        nodeTypeHasName.put(Node.DOCUMENT_TYPE_NODE, true);
        nodeTypeHasName.put(Node.ELEMENT_NODE, true);
        nodeTypeHasName.put(Node.ENTITY_NODE, true);
        nodeTypeHasName.put(Node.ENTITY_REFERENCE_NODE, true);
        nodeTypeHasName.put(Node.NOTATION_NODE, true);
        nodeTypeHasName.put(Node.PROCESSING_INSTRUCTION_NODE, true);
        nodeTypeHasName.put(Node.TEXT_NODE, false);
    }

    public String getNodeTypeDesc(Short v) {
        return nodeTypeDesc.get(v);
    }

    public boolean getNodeTypeHasName(Short v) {
        if (!nodeTypeHasName.containsKey(v))
            throw new RuntimeException("Unknown Node Type: "+v);
        return nodeTypeHasName.get(v);
    }

    

}
