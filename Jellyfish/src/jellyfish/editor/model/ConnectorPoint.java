/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.model;

import java.lang.reflect.Field;
import java.util.List;
import jellyfish.common.persistence.Attribute;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="EDITOR_CONNECTOR_POINT")
public class ConnectorPoint extends jellyfish.common.persistence.PersistenceObject {

    public static final Attribute ATTR_CONNECTOR = getDeclaredField(ConnectorPoint.class, "connector");
//    public static final Attribute ATTR_INDEX = getDeclaredField(ConnectorPoint.class, "index");
    public static final Attribute ATTR_X = getDeclaredField(ConnectorPoint.class, "x");
    public static final Attribute ATTR_Y = getDeclaredField(ConnectorPoint.class, "y");

    @javax.persistence.ManyToOne
    private Connector connector;
    
    private int x;
    private int y;

    protected ConnectorPoint() {
    }

    public ConnectorPoint(Connector connector, int x, int y) {
        this.connector = connector;
        this.x = x;
        this.y = y;
    }

    public Connector getConnector() {
        return connector;
    }

    public int getIndex() {
        return this.connector.getConnectorPoints().indexOf(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void setX(int x) {
        setAttribute(ATTR_X, x);
    }

    public void setY(int y) {
        setAttribute(ATTR_Y, y);
    }

    @Override
    public String toString() {
        return "ConnectorPoint(" + "id="+getId()+", index=" + getIndex() + ')';
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConnectorPoint other = (ConnectorPoint) obj;
        if (this.connector != other.connector && (this.connector == null || !this.connector.equals(other.connector))) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.connector != null ? this.connector.hashCode() : 0);
        hash = 43 * hash + this.x;
        hash = 43 * hash + this.y;
        return hash;
    }


}
