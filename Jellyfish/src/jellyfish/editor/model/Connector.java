/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import jellyfish.common.persistence.Attribute;
import jellyfish.triplestore.model.*;


/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="EDITOR_CONNECTOR")
@org.eclipse.persistence.annotations.Customizer(value=ConnectorCustomizer.class)
public class Connector extends jellyfish.common.persistence.PersistenceObject {

    public static final Attribute ATTR_SRC_BOX = getDeclaredField(Connector.class, "srcBox");
    public static final Attribute ATTR_DST_BOX = getDeclaredField(Connector.class, "dstBox");
    public static final Attribute ATTR_TRIPLE = getDeclaredField(Connector.class, "triple");
    public static final Attribute ATTR_CONNECTOR_POINTS = getDeclaredField(Connector.class, "connectorPoints");

    @javax.persistence.ManyToOne(optional=false)
    private EntityBox srcBox;
    
    @javax.persistence.ManyToOne(optional=false)
    private EntityBox dstBox;

    @javax.persistence.OneToOne(optional=false, cascade=CascadeType.ALL)
    private Triple triple;

    @javax.persistence.OneToMany(mappedBy = "connector", fetch=FetchType.EAGER)
    private List<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>();

    protected Connector() {
    }

    public Connector(EntityBox srcBox, EntityBox dstBox, Triple triple) {
        this.srcBox = srcBox;
        this.dstBox = dstBox;
        this.triple = triple;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connector other = (Connector) obj;
        if (this.triple != other.triple && (this.triple == null || !this.triple.equals(other.triple))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.triple != null ? this.triple.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return triple.toString();
    }

//    private static class ConnectorPointOrderComparator implements Comparator<ConnectorPoint> {
//
//        public int compare(ConnectorPoint o1, ConnectorPoint o2) {
//            return o1.getIndex()-o2.getIndex();
//        }
//
//    }

}
