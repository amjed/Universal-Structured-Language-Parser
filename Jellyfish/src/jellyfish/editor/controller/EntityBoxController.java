/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.controller;

import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.editor.model.EntityBox;

/**
 *
 * @author Xevia
 */
public class EntityBoxController extends AbstractController<EntityBox> {

    public static String ATTR_SELECTED_ENTITY_BOX = "selectedEntityBox";

    public EntityBoxController(PersistenceContext context) {
        super(context, PersistenceUtil.getAllQuery(context.getEntityManager(), EntityBox.class));
    }

    public EntityBox getSelectedEntityBox() {
        return itemList.get(selectedItemIndex);
    }

    public void setSelectedEntityBox(EntityBox selectedEntityBox) {
        int r = itemList.indexOf(selectedEntityBox);
        if (this.selectedItemIndex!=r) {
            EntityBox p = getSelectedEntityBox();
            setSelectedItemIndex(r);
            firePropertyChange(ATTR_SELECTED_ENTITY_BOX, p, selectedEntityBox);
        }
    }

    

}
