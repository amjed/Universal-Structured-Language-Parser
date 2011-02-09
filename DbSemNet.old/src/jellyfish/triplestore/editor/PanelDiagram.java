/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelDiagram.java
 *
 * Created on Oct 31, 2010, 12:31:12 PM
 */

package jellyfish.triplestore.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import jellyfish.dnd.DndTarget;
import jellyfish.dnd.DndTargetEventListener;
import jellyfish.dnd.DragContainer;
import jellyfish.dnd.DragContainerListener;

/**
 *
 * @author Xevia
 */
public class PanelDiagram extends javax.swing.JPanel 
{
    private static final Border normalBorder = new BevelBorder(BevelBorder.LOWERED);
    private static final Border dropBorder = new LineBorder(Color.RED, 2);

    private DndTarget dndTarget;
    private DragContainer dragContainer;

    /** Creates new form PanelDiagram */
    public PanelDiagram() {
        initComponents();
        doAllowDragDrop();
    }

    private void doAllowDragDrop() {
        
        dndTarget = new DndTarget(this, new DndTargetEventListener() {
            public boolean canAcceptType(Class type) {
                System.out.println("drop area checking type"+type);
                return PanelEntityBox.class.isAssignableFrom(type);
            }

            public void acceptObject(Object object, Point location) {
                System.out.println("accepting object");
                if (PanelEntityBox.class.isAssignableFrom(object.getClass())) {
                    PanelEntityBox entityBox = (PanelEntityBox)object;
                    PanelDiagram.this.add(entityBox);
                    entityBox.setLocation(location);
                    PanelDiagram.this.validate();
                    PanelDiagram.this.repaint();
                    System.out.println("panel added");
                }
            }
        });

        dragContainer = new DragContainer(this, new DragContainerListener() {

            public void dragStarted(Container parent, Component c) {
                if (c instanceof JComponent) {
                    JComponent jComponent = (JComponent)c;
                    Border prevBorder = jComponent.getBorder();
                    LineBorder lineBorder = new LineBorder(Color.RED, 2);
                    CompoundBorder compoundBorder = new CompoundBorder(lineBorder, prevBorder);
                    jComponent.setBorder(compoundBorder);
                    jComponent.validate();
                    jComponent.repaint();
                }
            }

            public void dragEnded(Container parent, Component c) {
                if (c instanceof JComponent) {
                    JComponent jComponent = (JComponent)c;
                    CompoundBorder compoundBorder = (CompoundBorder)jComponent.getBorder();
                    jComponent.setBorder(compoundBorder.getInsideBorder());
                    jComponent.validate();
                    jComponent.repaint();
                }
            }

            public void moveComponent(Container parent, Component c, Point oldLocation, Point newLocation) {
                c.setLocation(newLocation);
                System.out.println("setting component movement");
            }
        });
        
    }

    public final void setDraggingActive(boolean draggingActive) {
        dragContainer.setDraggingActive(draggingActive);
    }

    public final boolean isDraggingActive() {
        return dragContainer.isDraggingActive();
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
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                formComponentAdded(evt);
            }
        });
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentAdded

        if (evt.getChild() instanceof PanelEntityBox) {
            dragContainer.addDraggableComponent(evt.getChild());
        }

    }//GEN-LAST:event_formComponentAdded



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}