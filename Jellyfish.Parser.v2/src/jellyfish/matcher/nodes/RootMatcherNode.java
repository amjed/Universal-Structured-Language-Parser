package jellyfish.matcher.nodes;

import java.util.*;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public class RootMatcherNode extends MatcherNode {

    public RootMatcherNode() {
        super();
    }

    @Override
    public String toString() {
        return "/"+super.toString();
    }

    public String fullTreeToString() {
        StringBuilder bldr = new StringBuilder();
        this.fullTreeToString(bldr);
        return bldr.toString();
    }

    public List<List<MatcherNode>> computeMatrix() {
        List<MatcherNode> endings = new ArrayList<MatcherNode>();
        fillEndings( endings );

        List<List<MatcherNode>> matrix = new ArrayList<List<MatcherNode>>();

        List<MatcherNode> pathToRoot = new ArrayList<MatcherNode>();

        for (MatcherNode ending:endings) {
            pathToRoot.clear();
            ending.getPathToRoot( pathToRoot );
            matrix.add( new ArrayList<MatcherNode>(pathToRoot) );
        }

        return matrix;
    }

    @Override
    protected void match(InputTokenList input, int inputIndex, MatchResult prevMatch, List<MatchResult> output, boolean matchAtEnd) {
        if (inputIndex<input.size()) {
            InputToken token = input.get(inputIndex);
            if (token.isWildCard()) {
//                System.out.println( "RootMatcherNode>>wild-card found" );
                for (MatcherNode node:nextConstNodes.values()) {
                    node.match(input, inputIndex, prevMatch, output, matchAtEnd);
                }
                for (MatcherNode node:nextOtherNodes.values()) {
//                    System.out.println( "RootMatcherNode>>processing:"+node );
                    node.match(input, inputIndex, prevMatch, output, matchAtEnd);
                }
            } else {
//                System.out.println( "RootMatcherNode>>non-wild-card found : "+token );
                if (token.isInDictionary() && nextConstNodes!=null) {
//                    System.out.println( "RootMatcherNode>>\tdictionary token found" );
                    MatcherNode node = nextConstNodes.get(token.getDictionaryEntryId());
                    if (node!=null)
                        node.match(input, inputIndex, prevMatch, output, matchAtEnd);
                }

                for (MatcherNode node:nextOtherNodes.values())
                    node.match(input, inputIndex, prevMatch, output, matchAtEnd);
            }
        }
    }

    public void match(InputTokenList input, List<MatchResult> output) {
        match(input, 0, null, output, true);
    }

    @Override
    protected void fillWildCardEndResults(InputTokenList input, int inputIndex, MatchResult prevMatch, List<MatchResult> output) {
        
    }
    
    protected int compareFunctionality(MatcherNode o) {
        if (o==null) return -1;
        int compClass = (o.getClass().getCanonicalName().compareTo( this.getClass().getCanonicalName() ));
        return compClass;
    }

}
