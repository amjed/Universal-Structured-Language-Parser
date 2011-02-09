/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import jellyfish.editor.controller.PersistenceContext;
import java.util.List;
import jellyfish.common.ObservableBean;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.triplestore.model.Relationship;
import org.jdesktop.observablecollections.ObservableList;

/**
 *
 * @author Xevia
 */
public class RelationshipController extends AbstractController<Relationship> {

    public static String ATTR_HIGHLIGHT_RELATIONSHIP = "highlightRelationship";
    public static String ATTR_SELECTED_RELATIONSHIP = "selectedRelationship";

    private boolean highlightRelationship;


    public RelationshipController(PersistenceContext context) {
        super(context, PersistenceUtil.getAllQuery(context.getEntityManager(), Relationship.class));
    }

    public boolean isHighlightRelationship() {
        return highlightRelationship;
    }

    public void setHighlightRelationship(boolean highlightRelationship) {
        firePropertyChange(
                ATTR_HIGHLIGHT_RELATIONSHIP,
                this.highlightRelationship,
                this.highlightRelationship = highlightRelationship);
    }

    public Relationship getSelectedRelationship() {
        return itemList.get(selectedItemIndex);
    }

    public void setSelectedRelationship(Relationship relationship) {
        int r = itemList.indexOf(relationship);
        if (this.selectedItemIndex!=r) {
            Relationship p = getSelectedRelationship();
            setSelectedItemIndex(r);
            firePropertyChange(ATTR_SELECTED_RELATIONSHIP, p, relationship);
        }
    }

    
}
