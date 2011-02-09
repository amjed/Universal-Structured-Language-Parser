/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Test.java
 *
 * Created on Nov 1, 2010, 4:02:31 PM
 */

package jellyfish.dnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Xevia
 */
public class Test extends javax.swing.JFrame {

    private DndSource newButtonSource;
    private DndSource otherButtonSource;
    private DndTarget target;
    private DragContainer dragParent;

    private List<DndSource> dragSources = new ArrayList<DndSource>();

    private JPanel createDraggablePane() {
        System.out.println("creating a new draggable pane");
        String value = JOptionPane.showInputDialog("Say what?");

        if (value==null)
            return null;

        JPanel pane = new JPanel();
        JLabel lbl = new JLabel(value);
        lbl.setHorizontalAlignment(JLabel.CENTER);

        pane.setLayout(new BorderLayout());
        pane.add(lbl, BorderLayout.CENTER);
        pane.setSize(100, 50);
        pane.setBackground(Color.CYAN);
        pane.setBorder(new LineBorder(Color.BLACK, 1));

        dragSources.add(makeDraggablePaneProducer(pane));

        pane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("draggable-mouse-pressed");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("draggable-mouse-released");
            }

        });
        
        return pane;
    }

    private DndSource makeDraggablePaneProducer(Component component) {
        return new DndSource(
                component,
                new DndSourceEventListener() {
                    public Component getTransferedObject(Component component) {
                        return createDraggablePane();
                    }

                    public void componentDropped(Container prevParent, Component component) {
                    }
                }
        );
    }

    /** Creates new form Test */
    public Test() {
        initComponents();

        newButtonSource = makeDraggablePaneProducer(sourceNewButton);
        
        otherButtonSource = new DndSource(
                otherButton,
                new DndSourceEventListener() {
                    public Component getTransferedObject(Component component) {
                        return otherButton;
                    }

                    public void componentDropped(Container prevParent, Component component) {
                        System.out.println("about to repaint previous otherButton parent");
                        if (prevParent!=null) {
                            prevParent.validate();
                            prevParent.repaint();
                        } else {
                            System.out.println("prevParent is null");
                        }
                        otherButtonSource.setActive(false);
                    }
                }
        );

        target = new DndTarget(targetPanel, new DndTargetEventListener() {

            public boolean canAcceptType(Class type) {
                return Component.class.isAssignableFrom(type);
            }

            public void acceptObject(Object object, Point location) {
                System.out.println("accepting object...");
                Component component = (Component)object;
                targetPanel.add(component);
                component.setLocation(location);
                targetPanel.validate();
                targetPanel.repaint();
            }

        });

        dragParent = new DragContainer(targetPanel,
                new DragContainerListener() {

            public void dragStarted(Container parent, Component c) {
                System.out.println("drag started");
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
                System.out.println("drag ended");
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
            }

        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourcePanel = new javax.swing.JPanel();
        sourceNewButton = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        otherButton = new javax.swing.JButton();
        chkAllowDragging = new javax.swing.JCheckBox();
        targetPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sourcePanel.setBackground(new java.awt.Color(153, 255, 51));
        sourcePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sourceNewButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 153)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        sourceNewButton.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Create New Button");
        sourceNewButton.add(jLabel1, java.awt.BorderLayout.CENTER);

        sourcePanel.add(sourceNewButton);

        otherButton.setText("Other Button");
        sourcePanel.add(otherButton);

        chkAllowDragging.setText("Allow Dragging");
        chkAllowDragging.setOpaque(false);
        chkAllowDragging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAllowDraggingActionPerformed(evt);
            }
        });
        sourcePanel.add(chkAllowDragging);

        getContentPane().add(sourcePanel, java.awt.BorderLayout.PAGE_START);

        targetPanel.setBackground(new java.awt.Color(204, 255, 204));
        targetPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        targetPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        targetPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        targetPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                targetPanelComponentMoved(evt);
            }
        });
        targetPanel.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                targetPanelComponentAdded(evt);
            }
        });
        targetPanel.setLayout(null);
        getContentPane().add(targetPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void targetPanelComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_targetPanelComponentAdded

        dragParent.addDraggableComponent(evt.getChild());

    }//GEN-LAST:event_targetPanelComponentAdded

    private void chkAllowDraggingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAllowDraggingActionPerformed

        dragParent.setDraggingActive(chkAllowDragging.isSelected());

    }//GEN-LAST:event_chkAllowDraggingActionPerformed

    private void targetPanelComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_targetPanelComponentMoved

        DndSource dndSource = null;
        for (DndSource ds:dragSources)
            if (ds.getComponent()==evt.getComponent()) {
                dndSource = ds;
                break;
            }
        
        if (dndSource!=null)
            dragSources.remove(dndSource);

    }//GEN-LAST:event_targetPanelComponentMoved

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAllowDragging;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton otherButton;
    private javax.swing.JPanel sourceNewButton;
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JPanel targetPanel;
    // End of variables declaration//GEN-END:variables

}
