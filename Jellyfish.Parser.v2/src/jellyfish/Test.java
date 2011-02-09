/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish;

import jellyfish.tokenizer.english.EnglishTokenizer;
import java.io.*;
import java.util.*;
import jellyfish.base.ClauseBase;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.StorageTable;
import jellyfish.matcher.clauses.Clause;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.SimpleClause;
import jellyfish.tokenizer.*;

/**
 *
 * @author Umran
 */
public class Test
{

    /*
    public static void main( String[] args ) {

    try {
    Tokenizer tokenizer = new EnglishTokenizer( true );
    XmlClauseParser parser = new XmlClauseParser( tokenizer );

    FileInputStream fin = new FileInputStream( new File( "XmlParserTest.xml" ) );
    parser.parse( fin );
    fin.close();

    Set<String> clauseNames = parser.getClauseNames();

    System.out.println( "Dictionary:" );
    for (DictionaryEntry entry : parser.getLatestDictionary().
    getAllEntries()) {
    System.out.println( "\t'" + entry.getWord() + "'" );
    }

    System.out.println( "Clause Names:" );
    System.out.println( clauseNames );

    RootMatcherNode queryRoot = null;

    for (String name : clauseNames) {
    AndClause clause = parser.getNamedClause( name );

    System.out.println( "Clause '" + name + "':" );
    RootMatcherNode matcherRoot = clause.buildMatchTree();

    if (name.equalsIgnoreCase( "query"))
    queryRoot = matcherRoot;

    System.out.println( "Matrix:" );
    List<List<MatcherNode>> matrix = matcherRoot.computeMatrix();

    for (List<MatcherNode> list:matrix) {
    System.out.println( list );
    }
    }

    if (queryRoot==null) {
    System.out.println( "Unable to find the named clause 'query'" );
    return;
    }


    System.out.println( "Please enter input to test. Enter 'exit' to quit." );
    Scanner scan = new Scanner(System.in);
    InputTokenList inputTokenList = new InputTokenList();
    List<String> inputTokenStrList = new ArrayList<String>();
    List<MatchResult> results = new ArrayList<MatchResult>();
    List<MatchResult> detailedResults = new ArrayList<MatchResult>();

    while (scan.hasNextLine()) {
    String line = scan.nextLine();
    line = line.trim();

    if (line.equalsIgnoreCase( "exit" )) {
    break;
    }

    inputTokenStrList.clear();
    tokenizer.tokenize( inputTokenStrList, line );

    inputTokenList.clear();
    for (String inputToken:inputTokenStrList) {
    InputToken.addInputToken( inputTokenList, parser.getLatestDictionary(), inputToken );
    }

    System.out.println( "inputTokenList = " + inputTokenList);

    results.clear();
    queryRoot.match( inputTokenList, results );

    for (MatchResult result:results) {
    detailedResults.clear();
    result.fillResults( detailedResults );
    System.out.println( ">>" + detailedResults );
    }
    }

    } catch (Exception e) {
    e.printStackTrace( System.out );
    }
    }
     */
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

    public static void printAliasTree( AliasTreeNode treeNode, int index, int depth ) {
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

    public static void printMatcherTree( MatcherNode node, int depth ) {
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

    public static void printClauseTree( Clause c, int depth ) {
        if (c instanceof CompositeClause) {
            CompositeClause clause = (CompositeClause)c;
        } else
            if (c instanceof SimpleClause) {
                SimpleClause clause = (SimpleClause)c;
                
            }
    }

    public static void main( String[] args ) {
        try {
            Tokenizer tokenizer = new EnglishTokenizer( true );
            ClauseBase clauseBase = new ClauseBase( tokenizer );
            {
                FileInputStream fin = new FileInputStream( new File( "XmlParserTest.xml" ) );
                clauseBase.build( fin );
                fin.close();
            }

//            System.out.println( "clauseBase" );
//            System.out.println( "\tmatcher tree" );
//            printMatcherTree( clauseBase.getMatcherRoot(), 0 );
//            for ( AliasTreeNode rootAlias : clauseBase.getRootAliases() ) {
//                System.out.println( "\talias tree: " + rootAlias );
//                printAliasTree( rootAlias, -1, 0 );
//            }

            if ( true != false ) {
                return;
            }

            System.out.println( "Please enter input to test. Enter 'exit' to quit." );
            Scanner scan = new Scanner( System.in );
            List<MatchResult> detailedResults = new ArrayList<MatchResult>();

            while ( scan.hasNextLine() ) {
                String line = scan.nextLine();
                line = line.trim();

                if ( line.equalsIgnoreCase( "exit" ) ) {
                    break;
                }

                List<MatchResult> results = clauseBase.match( line );

                if ( results.isEmpty() ) {
                    System.out.println( "No Results" );
                } else {
                    for ( MatchResult result : results ) {
//                        detailedResults.clear();
//                        result.fillResults( detailedResults );
//                        System.out.println( ">>" + detailedResults );
//            AliasTreeNode.StorageTable<Object> storageTable = clauseBase.getRootAlias().createStorageTable(Object.class);
//                        result.fillValues(storageTable);
//                        printAliasTreeValues(clauseBase.getRootAlias(), -1, 0, storageTable);

//                        for ( MatchResult mr : detailedResults) {
//                            System.out.println("\tInputMatchIndex="+mr.getInputMatchIndex());
//                            System.out.println("\tMatchValue="+mr.getMatchValue());
//                            System.out.println("\tMatchValue.AliasTreeNode="+mr.getMatchingNode().getAliasTreeNode());
//                        }
                    }
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace( System.out );
        }
    }
}
