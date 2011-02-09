package old;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 *
 * @author Xevia
 */
public class DragDrop implements
        DragGestureListener,
        DragSourceListener,
        DropTargetListener,
        Transferable
{

    static final DataFlavor[] supportedFlavors = {null};

    static {
        try {
            supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    Object object;
    // Transferable methods.

    public Object getTransferData(DataFlavor flavor) {
        if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) {
            return object;
        } else {
            return null;
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
    }
    // DragGestureListener method.

    public void dragGestureRecognized(DragGestureEvent ev) {
        ev.startDrag(null, this, this);
        
    }
    // DragSourceListener methods.

    public void dragDropEnd(DragSourceDropEvent ev) {
    }

    public void dragEnter(DragSourceDragEvent ev) {
    }

    public void dragExit(DragSourceEvent ev) {
    }

    public void dragOver(DragSourceDragEvent ev) {
        object = ev.getSource();
    }

    public void dropActionChanged(DragSourceDragEvent ev) {
    }
    // DropTargetListener methods.

    public void dragEnter(DropTargetDragEvent ev) {
    }

    public void dragExit(DropTargetEvent ev) {
    }

    public void dragOver(DropTargetDragEvent ev) {
        dropTargetDrag(ev);
    }

    public void dropActionChanged(DropTargetDragEvent ev) {
        dropTargetDrag(ev);
    }

    void dropTargetDrag(DropTargetDragEvent ev) {
        ev.acceptDrag(ev.getDropAction());
    }

    public void drop(DropTargetDropEvent ev) {
        ev.acceptDrop(ev.getDropAction());
        try {
            Object target = ev.getSource();
            Object source = ev.getTransferable().getTransferData(supportedFlavors[0]);
            Component component = ((DragSourceContext) source).getComponent();
            Container oldContainer = component.getParent();
            Container container = (Container) ((DropTarget) target).getComponent();
            container.add(component);
            oldContainer.validate();
            oldContainer.repaint();
            container.validate();
            container.repaint();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        ev.dropComplete(true);
    }

    public static void main(String[] arg) {
        Button button = new Button("Drag this button");
        Label label = new Label("Drag this label");
        Checkbox checkbox = new Checkbox("Drag this check box");
        CheckboxGroup radiobutton = new CheckboxGroup();
        Checkbox checkbox1 = new Checkbox("Drag this check box",
                radiobutton, false);
        Choice country = new Choice();

        // adding possible choices
        country.add("India");
        country.add("US");
        country.add("Australia");

        Frame source = new Frame("Source Frame");
        source.setLayout(new FlowLayout());
        source.add(button);
        source.add(label);
        source.add(checkbox);
        source.add(checkbox1);
        source.add(country);

        Frame target = new Frame("Target Frame");
        target.setLayout(new FlowLayout());

        DragDrop dndListener = new DragDrop();

        DragSource dragSource = new DragSource();
//        DropTarget dropTarget1 = new DropTarget(source, DnDConstants.ACTION_MOVE, dndListener);
        DropTarget dropTarget2 = new DropTarget(target, DnDConstants.ACTION_NONE, dndListener);
        
        DragGestureRecognizer dragRecognizer1 = dragSource.createDefaultDragGestureRecognizer(button,
                DnDConstants.ACTION_MOVE, dndListener);
        DragGestureRecognizer dragRecognizer2 = dragSource.createDefaultDragGestureRecognizer(label,
                DnDConstants.ACTION_MOVE, dndListener);
        DragGestureRecognizer dragRecognizer3 = dragSource.createDefaultDragGestureRecognizer(checkbox,
                DnDConstants.ACTION_MOVE, dndListener);
        DragGestureRecognizer dragRecognizer4 = dragSource.createDefaultDragGestureRecognizer(checkbox1,
                DnDConstants.ACTION_MOVE, dndListener);
        DragGestureRecognizer dragRecognizer5 = dragSource.createDefaultDragGestureRecognizer(country,
                DnDConstants.ACTION_MOVE, dndListener);

        source.setBounds(0, 200, 200, 200);
        target.setBounds(220, 200, 200, 200);
        source.show();
        target.show();
    }
}
