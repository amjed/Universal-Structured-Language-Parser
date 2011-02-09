/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.base;

import jellyfish.tokenizer.english.EnglishTokenizer;
import java.io.*;
import java.util.*;
import jellyfish.base.ClauseBase;
import jellyfish.common.Pair;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.StorageTable;
import jellyfish.matcher.parser.clauses.Clause;
import jellyfish.matcher.parser.clauses.CompositeClause;
import jellyfish.matcher.parser.clauses.SimpleClause;
import jellyfish.tokenizer.*;

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
        System.out.print( treeNode.getName() );
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
        System.out.println( "Matcher Tree: "+node );
        printMatcherTree( node, 0 );
    }

    private static class DisplayStruct {
        AliasTreeNode node;
        int depth;

        public DisplayStruct( AliasTreeNode node, int depth ) {
            this.node = node;
            this.depth = depth;
        }
    }

    public static Pair<String,String> resultTreeToString( ClauseBase clauseBase, MatchResult matchResult )
    {
        StringBuilder aliasTree = new StringBuilder();

	AliasMatcherMap aliasMatcherMap = matchResult.getAliasMatcherMap();
	System.out.println( "result alias matcher map = "+aliasMatcherMap );
//            MatcherNode matchingNode = matchResult.getMatchingNode();

	StorageTable storageTable = new StorageTable(aliasMatcherMap.getRootNode());
	matchResult.fillValues( storageTable );

	Stack<DisplayStruct> displayStructs = new Stack<DisplayStruct>();
	displayStructs.add( new DisplayStruct( aliasMatcherMap.getRootNode(), 0 ) );
	while (!displayStructs.isEmpty()) {
	    DisplayStruct displayStruct = displayStructs.pop();

	    for (int i=0; i<displayStruct.depth; ++i)
		aliasTree.append( "  " );
	    aliasTree.append( displayStruct.node.getIndexedName() ).append( " - " );
	    aliasTree.append( storageTable.get( displayStruct.node ) );
	    aliasTree.append( "\n" );

	    List<AliasTreeNode> aliasChildren = displayStruct.node.getChildren();
	    for (int i=aliasChildren.size()-1; i>=0; --i) {
		AliasTreeNode node = aliasChildren.get( i );
		displayStructs.push( new DisplayStruct( node, displayStruct.depth+1) );
	    }
	}

	ArrayList values = new ArrayList();
	matchResult.fillValues( values );
	ArrayList<String> valueStr = new ArrayList<String>(values.size());
	for (Object val:values)
	    valueStr.add( val.toString() );

	String concated = clauseBase.getTokenizer().combine( valueStr );

	return Pair.create( aliasTree.toString(), concated );
    }

    public static void main( String[] args ) {
        try {
	    System.out.println( "Building Base" );
	    System.out.println( "=============" );
            Tokenizer tokenizer = new EnglishTokenizer( true );
            ClauseBase clauseBase = new ClauseBase( tokenizer );
            {
                FileInputStream fin = new FileInputStream( new File( "default.xml" ) );
                clauseBase.build( fin );
                fin.close();
            }

	    String input = "a b b";
	    System.out.println( "\nMatching: '"+input+"'" );
	    System.out.println( "=====================" );
	    List<MatchResult> matchResults = clauseBase.match( input );

	    System.out.println( matchResults.size()+" results found." );
	    for (int i=0; i<matchResults.size(); ++i) {
		System.out.println( "\nResult "+(i+1)+" out of "+matchResults.size() );

		MatchResult matchResult = matchResults.get( i );

		Pair<String,String> s = resultTreeToString( clauseBase, matchResult );
		System.out.println( "Result Tree:" );
		System.out.println( "============" );
		System.out.println( s.getFirst() );

		System.out.println( "Concated Results:" );
		System.out.println( "=================" );
		System.out.println( s.getSecond() );
	    }

        } catch ( Exception e ) {
            e.printStackTrace( System.out );
        }
    }
}
