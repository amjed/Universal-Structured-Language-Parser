package jellyfish.matcher.nodes;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public abstract class MatcherNode
		implements Comparable<MatcherNode>
{

	private static long idSource = 0;
	private long id = ++idSource;
	protected boolean acceptedEnding;
	protected MatcherNode parentNode;
	protected boolean nextNodesInitialized;
	protected List<MatcherNode> nextNodes;
	protected AliasTreeNode aliasTreeNode;

	public MatcherNode() {
		this.parentNode = null;
		this.acceptedEnding = false;
		nextNodesInitialized = false;
		this.nextNodes = Collections.EMPTY_LIST;
	}

	public MatcherNode getParentNode() {
		return parentNode;
	}

	protected void setParentNode( MatcherNode parentNode ) {
		this.parentNode = parentNode;
	}

	private synchronized void initNextNodes() {
		if ( nextNodesInitialized ) {
			return;
		}

		this.nextNodes = new ArrayList<MatcherNode>();

		nextNodesInitialized = true;
	}

	public final <E extends MatcherNode> E registerNextNode( E node ) {
		if ( !nextNodesInitialized ) {
			initNextNodes();
		}

		nextNodes.add( node );

		return node;
	}

	/*
	 * Removes the next node given the instance.
	 * Please note that this function checks by the identity (i.e. == ) and not the equals function.
	 */
	public void removeNextNode( MatcherNode node ) {

		for ( int i = nextNodes.size() - 1; i >= 0; --i ) {
			if ( nextNodes.get( i ) == node ) {
				nextNodes.remove( i );
			}
		}

	}

	public MatcherNode getRoot() {
		MatcherNode node = this;
		while ( node.parentNode != null ) {
			node = node.parentNode;
		}
		return node;
	}

	public void getPathToRoot( List<MatcherNode> outputPath ) {
		Stack<MatcherNode> pathStack = new Stack<MatcherNode>();
		outputPath.clear();
		MatcherNode node = this;
		while ( node != null ) {
			pathStack.push( node );
			node = node.parentNode;
		}

		while ( !pathStack.isEmpty() ) {
			outputPath.add( pathStack.pop() );
		}
	}

	public List<MatcherNode> getNextNodes() {
		return nextNodes;
	}

	public boolean isAcceptedEnding() {
		return acceptedEnding;
	}

	public void setAcceptEnding() {
		if ( acceptedEnding ) {
			System.out.println( "WARNING: The parser node '" + this +
								"' is already an ending node.\n" );
		}
		this.acceptedEnding = true;
	}

	public AliasTreeNode getAliasTreeNode() {
		return aliasTreeNode;
	}

	public void setAliasTreeNode( AliasTreeNode aliasTreeNode ) {
		this.aliasTreeNode = aliasTreeNode;
	}

	protected abstract void fillWildCardEndResults(
			InputTokenList input, int inputIndex,
			MatchResult prevMatch,
			List<MatchResult> output );

	private boolean isWildCardEnding( InputTokenList input, int inputIndex, boolean matchAtEnd ) {
		InputToken token = input.get( inputIndex );

		return token.isLastWildCard() &&
			   this.acceptedEnding && (!matchAtEnd || inputIndex == input.size() - 1);
	}

	private boolean isNonWildCardEnding( InputTokenList input, int inputIndex, boolean matchAtEnd ) {
//        InputToken token = input.get( inputIndex );

		return this.acceptedEnding && (!matchAtEnd || inputIndex == input.size() - 1);
	}

	protected boolean isEnding( InputTokenList input, int inputIndex, boolean matchAtEnd ) {
		InputToken token = input.get( inputIndex );

		if ( token.isWildCard() ) {
			return isWildCardEnding( input, inputIndex, matchAtEnd );
		} else {
			return isNonWildCardEnding( input, inputIndex, matchAtEnd );
		}
	}

	protected void match( InputTokenList input, int inputIndex,
						  MatchResult prevMatch,
						  List<MatchResult> finalResults,
						  boolean matchAtEnd ) {
		InputToken token = input.get( inputIndex );

		//  we expect that the descendant match function will process this first..
		if ( token.isWildCard() ) {
			//  if the token is a wild-card, our work here is to try all possibilities

//            DebugCommons.out("wild-card found");

			ArrayList<MatchResult> selfResults = new ArrayList<MatchResult>();
			fillWildCardEndResults( input, inputIndex, prevMatch, selfResults );

			if ( token.isLastWildCard() ) {
//                DebugCommons.out("last-wild-card found");
				if ( isWildCardEnding( input, inputIndex, matchAtEnd ) ) {
//                    DebugCommons.out("INCLUDE RESULT: "+selfResults);
					finalResults.addAll( selfResults );
				}
			} else {
				this.match( input, inputIndex + 1,
							prevMatch, finalResults, matchAtEnd );
			}

			for ( MatchResult selfResult : selfResults ) {
				for ( MatcherNode node : nextNodes ) {
					node.match( input, inputIndex, selfResult, finalResults,
								matchAtEnd );
				}
			}

		} else {
			//  if not, then the descendant match has had a successful match

//            DebugCommons.out("non-wild-card found");

			//      our work here is to first check if we're to add this as an output
			if ( isNonWildCardEnding( input, inputIndex, matchAtEnd ) ) {
//                System.out.println( "INCLUDE RESULT: "+prevMatch );
				finalResults.add( prevMatch );
			}

			//      then disperse the next token (if any)...
			int nextInputIndex = inputIndex + 1;

			if ( nextInputIndex < input.size() ) {
				for ( MatcherNode node : nextNodes ) {
					node.match( input, nextInputIndex,
								prevMatch, finalResults, matchAtEnd );
				}
			}
		}
	}

	@Override
	public String toString() {
		return id + (this.acceptedEnding ? "$" : "") + (aliasTreeNode != null ? ("{" + aliasTreeNode +
																				 "}") : "");
	}

	/*
	 * This has been done to make sure that hash code is not overriden by inheriting classes.
	 *  The default (Object.hasCode()) is used to compare the MatcherNodes by unique instances.
	 */
	@Override
	final public int hashCode() {
		return super.hashCode();
	}

	public int compareTo( MatcherNode o ) {
		return this.hashCode() - o.hashCode();
	}

	protected abstract int compareFunctionality( MatcherNode matcherNode );

	protected void fillEndings( List<MatcherNode> endings ) {
		if ( this.acceptedEnding ) {
			endings.add( this );
		}

		for ( MatcherNode node : nextNodes ) {
			node.fillEndings( endings );
		}
	}

	protected void fullTreeToString( StringBuilder bldr ) {
		bldr.append( this.toString() );
		if ( nextNodes != null && !nextNodes.isEmpty() ) {
			bldr.append( "[" );
			boolean first = true;
			if ( nextNodes != null ) {
				for ( MatcherNode node : nextNodes ) {
					if ( first ) {
						first = false;
					} else {
						bldr.append( "," );
					}
					node.fullTreeToString( bldr );
				}
			}
			bldr.append( "]" );
		}
	}
}
