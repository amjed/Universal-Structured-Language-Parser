/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect;

/**
 *
 * @author Xevia
 */
public class DbLoadException extends RuntimeException {

    public DbLoadException( Throwable cause ) {
        super( cause );
    }

    public DbLoadException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DbLoadException( String message ) {
        super( message );
    }

    public DbLoadException() {
    }

}
