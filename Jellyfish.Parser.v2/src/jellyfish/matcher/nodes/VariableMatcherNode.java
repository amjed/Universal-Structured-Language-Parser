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
    public String toString() {
//        StringBuilder bldr = new StringBuilder();
//        parseTree.fullTreeToString(bldr);
//        return "{"+bldr.toString()+"}"+super.toString();
        return super.toString();
    }

    @Override
    protected void match( InputTokenList input, int inputIndex, MatchResult prevMatch,
                          List<MatchResult> output, boolean matchAtEnd ) {
        List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
        this.parseTree.match( input, inputIndex, null, innerResults, false );
        for ( MatchResult result : innerResults ) {
            List<Object> values = new ArrayList<Object>();
            result.fillValues( values );
            MatchResult firstResult = result.getFirstMatch();
            firstResult.setPreviousMatch( prevMatch );
            output.add( result );
//            output.add(new MatchResult(this,result,result.getInputMatchIndex(),values));
        }
    }

    @Override
    protected void fillWildCardEndResults( InputTokenList input, int inputIndex,
                                           MatchResult prevMatch, List<MatchResult> output ) {
        List<MatchResult> innerResults = new ArrayList<MatchResult>( 100 );
        this.parseTree.match( input, inputIndex, null, innerResults, false );
        for ( MatchResult result : innerResults ) {
            List<Object> values = new ArrayList<Object>();
            result.fillValues( values );
            output.add( new MatchResult( this, prevMatch, result.getInputMatchIndex(), values,
                                         aliasMatcherMap ) );
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
