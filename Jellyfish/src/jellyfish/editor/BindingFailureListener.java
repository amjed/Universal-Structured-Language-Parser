/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.Binding.SyncFailureType;

/**
 *
 * @author Xevia
 */
public class BindingFailureListener extends AbstractBindingListener {

    private JLabel lblErrorMsg;

    public BindingFailureListener(JLabel lblErrorMsg) {
        this.lblErrorMsg = lblErrorMsg;
    }

    @Override
    public void syncFailed(org.jdesktop.beansbinding.Binding binding, SyncFailure failure) {
        if (failure.getType()==SyncFailureType.VALIDATION_FAILED) {
            binding.refresh();
            Object source = binding.getTargetObject();
            if (source instanceof Component) {
                ((Component)source).requestFocus();
                if (source instanceof JTextComponent) {
                    String val = ((JTextComponent)source).getText();
//                    ((JTextComponent)source).setSelectionStart(0);
//                    ((JTextComponent)source).setSelectionEnd(val.length());
                    ((JTextComponent)source).setCaretPosition(val.length());
                }
            }
            lblErrorMsg.setText(failure.getValidationResult().getDescription());
        }
    }

    @Override
    public void synced(org.jdesktop.beansbinding.Binding binding) {
//        lblErrorMsg.setText("");
    }
    

}
