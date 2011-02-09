/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.editor.model;

import java.util.ArrayList;
import java.util.List;
import jellyfish.triplestore.model.*;


/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="EDITOR_CONNECTOR")
public class Connector extends jellyfish.common.persistence.PersistenceObject {

    @javax.persistence.ManyToOne(optional=false)
    private EntityBox srcBox;
    
    @javax.persistence.ManyToOne(optional=false)
    private EntityBox dstBox;

    @javax.persistence.OneToOne(optional=false)
    private Triple triple;

    @javax.persistence.OneToMany(mappedBy = "connector")
    private List<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>(2);

    protected Connector() {
    }

    public Connector(EntityBox srcBox, EntityBox dstBox, Triple triple) {
        this.srcBox = srcBox;
        this.dstBox = dstBox;
        this.triple = triple;

        // TODO: Let the system figure the best connector points for this connector
    }

    public EntityBox getDstBox() {
        return dstBox;
    }

    public Triple getTriple() {
        return triple;
    }
    
    public EntityBox getSrcBox() {
        return srcBox;
    }

    public List<ConnectorPoint> getConnectorPoints() {
        return connectorPoints;
    }

}
