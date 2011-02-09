/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

import java.awt.Point;

/**
 *
 * @author Xevia
 */
public interface DndTargetEventListener {
    
    boolean canAcceptType(Class type);
    void acceptObject(Object object, Point location);

}
