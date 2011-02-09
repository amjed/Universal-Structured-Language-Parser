/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.dnd;

/**
 *
 * @author Xevia
 */
abstract class DndHolder<Type> {

    private Class<? extends Type> type;

    public DndHolder(Class<? extends Type> type) {
        this.type = type;
    }

    public Class<? extends Type> getType() {
        return type;
    }
    
    public abstract Type getObject();

}
