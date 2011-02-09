/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.controller;

import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.editor.model.Connector;

/**
 *
 * @author Xevia
 */
public class ConnectorController extends AbstractController<Connector> {

    public static String ATTR_SELECTED_CONNECTOR = "selectedConnector";
    
    public ConnectorController(PersistenceContext context) {
        super(context, PersistenceUtil.getAllQuery(context.getEntityManager(), Connector.class));
    }
    
    public Connector getSelectedConnector() {
        return itemList.get(selectedItemIndex);
    }

    public void setSelectedConnector(Connector selectedConnector) {
        int r = itemList.indexOf(selectedConnector);
        if (this.selectedItemIndex!=r) {
            Connector p = getSelectedConnector();
            setSelectedItemIndex(r);
            firePropertyChange(ATTR_SELECTED_CONNECTOR, p, selectedConnector);
        }
    }
    
}
