/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewEntityDragPanel.java
 *
 * Created on Nov 10, 2010, 9:48:00 PM
 */

package jellyfish.editor;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jellyfish.dnd.DndSource;
import jellyfish.dnd.DndSourceEventListener;
import jellyfish.editor.model.EntityBox;
import jellyfish.triplestore.model.Entity;

/**
 *
 * @author Xevia
 */
public class NewEntityDragPanel extends DragPanel {

    private JFrame frame;
    private DndSource newEntitySource;
    
    /** Creates new form NewEntityDragPanel */
    public NewEntityDragPanel(JFrame frame) {
        this.frame = frame;
        initComponents();

        newEntitySource = new DndSource(this, new DndSourceEventListener() {

            public Object getTransferedObject(Component component) {

                String entityName = JOptionPane.showInputDialog(
                        NewEntityDragPanel.this.frame,
                        "Please enter the entity name"
                        );

                if (entityName==null || entityName.isEmpty())
                    return null;

                return createEntity(entityName);
            }

            public void componentDropped(Container prevParent, Component component) {
            }
        });
    }

    private PanelEntityBox createEntity(String name)
    {
        Entity entity = new Entity(name);
        EntityBox entityBox = new EntityBox(entity);
        PanelEntityBox panelEntityBox = new PanelEntityBox(frame,entityBox);

        return panelEntityBox;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Add New Entity");
        add(jLabel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
