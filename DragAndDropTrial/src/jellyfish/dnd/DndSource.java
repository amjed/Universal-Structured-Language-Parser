/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;

/**
 *
 * @author Xevia
 */
public class DndSource implements DragGestureListener, DragSourceListener {

    private static DataFlavor supportedFlavors[] = new DataFlavor[] {
        DndCommon.getJvmLocalDataFlavor()
    };
    
    private Component component;
    private DndSourceEventListener sourceListener;
    private DragGestureRecognizer gestureRecognizer;
    private DndHolderGetter holderGetter;

    private class DndHolderGetter extends DndHolder<Component> {

        public DndHolderGetter() {
            super(DndSource.this.component.getClass());
        }

        @Override
        public Component getObject() {
//            System.out.println("DndHolderGetter.getObject");
            return sourceListener.getTransferedComponent(component);
        }
    }

    public DndSource(Component component, DndSourceEventListener sourceListener) {
        this.component = component;
        this.sourceListener = sourceListener;
        this.holderGetter = new DndHolderGetter();
        initRecognizer();
    }

    public Component getComponent() {
        return component;
    }
    
    public boolean isActive() {
        return gestureRecognizer.getSourceActions()!=DnDConstants.ACTION_NONE;
    }

    public void setActive(boolean active) {
        if (active) {
            try {
                gestureRecognizer.setSourceActions(DnDConstants.ACTION_COPY_OR_MOVE);
                gestureRecognizer.addDragGestureListener(this);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        } else {
            try {
                gestureRecognizer.setSourceActions(DnDConstants.ACTION_NONE);
                gestureRecognizer.removeDragGestureListener(this);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
    
    private void initRecognizer() {
        this.gestureRecognizer =
                DndCommon.getDragSource().createDefaultDragGestureRecognizer(
                    component,
                    DnDConstants.ACTION_COPY_OR_MOVE,
                    this
                    );
    }

    //  From: DragGestureListener
    public void dragGestureRecognized(DragGestureEvent dge) {
        dge.startDrag(
                DragSource.DefaultMoveDrop,
                new TransferableComponent(),
                this
                );
    }

    //  From: DragSourceListener
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
//        System.out.println("drag-source.dragDropEnd");
        if (dsde.getDropSuccess()) {
            TransferableComponent tc = (TransferableComponent)dsde.getDragSourceContext().getTransferable();
            sourceListener.componentDropped(tc.prevComponentParent, component);
        }
    }

    private class TransferableComponent implements Transferable {

        private Container prevComponentParent;

        public TransferableComponent() {
            this.prevComponentParent = component.getParent();
        }

        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//            System.out.println("getTransferData");
            if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) {
                return holderGetter;
            } else
                throw new UnsupportedFlavorException(flavor);
        }

    }
    
}
