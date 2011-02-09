/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EntityEditor.java
 *
 * Created on Nov 7, 2010, 11:22:33 PM
 */

package jellyfish.editor;

import javax.persistence.EntityManager;
import jellyfish.triplestore.model.BaseEntity;
import jellyfish.triplestore.model.Entity;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.Binding.SyncFailureType;
import org.jdesktop.beansbinding.Validator.Result;


/**
 *
 * @author Xevia
 */
public class EntityEditor extends javax.swing.JPanel {

    private EntityManager entityManager;
    private Entity entity;
    private String entityName;
    private String source;
    private BaseEntity.BaseEntityService baseEntityService;

    /** Creates new form EntityEditor */
    public EntityEditor(EntityManager entityManager, Entity entity) {
        this.entityManager = entityManager;
        this.entity = entity;
        if (this.entity==null)
            this.entity = new Entity("");

        this.entityName = entity.getName();
        this.source = entity.getSource();

        if (entityManager!=null)
            baseEntityService = new BaseEntity.BaseEntityService(entityManager);

        initComponents();

        bindingGroup.addBindingListener(new AbstractBindingListener() {
            @Override
            public void syncFailed(Binding binding, SyncFailure failure) {
                if (failure.getType()==SyncFailureType.VALIDATION_FAILED) {
                    txtErrorMsg.setText(failure.getValidationResult().getDescription());
                }
            }

            @Override
            public void synced(Binding binding) {
                txtErrorMsg.setText("");
            }
        });
    }

    public Entity getEntity() {
        return entity;
    }
    
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.firePropertyChange(
                "entityName",
                this.entityName,
                this.entityName = entityName);
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.firePropertyChange(
                "source",
                this.source,
                this.source = source);
    }

    protected void closeDialog() {
        //  TO BE OVERRIDEN BY CONTAINING DIALOG
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        txtSource = new javax.swing.JTextArea();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtErrorMsg = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 0, 12));
        jLabel1.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        txtName.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        txtName.setMinimumSize(new java.awt.Dimension(4, 25));
        txtName.setPreferredSize(new java.awt.Dimension(4, 25));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${entityName}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setValidator(new NameValidator());
        bindingGroup.addBinding(binding);

        txtName.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                txtNameAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(txtName, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 0, 12));
        jLabel2.setText("Source:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabel2, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(223, 78));

        txtSource.setColumns(20);
        txtSource.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        txtSource.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${source}"), txtSource, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtSource);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jScrollPane1, gridBagConstraints);

        btnApply.setFont(new java.awt.Font("Lucida Sans", 0, 12));
        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });
        jPanel1.add(btnApply);

        btnCancel.setFont(new java.awt.Font("Lucida Sans", 0, 12));
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel1, gridBagConstraints);

        txtErrorMsg.setFont(new java.awt.Font("Lucida Sans", 0, 10));
        txtErrorMsg.setForeground(new java.awt.Color(255, 102, 102));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(txtErrorMsg, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed

        this.entity.setName(entityName);
        this.entity.setSource(source);
        closeDialog();

    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed

        closeDialog();
        
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtNameAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_txtNameAncestorAdded

        txtName.requestFocus();
        txtName.setSelectionStart(0);
        txtName.setSelectionEnd(txtName.getText().length());

    }//GEN-LAST:event_txtNameAncestorAdded


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel txtErrorMsg;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextArea txtSource;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables


    private class NameValidator extends org.jdesktop.beansbinding.Validator<String> {
        @Override
        public Result validate(String value) {

            if (baseEntityService==null)
                return null;

            long count = baseEntityService.countByName(value);
            
            if (count>0) {
                return new Result(null, "An item with the name '"+value+"' already exists.");
            }
            
            return null;
        }
        
    }

}
