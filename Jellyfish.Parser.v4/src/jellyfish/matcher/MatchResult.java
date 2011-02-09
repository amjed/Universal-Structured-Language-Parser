package jellyfish.matcher;

import jellyfish.matcher.nodes.MatcherNode;
import java.util.List;

public class MatchResult
{

	private AliasTreeNode aliasTreeNode;
	private MatcherNode matchingNode;
	private MatchResult previousMatch;
	private int inputMatchIndex;
	private String matchString;
	private Object matchValue;

	public MatchResult( AliasTreeNode aliasTreeNode,
						MatcherNode matchingNode, MatchResult previousMatch, int inputMatchIndex,
						String matchString,
						Object matchValue ) {
		assert (aliasTreeNode != null);
		this.aliasTreeNode = aliasTreeNode;
		this.matchingNode = matchingNode;
		this.previousMatch = previousMatch;
		this.inputMatchIndex = inputMatchIndex;
		this.matchString = matchString;
		this.matchValue = matchValue;
	}

	public MatchResult getPreviousMatch() {
		return previousMatch;
	}

	public void setPreviousMatch( MatchResult previousMatch ) {
		assert (previousMatch != this);
		this.previousMatch = previousMatch;
	}

	public int getCountFromFirst() {
		MatchResult res = this.previousMatch;
		int count = 0;
		while ( res != null ) {
			++count;
			res = res.previousMatch;
		}
		return count;
	}

	public AliasTreeNode getAliasTreeNode() {
		return aliasTreeNode;
	}

	public void setAliasTreeNode( AliasTreeNode aliasTreeNode ) {
		assert( aliasTreeNode!=null );
		this.aliasTreeNode = aliasTreeNode;
	}

	public MatcherNode getMatchingNode() {
		return matchingNode;
	}

	public void setMatchingNode( MatcherNode matchingNode ) {
		this.matchingNode = matchingNode;
	}

//    public MatchResult getFirstMatch() {
//        if (previousMatch!=null)
//            return previousMatch.getFirstMatch();
//        else
//            return this;
//    }
	public int getInputMatchIndex() {
		return inputMatchIndex;
	}

	public Object getMatchValue() {
		return matchValue;
	}

	public void setMatchValue( Object matchValue ) {
		this.matchValue = matchValue;
	}

	public String getMatchString() {
		return matchString;
	}

	public void setMatchString( String matchString ) {
		this.matchString = matchString;
	}
	/*
	public void fillValues( List<Object> outputList ) {
	if ( previousMatch == null ) {
	outputList.clear();
	} else {
	previousMatch.fillValues(outputList);
	}
	if (this.matchValue!=null)
	outputList.add(this.matchValue);
	}
	 */

	public void fillStrings( List<String> outputList ) {
		if ( previousMatch == null ) {
			outputList.clear();
		} else {
			previousMatch.fillStrings( outputList );
		}
		outputList.add( this.matchString );
	}

	public void fillValues( StorageTable<Object> storageTable ) {

		storageTable.clear();

		MatchResult result = this;
		while ( result != null ) {
			if (result.aliasTreeNode.isNormalNode() || result.aliasTreeNode.isRoot())
				storageTable.put( result.aliasTreeNode, result.getMatchValue() );

			result = result.previousMatch;
		}
	}

	@Override
	public String toString() {
		return "{(" + inputMatchIndex + ")" + matchValue + "}";
	}
}
