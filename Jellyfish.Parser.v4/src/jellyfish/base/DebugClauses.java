package jellyfish.base;

import java.io.*;
import java.util.*;
import jellyfish.common.Common;
import jellyfish.common.Pair;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.StorageTable;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.xml.XmlTripleStore;

/**
 *
 * @author Umran
 */
public class DebugClauses
{

	public static void printAliasTreeValues( AliasTreeNode treeNode, int index, int depth,
											 StorageTable<Object> storageTable ) {
		for ( int i = 0; i < depth; ++i ) {
			System.out.print( "\t" );
		}
		System.out.print( treeNode.getName() );
		if ( index >= 0 ) {
			System.out.print( "[" + index + "]" );
		}
		Object value = storageTable.get( treeNode );
		if ( value != null ) {
			System.out.print( "\t" + value.toString() );
		} else {
			System.out.print( "\tnull" );
		}
		System.out.println();

		for ( String alias : treeNode.getChildAliasSet() ) {
			List<AliasTreeNode> list = treeNode.getChildrenWithAlias( alias );
			if ( list.size() == 1 ) {
				printAliasTreeValues( list.get( 0 ), -1, depth + 1, storageTable );
			} else {
				for ( int i = 0; i < list.size(); ++i ) {
					printAliasTreeValues( list.get( i ), i, depth + 1, storageTable );
				}
			}
		}
	}

	private static void printAliasTree( AliasTreeNode treeNode, int index, int depth ) {
		for ( int i = 0; i < depth; ++i ) {
			System.out.print( "\t" );
		}
		System.out.print( treeNode );
		if ( index >= 0 ) {
			System.out.print( "[" + index + "]" );
		}
//        System.out.print( treeNode.getMatcherNodes() );
		System.out.println();

		for ( String alias : treeNode.getChildAliasSet() ) {
			List<AliasTreeNode> list = treeNode.getChildrenWithAlias( alias );
			if ( list.size() == 1 ) {
				printAliasTree( list.get( 0 ), -1, depth + 1 );
			} else {
				for ( int i = 0; i < list.size(); ++i ) {
					printAliasTree( list.get( i ), i, depth + 1 );
				}
			}
		}
	}

	public static void printAliasTree( AliasTreeNode treeNode ) {
		System.out.println( "Alias Tree:" + Arrays.toString( treeNode.getCompleteName() ) );

		printAliasTree( treeNode, -1, 0 );
	}

	private static void printMatcherTree( MatcherNode node, int depth ) {
		for ( int i = 0; i < depth; ++i ) {
			System.out.print( "\t" );
		}
		System.out.print( node.toString() );
//        System.out.print(" ");
//        System.out.print(node.getAliasTreeNodes());
		System.out.println();
		for ( MatcherNode n : node.getNextNodes() ) {
			printMatcherTree( n, depth + 1 );
		}
	}

	public static void printMatcherTree( MatcherNode node ) {
		System.out.println( "Matcher Tree: " + node );
		printMatcherTree( node, 0 );
	}

	private static class DisplayStruct
	{
		AliasTreeNode node;
		int depth;

		public DisplayStruct( AliasTreeNode node, int depth ) {
			this.node = node;
			this.depth = depth;
		}
	}

	public static Pair<String, String> resultTreeToString( ClauseBase clauseBase,
														   MatchResult matchResult ) {
		assert (matchResult != null);
		StringBuilder aliasTree = new StringBuilder();

//		System.out.println( "Match Result Fill Into Table:" );
		assert(matchResult.getAliasTreeNode()!=null);
		StorageTable storageTable = new StorageTable( matchResult.getAliasTreeNode().getRoot() );
		matchResult.fillValues( storageTable );

		Stack<DisplayStruct> displayStructs = new Stack<DisplayStruct>();
		displayStructs.add( new DisplayStruct( matchResult.getAliasTreeNode().getRoot(), 0 ) );
		while ( !displayStructs.isEmpty() ) {
			DisplayStruct displayStruct = displayStructs.pop();

			for ( int i = 0; i < displayStruct.depth; ++i ) {
				aliasTree.append( "  " );
			}
			aliasTree.append( displayStruct.node.getIndexedName() ).append( " - " );
			aliasTree.append( storageTable.get( displayStruct.node ) );
			aliasTree.append( "\n" );

			List<AliasTreeNode> aliasChildren = displayStruct.node.getChildren();
			for ( int i = aliasChildren.size() - 1; i >= 0; --i ) {
				AliasTreeNode node = aliasChildren.get( i );
				displayStructs.push( new DisplayStruct( node, displayStruct.depth + 1 ) );
			}
		}

		ArrayList<String> values = new ArrayList<String>();
		matchResult.fillStrings( values );
		String concated = clauseBase.getTokenizer().combine( values );

		return Pair.create( aliasTree.toString(), concated );
	}

	private static void match(ClauseBase clauseBase, String input)
	{
		System.out.println( "\nMatching: '" + input + "'" );
		System.out.println( "=====================" );
		List<MatchResult> matchResults = clauseBase.match( input );

		System.out.println( matchResults.size() + " results found." );
		for ( int i = 0; i < matchResults.size(); ++i ) {
			System.out.println( "\nResult " + (i + 1) + " out of " + matchResults.size() );

			MatchResult matchResult = matchResults.get( i );

			Pair<String, String> s = resultTreeToString( clauseBase, matchResult );
			System.out.println( "Result Tree:" );
			System.out.println( "============" );
			System.out.println( s.getFirst() );

			System.out.println( "Concated Results:" );
			System.out.println( "=================" );
			System.out.println( s.getSecond() );
		}
	}

	public static void main( String[] args ) {
		try {
			Common.copyFile( "semnet-1.xml", "semnet.xml" );
			
			System.out.println( "Loading..." );
			System.out.println( "===========" );
			TripleStore tripleStore = new XmlTripleStore( new File( "semnet.xml" ) );

			JellyfishBase jellyfishBase = new JellyfishBase( tripleStore );

			System.out.println( "Done Loading." );
			System.out.println( "--------------" );
			System.out.println();

			String language = "english";

			ClauseBase clauseBase = jellyfishBase.getClauseBase( language );

			assert (clauseBase != null);

			final String input = "show *";

			match( clauseBase, input );

			try {
				System.out.println(  );
				System.out.println( "Editing Semanting Net File" );
				System.out.println( "==========================" );
				
				Common.copyFile( "semnet-2.xml", "semnet.xml" );

				match( clauseBase, input );

			} finally {
				Common.copyFile( "semnet-1.xml", "semnet.xml" );
			}

		} catch ( Exception e ) {
			e.printStackTrace( System.out );
		}
	}
}
