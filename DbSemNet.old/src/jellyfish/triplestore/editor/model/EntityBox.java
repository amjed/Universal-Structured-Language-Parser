/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.editor.model;

import javax.persistence.FetchType;
import jellyfish.triplestore.model.*;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="EDITOR_ENTITYBOX")
public class EntityBox extends jellyfish.common.persistence.PersistenceObject {

    public static final String ATTR_X_LOCATION = "xLocation";
    public static final String ATTR_Y_LOCATION = "yLocation";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_HEIGHT = "height";

    @javax.persistence.OneToOne(fetch=FetchType.EAGER, optional=false)
    private Entity entity;

    private int xLocation;
    private int yLocation;
    private int width;
    private int height;

    protected EntityBox() {

    }

    public EntityBox(Entity entity) {
        this.entity = entity;

        // TODO: Have system find the best location to place box
        this.xLocation = 0;
        this.yLocation = 0;

        this.width = 100;
        this.height = 100;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        long oldValue = height;
        this.height = height;
        changeSupport.firePropertyChange(ATTR_HEIGHT, oldValue, height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        long oldValue = width;
        this.width = width;
        changeSupport.firePropertyChange(ATTR_WIDTH, oldValue, width);
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        long oldValue = xLocation;
        this.xLocation = xLocation;
        changeSupport.firePropertyChange(ATTR_X_LOCATION, oldValue, xLocation);
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        long oldValue = yLocation;
        this.yLocation = yLocation;
        changeSupport.firePropertyChange(ATTR_Y_LOCATION, oldValue, yLocation);
    }



}
