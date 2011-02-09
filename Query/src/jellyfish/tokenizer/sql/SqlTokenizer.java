/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.tokenizer.sql;

import java.util.List;
import java.util.regex.*;
import jellyfish.tokenizer.Tokenizer;

/**
 *
 * @author Umran
 *
 * PLEASE NOTE:
 *
 *  When applied with an equation, the tokenizer fails to tokenize
 *      + or - preceeding a number as a symbol, but recognizes is as
 *      part of the number (either making the number positive or negative).
 *
 *  breakDashedWords constructor parameter refers on how to deal with words
 *      joined with dashes (-) to form a single word (e.g. kuala-lumpur),
 *      if breakDashedWords is true, then it will break it down to its composite
 *      words, otherwise leave it as it is.
 */
public class SqlTokenizer
		implements Tokenizer
{

	private enum TokenType
	{

		STRING,
		WORD,
		NUMBER,
		SYMBOL,
		UNKNOWN
	};
	private static final String WORD_REGEX = "([a-zA-Z_][a-zA-Z0-9_]*)";
	private static final String NUMBER_REGEX = "([+-]?(?:[0-9]+(?:\\.[0-9]*)?)|(?:\\.[0-9]+))";
	private static final String SYMBOL_REGEX = "([^\\sa-zA-Z0-9])";
	private static final String QUOTATION_REGEX = "(?:('(?:[^\']|'')*')|(\"(?:[^\"]|\"\")*\"))";
	private static final String TOKEN_REGEX = "(?:" + QUOTATION_REGEX + ")|(?:" + WORD_REGEX +
											  ")|(?:" + NUMBER_REGEX + ")|(?:" + SYMBOL_REGEX + ")";
//    private static final String tokenRegex = "(?:"+wordRegex+")";
//    private static final String tokenRegex = "(?:"+numberRegex+")";
//    private static final String tokenRegex = "(?:"+symbolRegex+")";
//    private static final String tokenRegex = "(?:"+stringRegex+")";
	private Pattern tokenPat;
	private Pattern stringPat;
	private Pattern wordPat;
	private Pattern symbolPat;
	private Pattern numberPat;

	public SqlTokenizer() {
		this.tokenPat = Pattern.compile( TOKEN_REGEX, Pattern.CASE_INSENSITIVE );
		this.stringPat = Pattern.compile( QUOTATION_REGEX, Pattern.CASE_INSENSITIVE );
		this.wordPat = Pattern.compile( WORD_REGEX, Pattern.CASE_INSENSITIVE );
		this.numberPat = Pattern.compile( NUMBER_REGEX, Pattern.CASE_INSENSITIVE );
		this.symbolPat = Pattern.compile( SYMBOL_REGEX, Pattern.CASE_INSENSITIVE );
	}

	@Override
	public void tokenize( List<String> outputList, String input ) {
		outputList.clear();
		Matcher matcher = tokenPat.matcher( input );
		while ( matcher.find() ) {
			for ( int i = 1; i <= matcher.groupCount(); ++i ) {
				String groupCaptured = matcher.group( i );
				if ( groupCaptured != null ) {
					outputList.add( groupCaptured );
				}
			}
		}
	}

	@Override
	public String combine( List<String> tokens ) {
		StringBuilder builder = new StringBuilder();

		TokenType prevToken = TokenType.UNKNOWN;
		TokenType currentToken = TokenType.UNKNOWN;

		for ( int i = 0; i < tokens.size(); ++i ) {
			String token = tokens.get( i );

			if ( stringPat.matcher( token ).matches() ) {
				currentToken = TokenType.STRING;
			} else if ( wordPat.matcher( token ).matches() ) {
				currentToken = TokenType.WORD;
			} else if ( numberPat.matcher( token ).matches() ) {
				currentToken = TokenType.NUMBER;
			} else if ( symbolPat.matcher( token ).matches() ) {
				currentToken = TokenType.SYMBOL;
			} else {
				currentToken = TokenType.UNKNOWN;
			}

//            System.out.println("token="+token+" type="+currentToken);

			switch ( currentToken ) {
				case SYMBOL: {
					switch ( prevToken ) {
						case WORD: {
							builder.append( token );
							break;
						}
						default: {
							if ( builder.length() > 0 ) {
								builder.append( " " );
							}
							builder.append( token );
						}
					}
					break;
				}
				default: {
					if ( builder.length() > 0 ) {
						builder.append( " " );
					}
					builder.append( token );
				}
			}

			prevToken = currentToken;
		}

		return builder.toString();
	}
}
