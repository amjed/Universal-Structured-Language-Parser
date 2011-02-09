/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import javax.persistence.InheritanceType;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_VALUE_FLOAT")
public class FloatValue extends Value {

    public static final String ATTR_CONTENT = "content";

    @javax.persistence.Column(nullable=false)
    private double content;

    public FloatValue() {
        registerSelf();
    }

    public FloatValue(double val) {
        this.content = val;
        registerSelf();
    }

    private void registerSelf() {
        super.setFloatValue( this );
    }

    public double getValue() {
        return content;
    }

    public void setValue( double value ) {
        double oldValue = value;
        this.content = value;
        changeSupport.firePropertyChange(ATTR_CONTENT, oldValue, value);
    }

    

}
