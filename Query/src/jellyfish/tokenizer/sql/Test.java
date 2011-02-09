/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.tokenizer.sql;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xevia
 */
public class Test {

	public static void main( String[] args ) {
		String input = "select a+table.b from table";
		List<String> tokens = new ArrayList<String>();
		SqlTokenizer sqlTokenizer = new SqlTokenizer();
		sqlTokenizer.tokenize( tokens, input );
		System.out.println( "input: "+input );
		System.out.println( "tokens: "+tokens );
	}

}
