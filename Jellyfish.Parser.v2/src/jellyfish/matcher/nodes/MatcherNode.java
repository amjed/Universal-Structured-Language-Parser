package jellyfish.matcher.nodes;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.MatchResult;
import jellyfish.matcher.input.*;

public abstract class MatcherNode
        implements Comparable<MatcherNode>
{
    private static long idSource = 0;

    private long id = ++idSource;
    protected boolean acceptedEnding;
    protected MatcherNode parentNode;
    protected boolean nextConstNodesInitialized;
    protected Map<Integer, ConstantMatcherNode> nextConstNodes;
    protected boolean nextOtherNodesInitialized;
    protected Map<MatcherNode, MatcherNode> nextOtherNodes;
    protected AliasMatcherMap aliasMatcherMap;
    
    public MatcherNode() {
        this.parentNode = null;
        this.acceptedEnding = false;
        nextConstNodesInitialized = false;
        this.nextConstNodes = Collections.EMPTY_MAP;
        nextOtherNodesInitialized = false;
        this.nextOtherNodes = Collections.EMPTY_MAP;
        this.aliasMatcherMap = null;
    }
    
    protected void setParentNode( MatcherNode parentNode ) {
        this.parentNode = parentNode;
    }

    private synchronized void initNextConstNodes() {
        if ( nextConstNodesInitialized ) {
            return;
        }

        nextConstNodes = new TreeMap<Integer, ConstantMatcherNode>();

        nextConstNodesInitialized = true;
    }

    private synchronized void initNextOtherNodes() {
        if ( nextOtherNodesInitialized ) {
            return;
        }

        nextOtherNodes = new TreeMap<MatcherNode, MatcherNode>(new Comparator<MatcherNode>() {
            public int compare( MatcherNode o1, MatcherNode o2 ) {
                return o1.compareFunctionality( o2 );
            }
        });

        nextOtherNodesInitialized = true;
    }

    public final MatcherNode registerNextNode( MatcherNode node ) {
        if ( node instanceof ConstantMatcherNode ) {
            if ( !nextConstNodesInitialized ) {
                initNextConstNodes();
            }

            ConstantMatcherNode constNode = (ConstantMatcherNode) node;
            Integer id = constNode.getDictionaryEntry().getId();
            if ( nextConstNodes.containsKey( id ) ) {
                return nextConstNodes.get( id );
            } else {
                nextConstNodes.put( id, constNode );
                constNode.setParentNode(this);
                return constNode;
            }
        } else {
            if ( !nextOtherNodesInitialized ) {
                initNextOtherNodes();
            }

            MatcherNode existing = null;
            if ( nextOtherNodes.containsKey( node ) ) {
                existing = nextOtherNodes.get( node );
            }

            if ( existing != null ) {
                return existing;
            } else {
                nextOtherNodes.put( node, node );
                node.setParentNode( this );
                return node;
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
        ArrayList<MatcherNode> nodes = new ArrayList<MatcherNode>( nextOtherNodes.size() + nextConstNodes.
                size() );
        nodes.addAll( nextOtherNodes.values() );
        nodes.addAll( nextConstNodes.values() );
        return nodes;
    }

    public boolean isAcceptedEnding() {
        return acceptedEnding;
    }

    public final void setAcceptEnding( AliasMatcherMap aliasMatcherMap ) {
        if ( acceptedEnding ) {
            throw new RuntimeException( "The parser node '" + this +
                                        "' is already an ending node.\n" );
        }
        this.acceptedEnding = true;
        this.aliasMatcherMap = aliasMatcherMap;
    }

    public AliasMatcherMap getAliasMatcherMap() {
        return aliasMatcherMap;
    }
    
    protected abstract void fillWildCardEndResults(
            InputTokenList input, int inputIndex,
            MatchResult prevMatch,
            List<MatchResult> output );

    protected void match( InputTokenList input, int inputIndex,
                          MatchResult prevMatch,
                          List<MatchResult> output,
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
                if ( this.acceptedEnding && (!matchAtEnd || inputIndex == input.size() - 1) ) {
//                    DebugCommons.out("INCLUDE RESULT: "+selfResults);
                    output.addAll( selfResults );
                }
            } else {
                this.match( input, inputIndex + 1, prevMatch, output, matchAtEnd );
            }

            if ( nextConstNodes != null ) {
                for ( MatchResult selfResult : selfResults ) {
                    for ( MatcherNode node : nextConstNodes.values() ) {
                        node.match( input, inputIndex, selfResult, output, matchAtEnd );
                    }
                }
            }

            if ( nextOtherNodes != null ) {
                for ( MatchResult selfResult : selfResults ) {
                    for ( MatcherNode node : nextOtherNodes.values() ) {
                        node.match( input, inputIndex, selfResult, output, matchAtEnd );
                    }
                }
            }

        } else {
            //  if not, then the descendant match has had a successful match

//            DebugCommons.out("non-wild-card found");

            //      our work here is to first check if we're to add this as an output
            if ( this.acceptedEnding && (!matchAtEnd || inputIndex == input.size() - 1) ) {
//                System.out.println( "INCLUDE RESULT: "+prevMatch );
                output.add( prevMatch );
            }

            //      then disperse the next token (if any)...
            int nextInputIndex = inputIndex + 1;

            if ( nextInputIndex < input.size() ) {
                InputToken nextToken = input.get( nextInputIndex );

                if ( nextToken.isWildCard() ) {
                    if ( nextConstNodes != null ) {
                        for ( MatcherNode node : nextConstNodes.values() ) {
                            node.match( input, nextInputIndex, prevMatch, output, matchAtEnd );
                        }
                    }
                    if ( nextOtherNodes != null ) {
                        for ( MatcherNode node : nextOtherNodes.values() ) {
                            node.match( input, nextInputIndex, prevMatch, output, matchAtEnd );
                        }
                    }
                } else {
                    if ( nextToken.isInDictionary() && nextConstNodes != null ) {
                        MatcherNode node = nextConstNodes.get( nextToken.getDictionaryEntryId() );
                        if ( node != null ) {
                            node.match( input, nextInputIndex, prevMatch, output, matchAtEnd );
                        }
                    }
                    if ( nextOtherNodes != null ) {
                        for ( MatcherNode node : nextOtherNodes.values() ) {
                            node.match( input, nextInputIndex, prevMatch, output, matchAtEnd );
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return id+(this.acceptedEnding ? "$" : "");
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
        return this.hashCode()-o.hashCode();
    }

    protected abstract int compareFunctionality(MatcherNode matcherNode);

    protected void fillEndings( List<MatcherNode> endings ) {
        if ( this.acceptedEnding ) {
            endings.add( this );
        }

        for ( MatcherNode node : nextConstNodes.values() ) {
            node.fillEndings( endings );
        }
        for ( MatcherNode node : nextOtherNodes.values() ) {
            node.fillEndings( endings );
        }
    }

    protected void fullTreeToString( StringBuilder bldr ) {
        bldr.append( this.toString() );
        if ( (nextOtherNodes != null && !nextOtherNodes.isEmpty()) || (nextConstNodes != null && !nextConstNodes.
                                                                       isEmpty()) ) {
            bldr.append( "[" );
            boolean first = true;
            if ( nextConstNodes != null ) {
                for ( MatcherNode node : nextConstNodes.values() ) {
                    if ( first ) {
                        first = false;
                    } else {
                        bldr.append( "," );
                    }
                    node.fullTreeToString( bldr );
                }
            }
            if ( nextOtherNodes != null ) {
                for ( MatcherNode node : nextOtherNodes.values() ) {
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
