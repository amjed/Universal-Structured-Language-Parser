/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

import java.util.Comparator;

/**
 *
 * @author Umran
 */
public class CaseInsensitiveStringComparator implements Comparator<String> {

    public int compare(String o1, String o2) {
        if (o1==null || o2==null) {
            if (o1==null && o2!=null)
                return -1;
            else
                if (o2==null && o1!=null)
                    return 1;
                else
                    if (o1==null && o2==null)
                        return 0;
        }
        return o1.compareToIgnoreCase(o2);
    }

}
