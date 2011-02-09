/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.io.File;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 *
 * @author Xevia
 */
public class Test
{

	public static void main( String[] args ) {
		
		try {


			ClassLoader parent = Test.class.getClassLoader();
			GroovyClassLoader loader = new GroovyClassLoader( parent );
			Class groovyClass = loader.parseClass( new File( "groovy/Test.gpp" ) );
			GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

			Object[] path = { "Hello world!" };
			groovyObject.setProperty( "args", path );
			Object[] argz = {};

			groovyObject.invokeMethod( "run", argz );
		} catch ( Exception ex ) {
			ex.printStackTrace( System.out );
		}

	}
}
