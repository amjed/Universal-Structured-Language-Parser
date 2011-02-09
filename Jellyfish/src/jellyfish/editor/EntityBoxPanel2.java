/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EntityBoxPanel2.java
 *
 * Created on Nov 10, 2010, 4:16:45 AM
 */

package jellyfish.editor;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.persistence.EntityManager;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.editor.controller.ConnectorController;
import jellyfish.editor.controller.EntityBoxController;
import jellyfish.editor.controller.PersistenceContext;
import jellyfish.editor.model.EntityBox;

/**
 *
 * @author Xevia
 */
public class EntityBoxPanel2 extends javax.swing.JPanel {

    private JFrame frame;
    private EntityBoxController controller;
    private ConnectorController connectorController;

    /** Creates new form EntityBoxPanel2 */
    public EntityBoxPanel2(JFrame frame, EntityBoxController controller, ConnectorController connectorController) {
        this.frame = frame;
        this.controller = controller;
        this.connectorController = connectorController;
        initComponents();


        this.listEntityBoxs.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                EntityBox box = (EntityBox)value;
                return super.getListCellRendererComponent(list, box.getEntity().getName(), index, isSelected, cellHasFocus);
            }

        });

        bindingGroup.addBindingListener(new BindingFailureListener(txtErrorMsg));


    }


    public EntityBoxController getController() {
        return controller;
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

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        listEntityBoxs = new javax.swing.JList();
        jellyfish.editor.HorizontalLine horizontalLine3 = new jellyfish.editor.HorizontalLine();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        jellyfish.editor.HorizontalLine horizontalLine4 = new jellyfish.editor.HorizontalLine();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        btnEntityDel = new javax.swing.JButton();
        jellyfish.editor.HorizontalLine horizontalLine5 = new jellyfish.editor.HorizontalLine();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        txtErrorMsg = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setOpaque(false);
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        listEntityBoxs.setFont(new java.awt.Font("Lucida Sans", 0, 12));
        listEntityBoxs.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listEntityBoxs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${controller.itemList}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, eLProperty, listEntityBoxs);
        jListBinding.setSourceNullValue(null);
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${controller.selectedItemIndex}"), listEntityBoxs, org.jdesktop.beansbinding.BeanProperty.create("selectedIndex"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        listEntityBoxs.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listEntityBoxsValueChanged(evt);
            }
        });
        scrollPane.setViewportView(listEntityBoxs);

        jPanel1.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(jPanel1);

        horizontalLine3.setForeground(new java.awt.Color(153, 153, 153));
        horizontalLine3.setPreferredSize(new java.awt.Dimension(10, 1));

        javax.swing.GroupLayout horizontalLine3Layout = new javax.swing.GroupLayout(horizontalLine3);
        horizontalLine3.setLayout(horizontalLine3Layout);
        horizontalLine3Layout.setHorizontalGroup(
            horizontalLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        horizontalLine3Layout.setVerticalGroup(
            horizontalLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        add(horizontalLine3);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setMaximumSize(new java.awt.Dimension(2000, 25));
        jPanel3.setMinimumSize(new java.awt.Dimension(200, 25));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 12));
        jLabel2.setText("Name:");
        jLabel2.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel2.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 25));
        jPanel3.add(jLabel2, java.awt.BorderLayout.WEST);

        txtName.setFont(new java.awt.Font("Lucida Sans", 0, 12));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${controller.selectedEntityBox.entity.name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setValidator(controller.getContext().new BaseEntityNameValidator());
        bindingGroup.addBinding(binding);

        jPanel3.add(txtName, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3);

        jPanel4.setMaximumSize(new java.awt.Dimension(2000, 25));
        jPanel4.setMinimumSize(new java.awt.Dimension(200, 25));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 12));
        jLabel3.setText("Source:");
        jLabel3.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel3.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel3.setPreferredSize(new java.awt.Dimension(80, 25));
        jPanel4.add(jLabel3, java.awt.BorderLayout.WEST);

        txtSource.setFont(new java.awt.Font("Lucida Sans", 0, 12));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${controller.selectedEntityBox.entity.source}"), txtSource, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        jPanel4.add(txtSource, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel4);

        add(jPanel2);

        horizontalLine4.setForeground(new java.awt.Color(153, 153, 153));
        horizontalLine4.setPreferredSize(new java.awt.Dimension(10, 1));

        javax.swing.GroupLayout horizontalLine4Layout = new javax.swing.GroupLayout(horizontalLine4);
        horizontalLine4.setLayout(horizontalLine4Layout);
        horizontalLine4Layout.setHorizontalGroup(
            horizontalLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        horizontalLine4Layout.setVerticalGroup(
            horizontalLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        add(horizontalLine4);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        jPanel6.setOpaque(false);

        btnEntityDel.setFont(new java.awt.Font("Lucida Sans", 0, 10));
        btnEntityDel.setText("Del");
        btnEntityDel.setMaximumSize(new java.awt.Dimension(60, 25));
        btnEntityDel.setMinimumSize(new java.awt.Dimension(60, 25));
        btnEntityDel.setOpaque(false);
        btnEntityDel.setPreferredSize(new java.awt.Dimension(60, 25));
        btnEntityDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntityDelActionPerformed(evt);
            }
        });
        jPanel6.add(btnEntityDel);

        add(jPanel6);

        horizontalLine5.setForeground(new java.awt.Color(153, 153, 153));
        horizontalLine5.setPreferredSize(new java.awt.Dimension(10, 1));

        javax.swing.GroupLayout horizontalLine5Layout = new javax.swing.GroupLayout(horizontalLine5);
        horizontalLine5.setLayout(horizontalLine5Layout);
        horizontalLine5Layout.setHorizontalGroup(
            horizontalLine5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        horizontalLine5Layout.setVerticalGroup(
            horizontalLine5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        add(horizontalLine5);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        jPanel5.setMaximumSize(new java.awt.Dimension(2000, 30));
        jPanel5.setMinimumSize(new java.awt.Dimension(100, 30));
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel5.setLayout(new java.awt.BorderLayout());

        txtErrorMsg.setFont(new java.awt.Font("Lucida Sans", 1, 10));
        txtErrorMsg.setForeground(new java.awt.Color(153, 0, 0));
        txtErrorMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel5.add(txtErrorMsg, java.awt.BorderLayout.CENTER);

        add(jPanel5);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void listEntityBoxsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listEntityBoxsValueChanged

        if (!evt.getValueIsAdjusting())
            controller.setSelectedEntityBox((EntityBox)listEntityBoxs.getSelectedValue());

    }//GEN-LAST:event_listEntityBoxsValueChanged

    private void btnEntityDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntityDelActionPerformed

        EntityBox box = (EntityBox) listEntityBoxs.getSelectedValue();
        if (box == null) {
            JOptionPane.showMessageDialog(this, "Please select a predicate first");
            return;
        }
        
        controller.getItemList().remove(box);
        connectorController.update();

    }//GEN-LAST:event_btnEntityDelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntityDel;
    private javax.swing.JList listEntityBoxs;
    private javax.swing.JLabel txtErrorMsg;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSource;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables


    public static void main(String[] args) {
        try {
//            final EntityManager em = PersistenceUtil.getEntityManager(true, true);
            final EntityManager em = PersistenceUtil.getEntityManager(false, false);

            final PersistenceContext context = new PersistenceContext(em);
            final EntityBoxController controller = new EntityBoxController(context);
            final ConnectorController connectorController = new ConnectorController(context);

            if (em==null) {
                System.out.println("ERROR: Unintialized EntityManager");
                return;
            }

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    JFrame mainFrame = new JFrame();
                    EntityBoxPanel2 panel = new EntityBoxPanel2(mainFrame, controller, connectorController);
                    mainFrame.setContentPane(panel);
                    mainFrame.pack();
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    mainFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            context.save();
                        }
                    });

                    mainFrame.setVisible(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace( System.out );
        }
    }
}
