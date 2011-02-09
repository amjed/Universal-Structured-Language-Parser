/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_ENTITY")
public class Entity extends NamedEntity {

    public static final Attribute ATTR_SOURCE = getDeclaredField(Entity.class, "source");

    @javax.persistence.Column(nullable=true)
    private String source;

    public Entity() {
    }

    public Entity( String name ) {
        super( name );
    }
    
    public Entity( Entity e ) {
        super( e );
        this.source  = e.source;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        setAttribute(ATTR_SOURCE, source);
    }

    @Override
    public boolean isEntity() {
	return true;
    }
    
    @Override
    public String toString() {
        return "Entity("+getName()+")";
    }

    
}
