/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common.persistence;

import java.lang.reflect.Field;

/**
 *
 * @author Xevia
 */
public class Attribute {

    private Field field;

    public Attribute(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Class getType() {
        return field.getType();
    }

}
