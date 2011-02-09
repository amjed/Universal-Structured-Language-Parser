/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 *
 * @author Xevia
 */
public class DndTarget implements DropTargetListener {

    private Component component;
    private DndTargetEventListener targetEventListener;
    private DropTarget dropTarget;

    public DndTarget(
            Component component,
            DndTargetEventListener checkAcceptListener)
    {
        this.component = component;
        this.targetEventListener = checkAcceptListener;
        this.dropTarget = new DropTarget(component, this);
    }

    public synchronized void setActive(boolean isActive) {
        dropTarget.setActive(isActive);
    }

    public boolean isActive() {
        return dropTarget.isActive();
    }
    
    //  From: DropTargetListener
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
        dropTargetDrag(dtde);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        dropTargetDrag(dtde);
    }

    private void dropTargetDrag(DropTargetDragEvent ev) {
        if (ev.isDataFlavorSupported(DndCommon.getJvmLocalDataFlavor())) {
            Transferable transferable = ev.getTransferable();
            try {
                Object object = transferable.getTransferData(DndCommon.getJvmLocalDataFlavor());
                if (object instanceof DndHolder) {
                    DndHolder holder = (DndHolder)object;
                    if (targetEventListener.canAcceptType(holder.getClass())) {
                        ev.acceptDrag(ev.getDropAction());
                    }
                } else {
                    if (targetEventListener.canAcceptType(object.getClass())) {
                        ev.acceptDrag(ev.getDropAction());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
    
    public void drop(DropTargetDropEvent dtde) {
//        System.out.println("drag-target.drop");
        if (dtde.isDataFlavorSupported(DndCommon.getJvmLocalDataFlavor())) {
            Transferable transferable = dtde.getTransferable();
            try {
//                System.out.println("transferable="+transferable.getClass().getCanonicalName());
                Object object = transferable.getTransferData(DndCommon.getJvmLocalDataFlavor());
                if (object==null) {
                    dtde.rejectDrop();
                    return;
                }

//                System.out.println("\tobject type="+object.getClass());
                if (DndHolder.class.isAssignableFrom(object.getClass())) {
//                    System.out.println("\t\tis a DndHolder");
                    DndHolder holder = (DndHolder)object;
                    Object heldObject = holder.getObject();

                    if (heldObject==null) {
                        dtde.rejectDrop();
                        return;
                    }

                    if (targetEventListener.canAcceptType(heldObject.getClass())) {
                        targetEventListener.acceptObject(heldObject,dtde.getLocation());
                    }
                } else {
                    if (targetEventListener.canAcceptType(object.getClass())) {
                        targetEventListener.acceptObject(object,dtde.getLocation());
                    }
                }
                
                dtde.acceptDrop(dtde.getDropAction());
                dtde.dropComplete(true);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        } else {
            dtde.rejectDrop();
        }
    }

    
}
