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
@javax.persistence.Table(name="EDITOR_ENTITYBOX")
public class EntityBox extends jellyfish.common.persistence.PersistenceObject {

    public static final Attribute ATTR_X = getDeclaredField(EntityBox.class, "x");
    public static final Attribute ATTR_Y = getDeclaredField(EntityBox.class, "y");
    public static final Attribute ATTR_WIDTH = getDeclaredField(EntityBox.class, "width");
    public static final Attribute ATTR_HEIGHT = getDeclaredField(EntityBox.class, "height");

    @javax.persistence.OneToOne(fetch=FetchType.EAGER, optional=false, cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private Entity entity;

    private int x;
    private int y;
    private int width;
    private int height;

    @javax.persistence.OneToMany(mappedBy = "srcBox", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<Connector> srcSideConnectors = new ArrayList<Connector>();

    @javax.persistence.OneToMany(mappedBy = "dstBox", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<Connector> dstSideConnectors = new ArrayList<Connector>();

    protected EntityBox() {

    }

    public EntityBox(Entity entity) {
        this.entity = entity;
        
        this.x = 0;
        this.y = 0;

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
        setAttribute(ATTR_HEIGHT, height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        setAttribute(ATTR_WIDTH, width);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        setAttribute(ATTR_X, x);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        setAttribute(ATTR_Y, y);
    }

    public List<Connector> getDstSideConnectors() {
        return dstSideConnectors;
    }

    public List<Connector> getSrcSideConnectors() {
        return srcSideConnectors;
    }

    
    @Override
    public String toString() {
        return "EntityBox{" + entity + ", " + x + "," + y + "," + width + "," + height + '}';
    }



}
