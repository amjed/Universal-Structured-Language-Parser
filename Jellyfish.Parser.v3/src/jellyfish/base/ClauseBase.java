/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.base;

import jellyfish.matcher.nodes.RootMatcherNode;
import jellyfish.matcher.nodes.MatcherNode;
import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;
import jellyfish.matcher.*;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.parser.clauses.AndClause;
import jellyfish.matcher.parser.clauses.NodeAliasList;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.matcher.input.InputToken;
import jellyfish.matcher.input.InputTokenList;
import jellyfish.matcher.nodes.ConstantMatcherNode;
import jellyfish.matcher.parser.xml.XmlClauseParser;
import jellyfish.tokenizer.Tokenizer;

/**
 *
 * @author Xevia
 */
public class ClauseBase
{

    private Tokenizer tokenizer;
    private TokenDictionary dictionary;
    private RootMatcherNode matcherRoot;
    private List<AliasTreeNode> rootAliases;

    public ClauseBase( Tokenizer tokenizer ) {
        this.tokenizer = tokenizer;
        this.matcherRoot = new RootMatcherNode();
        this.rootAliases = new ArrayList<AliasTreeNode>();
    }

    public List<AliasTreeNode> getRootAliases()
    {
        return rootAliases;
    }

    public RootMatcherNode getMatcherRoot()
    {
        return matcherRoot;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }
    
    
    public void build( InputStream input ) throws Exception {
        XmlClauseParser clauseParser = new XmlClauseParser(tokenizer);

        long startTime = System.currentTimeMillis();
        
        clauseParser.parse(input);

        this.dictionary = clauseParser.getLatestDictionary();

        NodeAliasList starts = new NodeAliasList(10);
        NodeAliasList ends = new NodeAliasList(10);

        int endingCounter = 0;

        List<AndClause> primaryClauses = clauseParser.getPrimaryClauses();
        for ( AndClause clause : primaryClauses ) {
            AliasTreeNode rootAlias = AliasTreeNode.createRoot(clause.getName());
            
            System.out.println( "Building Clause: "+clause.getName() );
            System.out.println( "========================" );
            clause.printClauseTree( System.out, 0 );

            starts.clear();
            ends.clear();

            starts.add(matcherRoot, AliasMatcherMap.createMap( rootAlias, matcherRoot ));
            clause.buildMatchTree(rootAlias, starts, ends, true);
            rootAliases.add(rootAlias);
            
            DebugClauses.printAliasTree( rootAlias );
        }

        DebugClauses.printMatcherTree( matcherRoot );
        
        System.out.println( "Tree Build Taken "+(System.currentTimeMillis()-startTime)+" ms" );
        System.out.println( "Endings Found: "+endingCounter );
        System.out.println( "AliasMatcherMaps created: "+AliasMatcherMap.aliasMatcherMapCounter );
        System.out.println( "ConstantMatcherNodes created: "+ConstantMatcherNode.constantMatcherNodeCounter );
    }

    public List<MatchResult> match( String inputLine ) {
        InputTokenList inputTokenList = new InputTokenList();
        List<MatchResult> results = new ArrayList<MatchResult>();

        InputToken.parseInputLine(inputTokenList, dictionary, tokenizer, inputLine);
        matcherRoot.match(inputTokenList, results);

        return results;
    }

    @Override
    public String toString()
    {
        return matcherRoot.fullTreeToString();
    }


}
