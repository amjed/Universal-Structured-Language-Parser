/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelEntityBox.java
 *
 * Created on Oct 31, 2010, 3:50:55 AM
 */

package jellyfish.triplestore.editor;

import jellyfish.triplestore.editor.model.EntityBox;

/**
 *
 * @author Xevia
 */
public class PanelEntityBox extends javax.swing.JPanel {

    private EntityBox entityBox;

    /** Creates new form PanelEntityBox */
    public PanelEntityBox(EntityBox entityBox) {
        this.entityBox = entityBox;
        initComponents();
        this.setSize(this.getPreferredSize());
    }

    public EntityBox getEntityBox() {
        return entityBox;
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

        jPanel1 = new javax.swing.JPanel();
        lblEntityName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSource = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(204, 204, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 20));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 20));

        lblEntityName.setFont(new java.awt.Font("Dialog", 0, 12));
        lblEntityName.setName("lblEntityName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${entityBox.entity.name}"), lblEntityName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel1.add(lblEntityName);

        add(jPanel1);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 100));

        txtSource.setColumns(20);
        txtSource.setRows(5);
        txtSource.setName("txtSource"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${entityBox.entity.source}"), txtSource, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtSource);

        add(jScrollPane1);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEntityName;
    private javax.swing.JTextArea txtSource;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}