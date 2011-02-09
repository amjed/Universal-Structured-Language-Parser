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
@javax.persistence.Table(name="TRIPLESTORE_VALUE_INTEGER")
public class IntegerValue extends Value {

    public static final String ATTR_CONTENT = "content";
    
    @javax.persistence.Column(nullable=false)
    private long content;

    public IntegerValue() {
        registerSelf();
    }

    public IntegerValue(int val) {
        this.content = val;
        registerSelf();
    }

    private void registerSelf() {
        super.setIntegerValue( this );
    }

    public long getValue() {
        return content;
    }

    public void setValue( long value ) {
        long oldValue = value;
        this.content = value;
        changeSupport.firePropertyChange(ATTR_CONTENT, oldValue, value);
    }

    

}
