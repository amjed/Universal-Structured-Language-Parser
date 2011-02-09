package jellyfish.matcher.nodes;

import java.util.*;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;
import jellyfish.tokenizer.Tokenizer;

public class VariableMatcherNode
		extends MatcherNode
{

	private static int variableMatcherNodeCounter = 0;
	protected Tokenizer tokenizer;
	protected String name;
	protected RootMatcherNode parseTree;

	public VariableMatcherNode( Tokenizer tokenizer, String name ) {
		super();
		this.tokenizer = tokenizer;
		assert (name != null);
//        if ( name == null || name.isEmpty() ) {
//            this.name = "var" + (++variableMatcherNodeCounter);
//        } else {
//            this.name = name;
//        }
		this.parseTree = null;
	}

	public String getName() {
		return name;
	}

	public RootMatcherNode getParseTree() {
		return parseTree;
	}

	public void setParseTree( RootMatcherNode parseTree ) {
		this.parseTree = parseTree;
	}

	@Override
	public void setAcceptEnding() {
		super.setAcceptEnding();
	}

	@Override
	public String toString() {
//        StringBuilder bldr = new StringBuilder();
//        parseTree.fullTreeToString(bldr);
//        return "{"+bldr.toString()+"}"+super.toString();
		return "var(" + parseTree + ")" + super.toString();
	}

	@Override
	protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
						  List<MatchResult> finalResults, boolean matchAtEnd ) {
		System.out.println( "var match: " + input + " [" + inputIndex + "]" );
		InputToken inputToken = input.get( inputIndex );
		if ( inputToken.isWildCard() ) {
			super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
		} else {
			List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
			List<String> matchedLineValues = new ArrayList<String>();
			this.parseTree.match( input, inputIndex,
								  null, innerResults, false );
			for ( MatchResult innerResult : innerResults ) {
				matchedLineValues.clear();
				innerResult.fillStrings( matchedLineValues );
				String matchedLine = tokenizer.combine( matchedLineValues );

				System.out.println( "match: text='" + matchedLineValues + "' value=" + innerResult.
						getMatchValue() );

				finalResults.add( new MatchResult( aliasTreeNode,
												   this,
												   prevMatch,
												   inputIndex,
												   matchedLine,
												   innerResult.getMatchValue() ) );
			}
		}

	}

	@Override
	protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
										   MatchResult prevMatch, List<MatchResult> finalResults ) {
		List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
		this.parseTree.match( input, inputIndex, null, innerResults, false );

		List<String> matchedLineValues = new ArrayList<String>();

		for ( MatchResult innerResult : innerResults ) {
			matchedLineValues.clear();
			innerResult.fillStrings( matchedLineValues );
			String matchedLine = tokenizer.combine( matchedLineValues );

			System.out.println( "match: text='" + matchedLineValues + "' value=" + innerResult.
					getMatchValue() );

			finalResults.add( new MatchResult( aliasTreeNode,
											   this,
											   prevMatch,
											   inputIndex,
											   matchedLine,
											   innerResult.getMatchValue() ) );
		}

	}

	protected int compareFunctionality( MatcherNode o ) {
		if ( o == null ) {
			return -1;
		}
		int compClass = (o.getClass().getCanonicalName().compareTo(
						 this.getClass().getCanonicalName() ));
		if ( compClass == 0 ) {
			VariableMatcherNode varNode = (VariableMatcherNode) o;
			return this.name.compareToIgnoreCase( varNode.name );
		} else {
			return compClass;
		}
	}
}
