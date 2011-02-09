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
    public boolean equals( Object obj ) {
	if ( obj == null ) {
	    return false;
	}
	if ( getClass() != obj.getClass() ) {
	    return false;
	}
	final Pair<First, Second> other = (Pair<First, Second>) obj;
	if ( this.first != other.first && (this.first == null || !this.first.equals( other.first )) ) {
	    return false;
	}
	if ( this.second != other.second &&
	     (this.second == null || !this.second.equals( other.second )) ) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 97 * hash + (this.first != null ? this.first.hashCode() : 0);
	hash = 97 * hash + (this.second != null ? this.second.hashCode() : 0);
	return hash;
    }
    
    @Override
    public String toString() {
        return "{" + first.getClass().getSimpleName()+"='" + first + "', "+second.getClass().getSimpleName()+"='" + second + "'}";
    }

}
