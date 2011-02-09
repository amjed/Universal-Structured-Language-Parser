/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import java.lang.reflect.Field;
import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import java.util.*;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_ENTITYNAME")
public class EntityName extends PersistenceObject {

    public static final Attribute ATTR_NAME = getDeclaredField(EntityName.class, "name");
    public static final Attribute ATTR_ENTITY = getDeclaredField(EntityName.class, "entity");
    public static final Attribute ATTR_LANGUAGE = getDeclaredField(EntityName.class, "language");

    @javax.persistence.ManyToOne(optional=false)
    private Language language;

    @javax.persistence.ManyToOne(optional=false)
    private Entity entity;

    private String name;

    public EntityName() {
    }
    
    public EntityName( Language language, Entity entity, String name ) {
        this.language = language;
        this.entity = entity;
        this.name = name;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity( Entity entity ) {
        setAttribute(ATTR_ENTITY, entity);
    }
    
    public Language getLanguage() {
        return language;
    }

    public void setLanguage( Language language ) {
        setAttribute(ATTR_LANGUAGE, language);
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        setAttribute(ATTR_NAME, name);
    }

    
}
