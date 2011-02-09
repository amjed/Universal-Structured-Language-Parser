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
@javax.persistence.Table(name="TRIPLESTORE_VALUE_STRING")
public class StringValue extends Value {

    public static final String ATTR_CONTENT = "content";
    
    @javax.persistence.Column(nullable=false,length=1000)
    private String content;

    public StringValue() {
        registerSelf();
    }

    public StringValue(String val) {
        this.content = val;
        registerSelf();
    }

    private void registerSelf() {
        super.setStringValue( this );
    }

    public String getValue() {
        return content;
    }

    public void setValue( String value ) {
        String oldValue = value;
        this.content = value;
        changeSupport.firePropertyChange(ATTR_CONTENT, oldValue, value);
    }
    
    

}
