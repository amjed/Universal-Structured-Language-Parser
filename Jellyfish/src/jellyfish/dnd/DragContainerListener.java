/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

/**
 *
 * @author Xevia
 */
public interface DragContainerListener {

    public void dragStarted(Container parent, Component c);
    public void moveComponent(Container parent, Component c, Point oldLocation, Point newLocation);
    public void dragEnded(Container parent, Component c);

}
