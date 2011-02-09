/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

/**
 *
 * @author Umran
 */
public class Pair<First,Second> {

    public static <A,B> Pair<A,B> create(A a, B b) {
        return new Pair<A,B>(a,b);
    }

    private First first;
    private Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" + "first=" + first + ", second=" + second + '}';
    }

}
