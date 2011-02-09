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
//@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_RELATIONSHIP")
public class Relationship extends BaseEntity {

    public static final String ATTR_TRANSITIVE = "transitive";
    public static final String ATTR_SYMMETRIC = "symmetric";

    @javax.persistence.Column(nullable=false)
    private boolean transitive;

    @javax.persistence.Column(nullable=false)
    private boolean symmetric;

    public Relationship() {
    }

    public Relationship( String name, boolean transitive,
                                        boolean symmetric ) {
        super( name );
        this.transitive = transitive;
        this.symmetric = symmetric;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public void setSymmetric( boolean symmetric ) {
        boolean oldValue = symmetric;
        this.symmetric = symmetric;
        changeSupport.firePropertyChange(ATTR_SYMMETRIC, oldValue, symmetric);
    }

    public boolean isTransitive() {
        return transitive;
    }

    public void setTransitive( boolean transitive ) {
        boolean oldValue = transitive;
        this.transitive = transitive;
        changeSupport.firePropertyChange(ATTR_TRANSITIVE, oldValue, transitive);
    }

    @Override
    public boolean isRelationship() {
        return true;
    }



}
