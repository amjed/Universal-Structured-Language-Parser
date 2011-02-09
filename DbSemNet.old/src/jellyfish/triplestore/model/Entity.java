/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_ENTITY")
public class Entity extends BaseEntity {

    public static final String ATTR_SOURCE = "source";

    @javax.persistence.Column(nullable=true)
    private String source;

    public Entity() {
    }

    public Entity( String name ) {
        super( name );
    }
    
    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        String oldValue = this.source;
        this.source = source;
        changeSupport.firePropertyChange(ATTR_SOURCE, oldValue, source);
    }
    

}
