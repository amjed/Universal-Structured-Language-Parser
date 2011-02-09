package jellyfish.matcher.nodes;

import java.util.*;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.matcher.input.*;

public class RootMatcherNode
	extends MatcherNode
{

    public RootMatcherNode() {
	super();
    }

    @Override
    public String toString() {
	return "/" + super.toString();
    }

    public String fullTreeToString() {
	StringBuilder bldr = new StringBuilder();
	this.fullTreeToString( bldr );
	return bldr.toString();
    }

    /*
    public List<List<MatcherNode>> computeMatrix() {
	List<MatcherNode> endings = new ArrayList<MatcherNode>();
	fillEndings( endings );

	List<List<MatcherNode>> matrix = new ArrayList<List<MatcherNode>>();

	List<MatcherNode> pathToRoot = new ArrayList<MatcherNode>();

	for ( MatcherNode ending : endings ) {
	    pathToRoot.clear();
	    ending.getPathToRoot( pathToRoot );
	    matrix.add( new ArrayList<MatcherNode>( pathToRoot ) );
	}

	return matrix;
    }
     */

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
			  List<MatchResult> finalResults, boolean matchAtEnd ) {
	if ( inputIndex < input.size() ) {
	    for ( MatcherNode node : nextNodes ) {
		node.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
	    }
	}
    }

    public void match( InputTokenList input, List<MatchResult> output ) {
	match( input, 0, null, output, true );
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex, 
					   MatchResult prevMatch, List<MatchResult> finalResults ) {
    }

    protected int compareFunctionality( MatcherNode o ) {
	if ( o == null ) {
	    return -1;
	}
	int compClass = (o.getClass().getCanonicalName().compareTo(
			 this.getClass().getCanonicalName() ));
	return compClass;
    }
}
