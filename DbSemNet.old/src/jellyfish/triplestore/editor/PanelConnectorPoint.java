/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelConnectorPointBox.java
 *
 * Created on Oct 31, 2010, 7:07:09 PM
 */

package jellyfish.triplestore.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import jellyfish.dnd.DndSource;
import jellyfish.dnd.DndSourceEventListener;
import jellyfish.dnd.DndTarget;
import jellyfish.dnd.DndTargetEventListener;
import jellyfish.triplestore.editor.model.ConnectorPoint;

/**
 *
 * @author Xevia
 */
public class PanelConnectorPoint extends javax.swing.JPanel {

    private ConnectorPoint connectorPoint;
    private DndSource dndSource;

    /** Creates new form PanelConnectorPointBox */
    public PanelConnectorPoint() {
        initComponents();

        this.dndSource = new DndSource(this, new DndSourceEventListener() {
            public Component getTransferedObject(Component component) {
                return PanelConnectorPoint.this;
            }

            public void componentDropped(Container prevParent, Component component) {
                
            }
        });
    }

    public ConnectorPoint getConnectorPoint() {
        return connectorPoint;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        setBackground(new java.awt.Color(204, 204, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setMaximumSize(new java.awt.Dimension(10, 10));
        setMinimumSize(new java.awt.Dimension(10, 10));
        setPreferredSize(new java.awt.Dimension(10, 10));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${connectorPoint.connector.triple.predicate.name}"), this, org.jdesktop.beansbinding.BeanProperty.create("name"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
