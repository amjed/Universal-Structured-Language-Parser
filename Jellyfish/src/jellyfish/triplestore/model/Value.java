/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.triplestore.model.enums.ValueType;
import javax.persistence.EnumType;
import javax.persistence.InheritanceType;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_VALUE")
public abstract class Value extends BaseEntity {

    public static final Attribute ATTR_VALUE_TYPE = getDeclaredField(Value.class, "valueType");
    public static final Attribute ATTR_STRING_VALUE = getDeclaredField(Value.class, "stringValue");
    public static final Attribute ATTR_FLOAT_VALUE = getDeclaredField(Value.class, "floatValue");
    public static final Attribute ATTR_INTEGER_VALUE = getDeclaredField(Value.class, "integerValue");

    @javax.persistence.Enumerated(EnumType.ORDINAL)
    private ValueType valueType;

    @javax.persistence.Transient
    private StringValue stringValue;

    @javax.persistence.Transient
    private FloatValue floatValue;

    @javax.persistence.Transient
    private IntegerValue integerValue;

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType( ValueType valueType ) {
        setAttribute(ATTR_VALUE_TYPE, valueType);
    }

    public FloatValue getFloatValue() {
        return floatValue;
    }

    public void setFloatValue( FloatValue floatValue ) {
        setAttribute(ATTR_FLOAT_VALUE, floatValue);
    }

    public IntegerValue getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue( IntegerValue integerValue ) {
        setAttribute(ATTR_INTEGER_VALUE, integerValue);
    }

    public StringValue getStringValue() {
        return stringValue;
    }

    public void setStringValue( StringValue stringValue ) {
        setAttribute(ATTR_STRING_VALUE, stringValue);
    }

    @Override
    public boolean isValue() {
        return true;
    }

}
