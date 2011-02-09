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
public class XmlNodeType implements Comparable<XmlNodeType> {

    private XmlCommon common;
    private short nodeType;
    private String nodeName;

    public XmlNodeType(XmlCommon common, short nodeType, String nodeName) {
        this.common = common;
        this.nodeType = nodeType;
        this.nodeName = nodeName;
        if (this.nodeName==null)
            this.nodeName = "";
    }

    public XmlNodeType(XmlCommon common, short nodeType) {
        this(common,nodeType,"");
    }

    public XmlNodeType(XmlCommon common, Node node) {
        this(common,node.getNodeType(),node.getNodeName());
    }

    public boolean isText() {
        return nodeType==Node.TEXT_NODE;
    }

    public boolean isComment() {
        return nodeType==Node.COMMENT_NODE;
    }

    public int compareTo(XmlNodeType o) {
        if (o==null)
            return -1;
        if (this.nodeType==o.nodeType) {
            if (common.getNodeTypeHasName(nodeType)==true)
                return this.nodeName.compareToIgnoreCase(o.nodeName);
            else
                return 0;
        } else
            return this.nodeType-o.nodeType;
    }

    @Override
    public String toString() {
        if (common.getNodeTypeHasName(nodeType)==true)
            return common.getNodeTypeDesc(nodeType);
        else
            return common.getNodeTypeDesc(nodeType)+"{'"+nodeName+"'}";
    }

    
}
