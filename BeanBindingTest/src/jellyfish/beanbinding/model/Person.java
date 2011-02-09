/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.beanbinding.model;

import java.lang.reflect.Field;
import jellyfish.beanbinding.BoundBean;

/**
 *
 * @author Xevia
 */
public class Person extends BoundBean {

    public static final Field ATTR_FIRST_NAME = getDeclaredField(Person.class, "firstName");
    public static final Field ATTR_LAST_NAME = getDeclaredField(Person.class, "lastName");

    private String firstName;
    private String lastName;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        setField(ATTR_FIRST_NAME, firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        setField(ATTR_LAST_NAME, lastName);
    }

    @Override
    public String toString() {
        return "Person{" + "firstName=" + firstName + ", lastName=" + lastName + '}';
    }

}
