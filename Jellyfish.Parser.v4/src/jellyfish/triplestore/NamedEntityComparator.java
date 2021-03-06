/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

import java.util.Comparator;
import jellyfish.triplestore.model.NamedEntity;

/**
 *
 * @author Xevia
 */
public class NamedEntityComparator<Type extends NamedEntity> implements Comparator<Type> {

    public int compare(Type o1, Type o2) {
        int i = o1.getClass().getName().compareTo(o2.getClass().getName());
        if (i!=0)
            return i;
        return o1.getName().compareTo(o2.getName());
    }

}
