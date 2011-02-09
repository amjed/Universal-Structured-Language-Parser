/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.triplestore.model.enums.ValueType;
import javax.persistence.EnumType;
import javax.persistence.InheritanceType;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_VALUE")
public abstract class Value extends BaseEntity {

    public static final String ATTR_VALUE_TYPE = "valueType";
    public static final String ATTR_STRING_VALUE = "stringValue";
    public static final String ATTR_FLOAT_VALUE = "floatValue";
    public static final String ATTR_INTERGER_VALUE = "integerValue";

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
        ValueType oldValue = valueType;
        this.valueType = valueType;
        changeSupport.firePropertyChange(ATTR_VALUE_TYPE, oldValue, valueType);
    }

    public FloatValue getFloatValue() {
        return floatValue;
    }

    public void setFloatValue( FloatValue floatValue ) {
        FloatValue oldValue = floatValue;
        this.floatValue = floatValue;
        changeSupport.firePropertyChange(ATTR_STRING_VALUE, oldValue, floatValue);
    }

    public IntegerValue getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue( IntegerValue integerValue ) {
        IntegerValue oldValue = integerValue;
        this.integerValue = integerValue;
        changeSupport.firePropertyChange(ATTR_FLOAT_VALUE, oldValue, integerValue);
    }

    public StringValue getStringValue() {
        return stringValue;
    }

    public void setStringValue( StringValue stringValue ) {
        StringValue oldValue = stringValue;
        this.stringValue = stringValue;
        changeSupport.firePropertyChange(ATTR_INTERGER_VALUE, oldValue, stringValue);
    }

    @Override
    public boolean isValue() {
        return true;
    }

}
