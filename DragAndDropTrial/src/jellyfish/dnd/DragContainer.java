/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jellyfish.common.Pair;

/**
 *
 * @author Xevia
 */
public class DragContainer {

    private JPanel parent;
    private Set<Component> draggableComponents;
    private List<Pair<Component,MouseMotionListener[]>> componentMotionListeners;
    private List<Pair<Component,MouseListener[]>> componentMouseListeners;
    private List<Pair<Component,MouseWheelListener[]>> componentWheelListeners;
    private boolean draggingActive;
    private DragContainerListener dragContainerListener;

    private Point lastMousePress = null;
    private Component lastPressOn = null;
    private Point lastPressOnLoc = null;

    public DragContainer(JPanel parent, DragContainerListener dragContainerListener) {
        this.parent = parent;
        this.draggingActive = false;
        this.dragContainerListener = dragContainerListener;
        this.componentMotionListeners = new ArrayList();
        this.componentMouseListeners = new ArrayList();
        this.componentWheelListeners = new ArrayList();
        
        this.draggableComponents = new TreeSet<Component>(new Comparator<Component>(){
            public int compare(Component o1, Component o2) {
                return o1.hashCode()-o2.hashCode();
            }
        });

        parent.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                if (draggableComponents.contains(e.getChild()))
                    draggableComponents.remove(e.getChild());
            }
        });
        
        parent.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                DragContainer.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                DragContainer.this.mouseReleased(e);
            }

        });

        parent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DragContainer.this.mouseDragged(e);
            }
        });

    }

    final public boolean isDraggingActive() {
        return draggingActive;
    }

    final public void setDraggingActive(boolean draggingActive) {
        this.draggingActive = draggingActive;
        if (this.draggingActive) {
            for (Component c:draggableComponents) {
                MouseListener[] mls = c.getMouseListeners();
                if (mls!=null)
                    componentMouseListeners.add(Pair.create(c, mls));

                MouseMotionListener[] mmls = c.getMouseMotionListeners();
                if (mmls!=null)
                    componentMotionListeners.add(Pair.create(c, mmls));

                MouseWheelListener[] mwls = c.getMouseWheelListeners();
                if (mwls!=null)
                    componentWheelListeners.add(Pair.create(c, mwls));
            }
            
            for (Pair<Component,MouseListener[]> x:componentMouseListeners) {
                for (MouseListener ml:x.getSecond())
                    x.getFirst().removeMouseListener(ml);
            }
            for (Pair<Component,MouseMotionListener[]> x:componentMotionListeners) {
                for (MouseMotionListener ml:x.getSecond())
                    x.getFirst().removeMouseMotionListener(ml);
            }
            for (Pair<Component,MouseWheelListener[]> x:componentWheelListeners) {
                for (MouseWheelListener ml:x.getSecond())
                    x.getFirst().removeMouseWheelListener(ml);
            }
        } else {
            for (Pair<Component,MouseListener[]> x:componentMouseListeners) {
                for (MouseListener ml:x.getSecond())
                    x.getFirst().addMouseListener(ml);
            }
            for (Pair<Component,MouseMotionListener[]> x:componentMotionListeners) {
                for (MouseMotionListener ml:x.getSecond())
                    x.getFirst().addMouseMotionListener(ml);
            }
            for (Pair<Component,MouseWheelListener[]> x:componentWheelListeners) {
                for (MouseWheelListener ml:x.getSecond())
                    x.getFirst().addMouseWheelListener(ml);
            }
            componentMouseListeners.clear();
            componentMotionListeners.clear();
            componentWheelListeners.clear();
        }
    }
    
    public void addDraggableComponent(Component component) {
//        System.out.println("draggable component added");
        draggableComponents.add(component);
    }

    public void removeDraggableComponent(Component component) {
        draggableComponents.remove(component);
    }

    public Set<Component> getDraggableComponents() {
        return Collections.unmodifiableSet(draggableComponents);
    }
    

    private void clearPress() {
        if (lastPressOn!=null) {
            dragContainerListener.dragEnded(parent, lastPressOn);
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
        dragContainerListener.moveComponent(parent, lastPressOn, lastPressOn.getLocation(), newLoc);
    }

    private void mousePressed(MouseEvent e) {
//        System.out.println("drag-panel-mouse-pressed!");
        if (isDraggingActive()) {
//            System.out.println("dragging is active");

            Point pt = e.getPoint();
            Component c = parent.getComponentAt(pt);

            if (c!=null && c!=parent && draggableComponents.contains(c)) {
                lastMousePress = pt;
                lastPressOn = c;
                lastPressOnLoc = c.getLocation();
                dragContainerListener.dragStarted(parent, c);
            } else {
                clearPress();
            }

        } else {
            clearPress();
        }

    }

    private void mouseReleased(MouseEvent e) {
//        System.out.println("drag-panel-mouse-released!");
        clearPress();
    }
}
