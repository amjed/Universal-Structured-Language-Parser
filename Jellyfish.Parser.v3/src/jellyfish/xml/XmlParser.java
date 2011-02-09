/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.xml;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Umran
 */
public class XmlParser {

    private XmlCommon xmlCommon = new XmlCommon();
    private Map<XmlNodeType, Set<XmlNodeType>> possibleChildNodes;
    private Map<XmlNodeType, XmlNodeParser> nodeParsers;

    public XmlParser( int numOfNodeTypes ) {
        this.possibleChildNodes = new TreeMap<XmlNodeType, Set<XmlNodeType>>();
        this.nodeParsers = new TreeMap<XmlNodeType, XmlNodeParser>();
    }

    public void addParser( XmlNodeType nodeType, XmlNodeParser nodeParser ) {
        nodeParsers.put( nodeType, nodeParser );
    }

    public void setPossibleChild( XmlNodeType parentNodeType,
                                  XmlNodeType childNodeType ) {
        if (!possibleChildNodes.containsKey( parentNodeType )) {
            possibleChildNodes.put( parentNodeType, new TreeSet<XmlNodeType>() );
        }
        possibleChildNodes.get( parentNodeType ).add( childNodeType );

        assert possibleChildNodes.containsKey( parentNodeType ) :
                "parent node type " + parentNodeType + " not registered.";

        assert possibleChildNodes.get( parentNodeType ).contains( childNodeType ) :
                "unable to set " + childNodeType + " as possible child of " + parentNodeType;
    }

    private class NodeDetailStruct {

        public Node node;
        public Object nodeObject;
        public XmlNodeType nodeType;
        public Node parentNode;
        public Object parentObject;
        public XmlNodeType parentType;
        public String location;

        public NodeDetailStruct( Node node, Object nodeObject, XmlNodeType nodeType, Node parentNode,
                                 Object parentObject, XmlNodeType parentType, String location )
        {
            this.node = node;
            this.nodeObject = nodeObject;
            this.nodeType = nodeType;
            this.parentNode = parentNode;
            this.parentObject = parentObject;
            this.parentType = parentType;
            this.location = location;
        }

        
    }

    synchronized public void parse( InputStream in ) throws
            ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.
                newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse( in );

        Queue<NodeDetailStruct> nodeQ = new LinkedList<NodeDetailStruct>();
        boolean hasProcessedRoot = false;

        {
            // normalize text representation
            doc.normalizeDocument();
            Element root = doc.getDocumentElement();
            root.normalize();
            XmlNodeType rootType = new XmlNodeType( xmlCommon, root );

            nodeQ.add( new NodeDetailStruct( root, null, rootType, null, null,
                                             null, root.getNodeName() ) );
        }

        while (!nodeQ.isEmpty()) {
            NodeDetailStruct currentParent = nodeQ.remove();

            if (!possibleChildNodes.containsKey( currentParent.nodeType )) {
                continue;
            }

//            System.out.println("parent=" + currentParent.nodeType + " (" + currentParent.nodeObject + ")" );

            NodeList children = currentParent.node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node childNode = children.item( i );
                XmlNodeType childType = new XmlNodeType( xmlCommon, childNode );

                String nodeLoc = childNode.getNodeName();
                {
                    //  compute number of sibling nodes of similar type and index of
                    //      this node among the siblings of similar type
                    int indexOfType = 0;
                    int totalOfType = 0;
                    for (int j = 0; j < children.getLength(); ++j) {
                        String nodeName = children.item( j ).getNodeName();
                        String childNodeName = childNode.getNodeName();
                        if (    childNode.getNodeType()==children.item(j).getNodeType() &&
                                ((nodeName==null && childNodeName==null) ||
                                (nodeName!=null && childNodeName!=null &&
                                    nodeName.equals(childNodeName))))
                        {
                            if (j<i)
                                ++indexOfType;
                            ++totalOfType;
                        }
                    }

                    if (totalOfType>1)
                        nodeLoc += "["+indexOfType+"]";

                    if (currentParent!=null)
                        nodeLoc = currentParent.location + "." + nodeLoc;
                }
                
//                System.out.println( "\tchild=" + childType );

                if (!possibleChildNodes.get( currentParent.nodeType ).contains(
                        childType )) {
                    if (childType.isText() && childNode.getNodeValue().trim().isEmpty()) {
                        //  ignore empty spaces returned as text
                        continue;
                    }

                    if (childType.isComment()) {
                        //  ignore comments
                        continue;
                    }

                    System.out.println(
                            "Warning: The child node type " + childType + " (location "+nodeLoc+")"
                            + " is unexpected for the parent node type "
                            + currentParent.nodeType + " (location "+currentParent.location+")" );
                    continue;
                }

                if (!nodeParsers.containsKey( childType )) {
                    /*
                    throw new RuntimeException(
                    "The Xml Node Parser for the node type "+childType+
                    " could not be found."
                    );
                     */
                    System.out.println( "Warning: "
                                        + "The Xml Node Parser for the node type " + childType
                                        + " (location "+nodeLoc+") "
                                        + " could not be found." );
                    continue;
                }

                XmlNodeParser parser = nodeParsers.get( childType );

                Object childObj = parser.parse( nodeLoc, childNode, currentParent.node,
                                                currentParent.nodeObject );

                if (childObj == null) {
                    continue;
                }

//                System.out.println( "\t\tparsed: " + childObj );

                if (possibleChildNodes.containsKey( currentParent.nodeType )) {
                    nodeQ.add(
                            new NodeDetailStruct(
                            childNode, childObj, childType,
                            currentParent.node, currentParent.parentObject,
                            currentParent.nodeType,
                            nodeLoc) );
                }
            }

            if (!hasProcessedRoot) {
                hasProcessedRoot = true;
            }
        }
    }
}
