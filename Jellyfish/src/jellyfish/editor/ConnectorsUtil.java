/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor;


import jellyfish.editor.controller.RelationshipController;
import jellyfish.editor.controller.PersistenceContext;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jellyfish.triplestore.BaseEntityComparator;
import jellyfish.editor.model.Connector;
import jellyfish.editor.model.ConnectorPoint;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;

/**
 *
 * @author Xevia
 */
class ConnectorsUtil {

    private static final Color TRIANGLE_COLOR = new Color(0, 64, 0);
    private static final Color NORMAL_LINE_COLOR = Color.BLACK;
    private static final Color HIGHLIGHTED_LINE_COLOR = Color.RED;
    
    private static final Stroke NORMAL_STROKE = new BasicStroke();
    private static final Stroke DASHED_STROKE = new BasicStroke(1.0f, 1, 1, 1.0f, new float[]{5.0f,2.0f}, 0.0f);

    private PanelDiagram diagram;
    private DrawDiagramHelper drawDiagramUtils;
    private PersistenceContext persistenceContext;
    private RelationshipController relashionshipsHolder;

    private Map<PanelEntityBox,Map<PanelEntityBox,Map<Relationship,Connector>>>
            entityConnectors = new TreeMap(new PanelEntityBoxComparator());

    public ConnectorsUtil(PanelDiagram diagram, DrawDiagramHelper drawDiagramUtils, PersistenceContext persistenceContext, RelationshipController relashionshipsHolder) {
        this.diagram = diagram;
        this.drawDiagramUtils = drawDiagramUtils;
        this.persistenceContext = persistenceContext;
        this.relashionshipsHolder = relashionshipsHolder;
    }
    
    public void removeEntityBox(PanelEntityBox panelEntityBox)
    {
        if (entityConnectors.containsKey(panelEntityBox)) {
            entityConnectors.remove(panelEntityBox);
        }

        for (PanelEntityBox src:entityConnectors.keySet()) {
            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);

            if (dstMap.containsKey(panelEntityBox)) {
                dstMap.remove(panelEntityBox);
            }
        }
    }

    public void removeRelationship(Relationship r)
    {
        for (PanelEntityBox src:entityConnectors.keySet()) {
            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);

            for (PanelEntityBox dst:dstMap.keySet()) {
                Map<Relationship,Connector> relMap = dstMap.get(dst);

                if (relMap.containsKey(r)) {
                    Connector con = relMap.get(r);

                    List<PanelConnectorPoint> connectorPoints = new ArrayList<PanelConnectorPoint>();

                    for (Component c:diagram.getComponents()) {
                        if (c instanceof PanelConnectorPoint) {
                            PanelConnectorPoint pcp = (PanelConnectorPoint)c;
                            if (pcp.getConnectorPoint().getConnector().equals(con)) {
                                connectorPoints.add((PanelConnectorPoint)c);
                            }
                        }
                    }

                    for (PanelConnectorPoint pcp:connectorPoints)
                        diagram.remove(pcp);

                    relMap.remove(r);
                }
            }
        }
    }

    public boolean hasRelationship(Relationship r)
    {
        for (PanelEntityBox src:entityConnectors.keySet()) {
            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);

            for (PanelEntityBox dst:dstMap.keySet()) {
                Map<Relationship,Connector> relMap = dstMap.get(dst);

                if (relMap.containsKey(r)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public Connector getConnector(PanelEntityBox src, PanelEntityBox dst, Relationship rel)
    {
        Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);
        if (dstMap==null) {
            return null;
        }
        Map<Relationship,Connector> relMap = dstMap.get(dst);
        if (relMap==null) {
            return null;
        }
        return relMap.get(rel);
    }

    public void addConnector(PanelEntityBox src, PanelEntityBox dst, Connector con)
    {
        Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);
        if (dstMap==null) {
            dstMap = new TreeMap(new PanelEntityBoxComparator());
            entityConnectors.put(src, dstMap);
        }

        Map<Relationship,Connector> relMap = dstMap.get(dst);
        if (relMap==null) {
            relMap = new TreeMap(new BaseEntityComparator());
            dstMap.put(dst, relMap);
        }
        relMap.put(con.getTriple().getPredicate(), con);

        con.getTriple().removePropertyChangeListeners(Triple.ATTR_PREDICATE.getName(), PredicateChangeListener.class);
        con.getTriple().addPropertyChangeListener(Triple.ATTR_PREDICATE.getName(), new PredicateChangeListener(src, dst));
    }
    
    public ConnectorInterceptDetails findConnectorAt(Point pt)
    {
        if (diagram.getComponentAt(pt)!=diagram) {
            return null;
        }

        ConnectorInterceptDetails details=null;

        for (PanelEntityBox src:entityConnectors.keySet()) {
            Point p1 = drawDiagramUtils.calcCenter(src);
            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);

            for (PanelEntityBox dst:dstMap.keySet()) {
                Point p2 = drawDiagramUtils.calcCenter(dst);
                Map<Relationship,Connector> relMap = dstMap.get(dst);

                for (Relationship rel:relMap.keySet()) {
                    Connector connector = relMap.get(rel);

                    if (connector==null) continue;

                    Point prevPt = p1;

                    int prevIndex = 0;
                    for (ConnectorPoint cp:connector.getConnectorPoints()) {
                        Point cpPt = new Point(cp.getX(), cp.getY());
                        if (drawDiagramUtils.pointInLine(prevPt, cpPt, pt)) {
                            ConnectorInterceptDetails newDetails  =
                                    new ConnectorInterceptDetails(
                                            src, dst,
                                            rel, connector,
                                            prevIndex,
                                            prevPt, cpPt, pt
                                        );
                            if (details==null || details.height<newDetails.height)
                                details = newDetails;
                        }
                        prevPt = cpPt;
                        ++prevIndex;
                    }

                    if (drawDiagramUtils.pointInLine(prevPt, p2, pt)) {
                        ConnectorInterceptDetails newDetails  =
                                new ConnectorInterceptDetails(
                                        src, dst,
                                        rel, connector,
                                        prevIndex,
                                        prevPt, p2, pt
                                    );
                        if (details==null || details.height>newDetails.height)
                            details = newDetails;
                    }

                }
            }
        }

        return details;
    }

    public void paintConnectors(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        
        for (PanelEntityBox src:entityConnectors.keySet()) {
            Point p1 = drawDiagramUtils.calcCenter(src);
            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);

            for (PanelEntityBox dst:dstMap.keySet()) {
                Point p2 = drawDiagramUtils.calcCenter(dst);
                Map<Relationship,Connector> relMap = dstMap.get(dst);

                Point lastPt = p1;

                for (Relationship rel:relMap.keySet()) {
                    Color lineColor = NORMAL_LINE_COLOR;
                    Stroke stroke = NORMAL_STROKE;

                    if (rel.isTransitive())
                        stroke = DASHED_STROKE;

                    if (relashionshipsHolder.isHighlightRelationship()
                            && relashionshipsHolder.getSelectedRelationship()!=null
                            && relashionshipsHolder.getSelectedRelationship().equals(rel))
                        lineColor = HIGHLIGHTED_LINE_COLOR;
                    
                    Connector connector = relMap.get(rel);

                    for (ConnectorPoint cp:connector.getConnectorPoints()) {
                        Point pt = new Point(cp.getX(),cp.getY());

                        g2d.setStroke(stroke);
                        g2d.setColor(lineColor);
                        g2d.drawLine(lastPt.x, lastPt.y, pt.x, pt.y);

                        if (lastPt.equals(p1)) {
                            Rectangle rc = src.getBounds();
                            Point exit = drawDiagramUtils.calcExitPoint(rc, lastPt, pt);
                            if (exit!=null)
                                lastPt = exit;
                        }

                        if (!rel.isSymmetric()) {
                            g2d.setStroke(NORMAL_STROKE);
                            g2d.setColor(TRIANGLE_COLOR);
                            drawDiagramUtils.paintCenterTriangle(g2d, lastPt, pt);
                        }

                        lastPt = pt;
                    }
                    
                    if (lastPt.equals(p1)) {
                        Rectangle rc = src.getBounds();
                        Point exit = drawDiagramUtils.calcExitPoint(rc, lastPt, p2);
                        if (exit!=null)
                            lastPt = exit;
                    }

                    {
                        Rectangle rc = dst.getBounds();
                        Point exit = drawDiagramUtils.calcExitPoint(rc, p2, lastPt);
                        if (exit!=null)
                            p2 = exit;
                    }

                    g2d.setStroke(stroke);
                    g2d.setColor(lineColor);
                    g2d.drawLine(lastPt.x, lastPt.y, p2.x, p2.y);
                    if (!rel.isSymmetric()) {
                        g2d.setStroke(NORMAL_STROKE);
                        g2d.setColor(TRIANGLE_COLOR);
                        drawDiagramUtils.paintCenterTriangle(g2d, lastPt, p2);
                    }
                }

            }
        }

        g2d.setStroke(NORMAL_STROKE);
    }

    private class PanelEntityBoxComparator implements Comparator<PanelEntityBox> {
        public int compare(PanelEntityBox o1, PanelEntityBox o2) {
            return o1.getEntityBox().getEntity().getName().compareTo(o2.getEntityBox().getEntity().getName());
        }
    }

    public class ConnectorInterceptDetails {
        public final PanelEntityBox src;
        public final PanelEntityBox dst;
        public final Relationship relationship;
        public final Connector connector;
        public final int insertionIndex;
        public final Point srcPt, dstPt;
        public final Point mousePt;
        public final double height;
        public PanelConnectorPoint pcp;

        public ConnectorInterceptDetails(PanelEntityBox src, PanelEntityBox dst, Relationship relationship, Connector connector, int insertionIndex, Point srcPt, Point dstPt, Point mousePt) {
            this.src = src;
            this.dst = dst;
            this.relationship = relationship;
            this.connector = connector;
            this.insertionIndex = insertionIndex;
            this.srcPt = srcPt;
            this.dstPt = dstPt;
            this.mousePt = mousePt;
            this.height = calcHeight();
        }


        private double calcHeight() {
            double srcHeight = diagram.getComponentZOrder(src);
            double dstHeight = diagram.getComponentZOrder(dst);
            double len_src_pt = (srcPt.x-mousePt.x)*(srcPt.x-mousePt.x) +
                                (srcPt.y-mousePt.y)*(srcPt.y-mousePt.y);
            double len_src_dst = (srcPt.x-dstPt.x)*(srcPt.x-dstPt.x) +
                                (srcPt.y-dstPt.y)*(srcPt.y-dstPt.y);
            return Math.min(srcHeight,dstHeight)+
                    ((Math.abs(srcHeight-dstHeight)*len_src_pt)/len_src_dst);
        }
    }

    private class PredicateChangeListener implements PropertyChangeListener {

        private PanelEntityBox src, dst;

        public PredicateChangeListener(PanelEntityBox src, PanelEntityBox dst) {
            this.src = src;
            this.dst = dst;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Relationship oldPredicate = (Relationship)evt.getOldValue();
            Relationship newPredicate = (Relationship)evt.getNewValue();


            Map<PanelEntityBox, Map<Relationship,Connector>> dstMap = entityConnectors.get(src);
            Map<Relationship,Connector> relMap = dstMap.get(dst);

            Connector connector = relMap.get(oldPredicate);
            relMap.remove(oldPredicate);
            relMap.put(newPredicate, connector);

            diagram.invokeRepaint(false);
        }
    }
}
