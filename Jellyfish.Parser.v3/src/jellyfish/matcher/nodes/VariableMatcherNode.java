package jellyfish.matcher.nodes;

import java.util.*;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public class VariableMatcherNode
        extends MatcherNode
{

    private static int variableMatcherNodeCounter = 0;
    protected String name;
    protected RootMatcherNode parseTree;
    protected Map<AliasMatcherMap,AliasMatcherMap> innerToOuterMatcherMap;

    public VariableMatcherNode( String name, Map<AliasMatcherMap,AliasMatcherMap> innerToOuterMatcherMap ) {
        super();
        this.innerToOuterMatcherMap = innerToOuterMatcherMap;
        if ( name == null || name.isEmpty() ) {
            this.name = "var" + (++variableMatcherNodeCounter);
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
        return "var"+super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> finalResults, boolean matchAtEnd ) {
        System.out.println( "var match: "+input+" ["+inputIndex+"]" );
        List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
        this.parseTree.match( input, inputIndex, null, innerResults, false );
        for ( MatchResult innerResult : innerResults ) {
            List<Object> values = new ArrayList<Object>();
            innerResult.fillValues( values );

            System.out.println( "match: "+innerResult+"="+values );

            MatchResult firstResult = innerResult.getFirstMatch();
            firstResult.setPreviousMatch( prevMatch );

            AliasMatcherMap aliasMatcherMap = innerResult.getAliasMatcherMap();
            if (aliasMatcherMap!=null) {
                System.out.println( "var inner result alias matcher map is NOT null " );
                aliasMatcherMap = innerToOuterMatcherMap.get( aliasMatcherMap );
                if (aliasMatcherMap==null)
                    System.out.println( "var outer result alias matcher map is null" );
                innerResult.setAliasMatcherMap( aliasMatcherMap );
            } else {
                System.out.println( "var inner result alias matcher map is null " );
            }

            super.match( input, inputIndex,
                         //                         new MatchResult( this, prevMatch, inputIndex, values, aliasMatcherMap ),
                         innerResult,
                         finalResults, matchAtEnd );
//            output.add(new MatchResult(this,result,result.getInputMatchIndex(),values));
        }
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> finalResults ) {
        List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
        this.parseTree.match( input, inputIndex, null, innerResults, false );
        for ( MatchResult result : innerResults ) {
//            List<Object> values = new ArrayList<Object>();
//            result.fillValues( values );
//            output.add( new MatchResult( this, prevMatch, result.getInputMatchIndex(), values,
//                                         aliasMatcherMap ) );

            List<Object> values = new ArrayList<Object>();
            result.fillValues( values );

            MatchResult firstResult = result.getFirstMatch();
            firstResult.setPreviousMatch( prevMatch );

            AliasMatcherMap aliasMatcherMap = result.getAliasMatcherMap();
            if (aliasMatcherMap!=null) {
                aliasMatcherMap = innerToOuterMatcherMap.get( aliasMatcherMap );
                result.setAliasMatcherMap( aliasMatcherMap );
            }

            finalResults.add( result );
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
