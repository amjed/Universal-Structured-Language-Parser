/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelDiagram.java
 *
 * Created on Oct 31, 2010, 12:31:12 PM
 */

package jellyfish.editor;

import jellyfish.editor.controller.RelationshipController;
import jellyfish.editor.controller.PersistenceContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.dnd.DndSource;
import jellyfish.dnd.DndSourceEventListener;
import jellyfish.dnd.DndTarget;
import jellyfish.dnd.DndTargetEventListener;
import jellyfish.dnd.DragContainer;
import jellyfish.dnd.DragContainerListener;
import jellyfish.editor.controller.AbstractController;
import jellyfish.editor.controller.ConnectorController;
import jellyfish.editor.model.Connector;
import jellyfish.editor.model.ConnectorPoint;
import jellyfish.editor.model.EntityBox;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author Xevia
 */
class PanelDiagram extends DragContainer
{
    private static final Border normalBorder = new BevelBorder(BevelBorder.LOWERED);
    private static final Border dropBorder = new LineBorder(Color.RED, 2);
    
    private JFrame mainFrame;
    private DndTarget dndTarget;
    private Map<Component,DndSource> componentDndSources =
            new TreeMap(new ComponentComparator());
    private Map<Component,DndTarget> componentDndTargets =
            new TreeMap(new ComponentComparator());

    private PersistenceContext persistenceContext;
    private RelationshipController relashionshipsHolder;
    private ConnectorController connectorController;

    private DrawDiagramHelper drawDiagramUtils;
    private ConnectorsUtil drawConnectorsUtil;
    
    private Point lastPressedPoint=null;
    private ConnectorsUtil.ConnectorInterceptDetails lastPressedLine=null;

    /** Creates new form PanelDiagram */
    public PanelDiagram(
            JFrame mainFrame,
            PersistenceContext persistenceContext,
            RelationshipController relashionshipsHolder,
            ConnectorController connectorController
            ) {
        this.mainFrame = mainFrame;
        this.persistenceContext = persistenceContext;
        this.relashionshipsHolder = relashionshipsHolder;
        this.connectorController = connectorController;
        this.setDragContainerListener(new DragListener());

        initComponents();
        this.setSize(this.getPreferredSize());

        this.drawDiagramUtils = new DrawDiagramHelper(this);
        this.drawConnectorsUtil =  new ConnectorsUtil(
                this, drawDiagramUtils, this.persistenceContext, relashionshipsHolder);

        initDiagramDnd();

        this.relashionshipsHolder.addPropertyChangeListener(
                RelationshipController.ATTR_HIGHLIGHT_RELATIONSHIP,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        invokeRepaint(false);
                    }
                }
        );

        this.relashionshipsHolder.addPropertyChangeListener(
                AbstractController.ATTR_SELECTED_ITEM,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (PanelDiagram.this.relashionshipsHolder.isHighlightRelationship()) {
                            invokeRepaint(false);
                        }
                    }
                }
        );

        List<Relationship> listRelationships = this.relashionshipsHolder.getItemList();
        if (listRelationships instanceof  org.jdesktop.observablecollections.ObservableList) {
            org.jdesktop.observablecollections.ObservableList obsList
                    = (org.jdesktop.observablecollections.ObservableList) listRelationships;
            obsList.addObservableListListener(new ObservableListListener() {

                public void listElementsAdded(ObservableList list, int index, int length) {

                }

                public void listElementsRemoved(ObservableList list, int index, List oldElements) {
                    for (Object o:oldElements) {
                        drawConnectorsUtil.removeRelationship((Relationship)o);
                    }
                    invokeRepaint(false);
                }

                public void listElementReplaced(ObservableList list, int index, Object oldElement) {

                }

                public void listElementPropertyChanged(ObservableList list, int index) {

                }
            });
        }

        loadFromDb();
    }
    
    private void initDiagramDnd() {
        dndTarget = new DndTarget(this, new DndTargetEventListener() {
            public boolean canAcceptType(Class type) {
                return PanelEntityBox.class.isAssignableFrom(type);
            }

            public void acceptObject(Object object, Point location) {
                System.out.println("accepting object");
                if (PanelEntityBox.class.isAssignableFrom(object.getClass())) {
                    PanelEntityBox peb = (PanelEntityBox)object;
                    addPanelEntityBox(peb, location);

                    persistenceContext.getEntityManager().persist(peb.getEntityBox().getEntity());
                    persistenceContext.getEntityManager().persist(peb.getEntityBox());
                    persistenceContext.save();
                    
                    invokeRepaint(true);
                }
            }
        });
        
    }

    @Override
    public boolean canDragFrom(Component component, Point start) {
        if (component instanceof PanelEntityBox) {
            PanelEntityBox panelEntityBox = (PanelEntityBox)component;
            JPanel dragPanel = panelEntityBox.getDragPanel();
            Rectangle rc = SwingUtilities.convertRectangle(dragPanel.getParent(), dragPanel.getBounds(), component);
            return rc.contains(start);
        } else {
            return true;
        }
    }

    private void loadFromDb()
    {
        List<EntityBox> entityBoxs =
                PersistenceUtil.getAllList(persistenceContext.getEntityManager(), EntityBox.class);
        for (EntityBox box:entityBoxs) {
            PanelEntityBox peb = new PanelEntityBox(mainFrame, box);
            Point loc = new Point(box.getX(), box.getY());
            peb.setPreferredSize(new Dimension(box.getWidth(), box.getHeight()));
            addPanelEntityBox(peb, loc);
        }

        List<Connector> connectors =
                PersistenceUtil.getAllList(persistenceContext.getEntityManager(), Connector.class);
        for (Connector c:connectors)
        {
            addConnector(c);
        }
    }
    
    private void addPanelEntityBox(final PanelEntityBox panelEntityBox, final Point location)
    {
        panelEntityBox.setLocation(
                drawDiagramUtils.repelBorders(
                drawDiagramUtils.calcCenterGridAlignedLoc(
                    location, panelEntityBox.getSize()),
                    panelEntityBox.getSize()));
        panelEntityBox.setSize(panelEntityBox.getPreferredSize());
        this.add(panelEntityBox);

        componentDndSources.put(panelEntityBox,
                new DndSource(panelEntityBox.getLinkPanel(), new DndSourceEventListener() {
            public Object getTransferedObject(Component component) {

                Relationship relationship = getSelectedRelationship();

                if (relationship==null) return null;

                return new NewConnectionDetails(panelEntityBox, relationship);
            }

            public void componentDropped(Container prevParent, Component component) {
                
            }
        }));

        componentDndTargets.put(panelEntityBox,
                new DndTarget(panelEntityBox, new DndTargetEventListener() {

            public boolean canAcceptType(Class type) {
                return type.equals(NewConnectionDetails.class);
            }

            public void acceptObject(Object object, Point location) {
                if (object.getClass().equals(NewConnectionDetails.class)) {
                    NewConnectionDetails details = (NewConnectionDetails)object;
                    if (!details.srcPanelEntityBox.equals(panelEntityBox)) {
                        createConnector(
                                details.srcPanelEntityBox,
                                panelEntityBox,
                                details.relationship);
                    }
                }
            }
        }));
    }

    private void addConnector(PanelEntityBox src, PanelEntityBox dst, Connector connector)
    {
        drawConnectorsUtil.addConnector(
                        src,
                        dst,
                        connector    );
    }

    private void addConnector(Connector connector)
    {
        PanelEntityBox src = null;
        PanelEntityBox dst = null;

        for (Component c:getComponents()) {
            if (c instanceof PanelEntityBox) {
                PanelEntityBox peb = (PanelEntityBox)c;

                if (peb.getEntityBox().getEntity().getName().equals(
                        connector.getSrcBox().getEntity().getName())) {
                    if (src!=null) {
                        System.out.println("Two existing Panel were found to have the entity '"+connector.getSrcBox().getEntity().getName()+"'");
                        return;
                    }
                    src = peb;
                }
                if (peb.getEntityBox().getEntity().getName().equals(
                        connector.getDstBox().getEntity().getName())) {
                    if (dst!=null) {
                        System.out.println("Two existing Panel were found to have the entity '"+connector.getSrcBox().getEntity().getName()+"'");
                        return;
                    }
                    dst = peb;
                }
            }
        }

        if (src==null) {
            System.out.println("The source end of the connector "+connector+" could not be found");
            return;
        }
        if (dst==null) {
            System.out.println("The destination end of the connector "+connector+" could not be found");
            return;
        }

        addConnector(src, dst, connector);

        for (ConnectorPoint cp:connector.getConnectorPoints()) {
            addConnectorPoint(cp);
        }
    }

    private void createConnector(PanelEntityBox src, PanelEntityBox dst, Relationship rel)
    {
        if (drawConnectorsUtil.getConnector(src,dst,rel)==null) {
            Triple triple = new Triple(
                            src.getEntityBox().getEntity(),
                            rel,
                            dst.getEntityBox().getEntity()
                            );
            Connector connector = new Connector(
                            src.getEntityBox(),
                            dst.getEntityBox(),
                            triple
                            );

            addConnector(src, dst, connector);

            persistenceContext.getEntityManager().persist(triple);
            persistenceContext.getEntityManager().persist(connector);
            persistenceContext.save();

            invokeRepaint(false);
        }
    }

    private Relationship getSelectedRelationship()
    {
        Relationship r = relashionshipsHolder.getSelectedRelationship();

        if (r!=null)
            initRelationshipListener(r);

        return r;
    }
    
    private class RelationshipListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            PanelDiagram.this.invokeRepaint(false);
        }
        
    }

    private void initRelationshipListener(Relationship r)
    {
        if (!r.containsPropertyChangeListener(
                Relationship.ATTR_SYMMETRIC.getName(),
                RelationshipListener.class)) {
            r.addPropertyChangeListener(
                    Relationship.ATTR_SYMMETRIC.getName(),
                    new RelationshipListener());
        }

        if (!r.containsPropertyChangeListener(
                Relationship.ATTR_TRANSITIVE.getName(),
                RelationshipListener.class)) {
            r.addPropertyChangeListener(
                    Relationship.ATTR_TRANSITIVE.getName(),
                    new RelationshipListener());
        }
    }

    public void invokeRepaint(boolean revalidate)
    {
        if (revalidate)
            validate();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PanelDiagram.this.repaint();
            }
        });
    }

    protected PanelConnectorPoint addConnectorPoint(ConnectorPoint cp)
    {
        PanelConnectorPoint pcp = new PanelConnectorPoint(cp);
        Point pt = new Point(cp.getX()-pcp.getWidth()/2, cp.getY()-pcp.getHeight()/2);
        pcp.setLocation(
                drawDiagramUtils.repelBorders(
                drawDiagramUtils.calcCenterGridAlignedLoc(
                    pt, pcp.getSize()),
                    pcp.getSize())
                    );

        pcp.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                PanelConnectorPoint pcp = (PanelConnectorPoint)e.getComponent();
                ConnectorPoint cp = pcp.getConnectorPoint();
                connectorController.setSelectedConnector(cp.getConnector());
            }

        });
        
        this.add(pcp);
        return pcp;
    }

    protected PanelConnectorPoint createConnectorPoint(Connector con, int insertionIndex, Point loc)
    {
        ConnectorPoint cp = new ConnectorPoint(con, loc.x, loc.y);
        con.getConnectorPoints().add(insertionIndex,cp);

        PanelConnectorPoint pcp = addConnectorPoint(cp);

        persistenceContext.getEntityManager().persist(cp);
        persistenceContext.getEntityManager().merge(con);
        persistenceContext.save();

        return pcp;
    }


    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        if (drawConnectorsUtil!=null)
            drawConnectorsUtil.paintConnectors(g);
        super.paintChildren(g);
        super.paintBorder(g);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(javax.swing.UIManager.getDefaults().getColor("desktop"));
        setBorder(normalBorder);
        setMaximumSize(new java.awt.Dimension(2000, 1000));
        setMinimumSize(new java.awt.Dimension(2000, 1000));
        setPreferredSize(new java.awt.Dimension(2000, 1000));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved

        if (componentDndSources.containsKey(evt.getChild())) {
            componentDndSources.remove(evt.getChild());
        }

        if (evt.getChild() instanceof PanelEntityBox) {
            drawConnectorsUtil.removeEntityBox((PanelEntityBox)evt.getChild());
        }

    }//GEN-LAST:event_formComponentRemoved

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed

        ConnectorsUtil.ConnectorInterceptDetails cid = drawConnectorsUtil.findConnectorAt(evt.getPoint());
        
        if (cid==null) return;
        
        lastPressedLine = cid;
        lastPressedPoint = evt.getPoint();
        

    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased

        if (lastPressedLine!=null) {
//            relashionshipsHolder.setSelectedRelationship(lastPressedLine.relationship);
            connectorController.setSelectedConnector(lastPressedLine.connector);
        }
        lastPressedLine = null;
        lastPressedPoint = null;
        
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

        if (lastPressedLine!=null) {
            if (lastPressedLine.pcp==null) {
                PanelConnectorPoint pcp = createConnectorPoint(
                        lastPressedLine.connector,
                        lastPressedLine.insertionIndex,
                        evt.getPoint());

                lastPressedLine.pcp = pcp;

                this.validate();
                this.dragComponent(lastPressedPoint, pcp);
            }

        }

    }//GEN-LAST:event_formMouseDragged



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


    private class NewConnectionDetails {
        public final PanelEntityBox srcPanelEntityBox;
        public final Relationship relationship;

        public NewConnectionDetails(PanelEntityBox srcPanelEntityBox, Relationship relationship) {
            this.srcPanelEntityBox = srcPanelEntityBox;
            this.relationship = relationship;
        }
        
    }

    private class ComponentComparator implements Comparator<Component> {
        public int compare(Component o1, Component o2) {
            return o1.hashCode()-o2.hashCode();
        }
    }

    private class DragListener implements DragContainerListener {

        public void dragStarted(Container parent, Component c) {
            if (c instanceof JComponent) {
                JComponent jComponent = (JComponent) c;
                Border prevBorder = jComponent.getBorder();
                LineBorder lineBorder = new LineBorder(Color.RED, 2);
                CompoundBorder compoundBorder = new CompoundBorder(lineBorder, prevBorder);
                jComponent.setBorder(compoundBorder);
                jComponent.repaint();
            }
        }

        public void dragEnded(Container parent, Component c) {
            if (c instanceof JComponent) {
                JComponent jComponent = (JComponent) c;
                CompoundBorder compoundBorder = (CompoundBorder) jComponent.getBorder();
                jComponent.setBorder(compoundBorder.getInsideBorder());
                jComponent.repaint();
            }
        }

        public void moveComponent(Container parent, Component c, Point oldLocation, Point newLocation) {
            c.setLocation(
                    drawDiagramUtils.repelBorders(
                    drawDiagramUtils.calcCenterGridAlignedLoc(
                    newLocation, c.getSize()), c.getSize()));
            invokeRepaint(false);
        }
    }
}
