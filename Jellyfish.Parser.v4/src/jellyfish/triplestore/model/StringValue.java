/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import javax.persistence.InheritanceType;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_VALUE_STRING")
public class StringValue extends Value {

    public static final Attribute ATTR_CONTENT = getDeclaredField(StringValue.class, "content");
    
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
        setAttribute(ATTR_CONTENT, value);
    }

    @Override
    public String toString() {
	return "s'" + content + "'";
    }
    
    

}
