/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.editor.model;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="EDITOR_CONNECTOR_POINT")
public class ConnectorPoint extends jellyfish.common.persistence.PersistenceObject {

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


}
