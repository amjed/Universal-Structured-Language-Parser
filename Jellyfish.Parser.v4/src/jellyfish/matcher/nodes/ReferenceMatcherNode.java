package jellyfish.matcher.nodes;

import java.util.*;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public class ReferenceMatcherNode
        extends MatcherNode
{

    private static int variableMatcherNodeCounter = 0;
    protected String name;
    protected RootMatcherNode parseTree;
    protected Map<AliasTreeNode,AliasTreeNode> innerToOuterAliasMap;

    public ReferenceMatcherNode( String name, Map<AliasTreeNode,AliasTreeNode> innerToOuterAliasMap ) {
        super();
        this.innerToOuterAliasMap = innerToOuterAliasMap;
        if ( name == null || name.isEmpty() ) {
            this.name = "ref" + (++variableMatcherNodeCounter);
        } else {
            this.name = name;
        }
        this.parseTree = new RootMatcherNode();
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
    public void setAcceptEnding( ) {
        super.setAcceptEnding();
    }
    
    @Override
    public String toString() {
//        StringBuilder bldr = new StringBuilder();
//        parseTree.fullTreeToString(bldr);
//        return "{"+bldr.toString()+"}"+super.toString();
        return "ref("+parseTree+")"+super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> finalResults, boolean matchAtEnd ) {
	InputToken inputToken = input.get( inputIndex );

	if (inputToken.isWildCard()) {
	    super.match( input, inputIndex, prevMatch, finalResults, matchAtEnd );
	} else {
	    System.out.println( "ref match: "+input+" ["+inputIndex+"]" );
	    List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
	    this.parseTree.match( input, inputIndex,
				  null, innerResults, false );
	    for ( MatchResult innerResult : innerResults ) {
		MatchResult first = innerResult;
		MatchResult result = innerResult;
		while (result!=null) {
			AliasTreeNode outerAliasTreeNode = innerToOuterAliasMap.get( result.getAliasTreeNode() );
			if (outerAliasTreeNode==null) {
				throw new RuntimeException("No outer alias found for inner alias: "+result.getAliasTreeNode());
			}
		    result.setAliasTreeNode( outerAliasTreeNode );
		    
		    result = result.getPreviousMatch();
		    if (result!=null) first = result;
		}

		first.setPreviousMatch( prevMatch );

		super.match( input,
			     innerResult.getInputMatchIndex(),
			     innerResult,
			     finalResults,
			     matchAtEnd );
	    }
	}

    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> finalResults ) {
        List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
        this.parseTree.match( input, inputIndex, null, innerResults, false );
        for ( MatchResult innerResult : innerResults ) {
	    
//            List<Object> values = new ArrayList<Object>();
//            innerResult.fillValues( values );

	    MatchResult first = innerResult;
	    MatchResult result = innerResult;
	    while (result!=null) {
		result.setAliasTreeNode( innerToOuterAliasMap.get( result.getAliasTreeNode() ) );

		result = result.getPreviousMatch();
		if (result!=null) first = result;
	    }

	    first.setPreviousMatch( prevMatch );

            finalResults.add( innerResult );
        }
    }

    protected int compareFunctionality( MatcherNode o ) {
        if ( o == null ) {
            return -1;
        }
        int compClass = (o.getClass().getCanonicalName().compareTo(
                         this.getClass().getCanonicalName() ));
        if ( compClass == 0 ) {
            ReferenceMatcherNode varNode = (ReferenceMatcherNode) o;
            return this.name.compareToIgnoreCase( varNode.name );
        } else {
            return compClass;
        }
    }
}
