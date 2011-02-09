/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.groovy;

import java.io.File;
import java.io.FileInputStream;
import jellyfish.common.Common;

/**
 *
 * @author Xevia
 */
public class TestGroovyPP {

	public static void main( String[] args ) {

		try {
			File testFile = new File( "groovy/Test.gpp" );
			FileInputStream fis = new FileInputStream( testFile );
			String contents = Common.streamToString( fis );

			System.out.println( contents );

		} catch ( Exception ex ) {
			ex.printStackTrace( System.out );
		}

	}


}
