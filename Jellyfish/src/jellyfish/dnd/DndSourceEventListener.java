/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Xevia
 */
public interface DndSourceEventListener {

    Object getTransferedObject(Component component);
    
    void componentDropped(Container prevParent, Component component);

}
