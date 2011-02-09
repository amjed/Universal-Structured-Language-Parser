package jellyfish.base;

import jellyfish.matcher.nodes.RootMatcherNode;
import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;
import jellyfish.matcher.*;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.NodeAliasList;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.matcher.input.InputToken;
import jellyfish.matcher.input.InputTokenList;
import jellyfish.matcher.nodes.ConstantMatcherNode;
import jellyfish.matcher.xml.XmlClauseParser;
import jellyfish.triplestore.ReferenceEngine;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.triplestore.TripleStore;

/**
 *
 * @author Umran
 */
public class ClauseBase
{

	private String language;
	private Tokenizer tokenizer;
	private TokenDictionary dictionary;
	private RootMatcherNode matcherRoot;
	private List<AliasTreeNode> rootAliases;
	private TripleStore tripleStore;
	private ReferenceEngine referenceEngine;

	public ClauseBase( String language, Tokenizer tokenizer, TripleStore tripleStore,
					   ReferenceEngine referenceEngine ) {
		this.language = language;
		this.tokenizer = tokenizer;
		this.tripleStore = tripleStore;
		this.referenceEngine = referenceEngine;
		this.matcherRoot = new RootMatcherNode();
		this.rootAliases = new ArrayList<AliasTreeNode>();
	}

	public List<AliasTreeNode> getRootAliases() {
		return rootAliases;
	}

	public RootMatcherNode getMatcherRoot() {
		return matcherRoot;
	}

	public String getLanguage() {
		return language;
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public TokenDictionary getDictionary() {
		return dictionary;
	}

	public TripleStore getTripleStore() {
		return tripleStore;
	}

	public ReferenceEngine getReferenceEngine() {
		return referenceEngine;
	}

	public void build( InputStream input )
			throws Exception {
		XmlClauseParser clauseParser = new XmlClauseParser( this );

		long startTime = System.currentTimeMillis();

		clauseParser.parse( input );

		this.dictionary = clauseParser.getLatestDictionary();

		NodeAliasList starts = new NodeAliasList( 10 );
		NodeAliasList ends = new NodeAliasList( 10 );

		int endingCounter = 0;

		List<AndClause> primaryClauses = clauseParser.getPrimaryClauses();
		for ( AndClause clause : primaryClauses ) {
			AliasTreeNode rootAlias = AliasTreeNode.createRoot( clause.getName() );

			System.out.println( "Building Clause: " + clause.getName() );
			System.out.println( "========================" );
			clause.printClauseTree( System.out, 0 );

			starts.clear();
			ends.clear();

			starts.add( matcherRoot );
			clause.buildMatchTree( rootAlias, null, starts, ends, true );
			rootAliases.add( rootAlias );

			DebugClauses.printAliasTree( rootAlias );
		}

		DebugClauses.printMatcherTree( matcherRoot );

		System.out.println( "Tree Build Taken " + (System.currentTimeMillis() - startTime) + " ms" );
		System.out.println( "Endings Found: " + endingCounter );
		System.out.println( "ConstantMatcherNodes created: " +
							ConstantMatcherNode.constantMatcherNodeCounter );
	}

	public List<MatchResult> match( String inputLine ) {
		InputTokenList inputTokenList = new InputTokenList();
		List<MatchResult> results = new ArrayList<MatchResult>()
		{

			@Override
			public boolean add( MatchResult e ) {
				assert (e != null);
				return super.add( e );
			}
		};

		InputToken.parseInputLine( inputTokenList, dictionary, tokenizer, inputLine );
		matcherRoot.match( inputTokenList, results );

		return results;
	}

	@Override
	public String toString() {
		return matcherRoot.fullTreeToString();
	}
}
