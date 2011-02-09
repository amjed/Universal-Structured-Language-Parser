/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jellyfish.common.Pair;

/**
 *
 * @author Xevia
 */
public class DragContainer extends JPanel {

    private List<Pair<Component,MouseMotionListener[]>> componentMotionListeners;
    private List<Pair<Component,MouseListener[]>> componentMouseListeners;
    private List<Pair<Component,MouseWheelListener[]>> componentWheelListeners;
    private boolean draggingActive;
    private DragContainerListener dragContainerListener;

    private Point lastMousePress = null;
    private Component lastPressOn = null;
    private Point lastPressOnLoc = null;

    public DragContainer() {
        this.componentMotionListeners = new ArrayList();
        this.componentMouseListeners = new ArrayList();
        this.componentWheelListeners = new ArrayList();
        
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                DragContainer.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                DragContainer.this.mouseReleased(e);
            }

        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DragContainer.this.mouseDragged(e);
            }
        });

        this.addContainerListener(new ContainerAdapter() {

            @Override
            public void componentAdded(ContainerEvent e) {
                if (isDraggable(e.getChild())) {
                    setComponentDraggable(e.getChild());
                }
            }

        });

        setDraggingActive(true);

    }

    public DragContainerListener getDragContainerListener() {
        return dragContainerListener;
    }

    public void setDragContainerListener(DragContainerListener dragContainerListener) {
        this.dragContainerListener = dragContainerListener;
    }

    private void passToParent(MouseEvent event)
    {
        Point pt = new Point(event.getLocationOnScreen());
        SwingUtilities.convertPointFromScreen(pt, this);
        MouseEvent transformed = new MouseEvent(
                event.getComponent(),
                event.getID(),
                event.getWhen(),
                event.getModifiers(),
                pt.x, pt.y,
                event.getXOnScreen(), event.getYOnScreen(),
                event.getClickCount(),
                event.isPopupTrigger(),
                event.getButton()
                );

        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_RELEASED:
                this.processMouseEvent(transformed);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                this.processMouseMotionEvent(transformed);
                break;
        }
        
    }

    private void setComponentDraggable(Component component) {
        component.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
               passToParent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                passToParent(e);
            }

        });

        component.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                passToParent(e);
            }

        });
        
    }

    public boolean isDraggable(Component component) {
        return true;
    }

    public boolean canDragFrom(Component component, Point start)
    {
        return true;
    }

    final public boolean isDraggingActive() {
        return draggingActive;
    }
    

    final public void setDraggingActive(boolean draggingActive) {
        this.draggingActive = draggingActive;
    }
    
    public void clearDrag() {
        if (lastPressOn!=null && dragContainerListener!=null) {
            dragContainerListener.dragEnded(this, lastPressOn);
        }
        lastMousePress = null;
        lastPressOn = null;
        lastPressOnLoc = null;
    }

    private void mouseDragged(MouseEvent e) {
        if (lastMousePress==null || lastPressOn==null || lastPressOnLoc==null) {
            return;
        }

        Point currentPt = e.getPoint();
        Point newLoc = new Point(
                lastPressOnLoc.x + (currentPt.x - lastMousePress.x),
                lastPressOnLoc.y + (currentPt.y - lastMousePress.y)
                );
        if (dragContainerListener!=null)
            dragContainerListener.moveComponent(this, lastPressOn, lastPressOn.getLocation(), newLoc);
    }

    private void mousePressed(MouseEvent e) {
        if (isDraggingActive()) {
            Point pt = e.getPoint();
            Component c = this.getComponentAt(pt);
            if (c!=null && c!=this && isDraggable(c)) {
                Point c_pt = SwingUtilities.convertPoint(this, pt, c);
                if (canDragFrom(c, c_pt)) {
                    dragComponent(pt, c);
                }
            } else {
                clearDrag();
            }
        } else {
            clearDrag();
        }

    }

    private void mouseReleased(MouseEvent e) {
        clearDrag();
    }

    public void dragComponent(Point lastMousePress, Component c)
    {
        this.lastMousePress = lastMousePress;
        this.lastPressOn = c;
        this.lastPressOnLoc = c.getLocation();
        if (dragContainerListener!=null)
            dragContainerListener.dragStarted(this, c);
    }
}
