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
@javax.persistence.Table(name="TRIPLESTORE_NAMEDENTITY")
public class NamedEntity extends BaseEntity {

    public static final Attribute ATTR_NAME = getDeclaredField(NamedEntity.class, "name");

    @javax.persistence.Column(nullable=false,length=100,unique=true)
    private String name;

    public NamedEntity() {
    }

    public NamedEntity( String name ) {
        this.name = fixName( name );
    }

    public NamedEntity( NamedEntity entity ) {
        this.name = entity.name;
    }

    private String fixName(String name) {
        if (!name.isEmpty()) {
	    name = name.replaceAll( "\\W", "_" );
	    if (Character.isUpperCase( name.charAt(0))) {
		name = Character.toLowerCase( name.charAt(0) ) + name.substring( 1 );
	    }
        }
	return name;
    }

    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        setAttribute(ATTR_NAME, fixName( name ));
    }

    @Override
    public boolean isNamedEntity() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"{" + "name=" + name + '}';
    }


}
