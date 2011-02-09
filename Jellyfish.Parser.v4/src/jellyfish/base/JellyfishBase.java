package jellyfish.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import jellyfish.common.CaseInsensitiveStringComparator;
import jellyfish.matcher.MatchResult;
import jellyfish.prolog.PrologReferenceEngine;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.triplestore.ReferenceEngine;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.model.Language;

/**
 *
 * @author Umran
 */
public class JellyfishBase
{

	private TripleStore tripleStore;
	private ReferenceEngine referenceEngine;
	private final Map<String, Tokenizer> tokenizers;
	private final Map<String, ClauseBase> clauseBases;

	public JellyfishBase( TripleStore tripleStore )
			throws ClassNotFoundException, InstantiationException, IllegalAccessException,
				   FileNotFoundException, Exception {
		this.tripleStore = tripleStore;
		this.referenceEngine = new PrologReferenceEngine( tripleStore );
		this.tokenizers = new TreeMap<String, Tokenizer>( new CaseInsensitiveStringComparator() );
		this.clauseBases = new TreeMap<String, ClauseBase>( new CaseInsensitiveStringComparator() );

		init();
	}

	private void init()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException,
				   FileNotFoundException, Exception {
		for ( Language language : tripleStore.getLanguages() ) {
			String tokenizerClassName = language.getTokenizerClass();
			Class<? extends Tokenizer> tokenizerClass = (Class<? extends Tokenizer>) Class.forName(
					tokenizerClassName );
			Tokenizer tokenizer = tokenizerClass.newInstance();
			this.tokenizers.put( language.getName(), tokenizer );
			/*
			String clauseFilePath = language.getClausesFile();
			FileInputStream clauseInput = new FileInputStream( clauseFilePath );

			ClauseBase clauseBase = new ClauseBase( language.getName(), tokenizer, tripleStore, referenceEngine );
			clauseBase.build( clauseInput );
			clauseBases.put( language.getName(), clauseBase );
			 */
		}
	}

	private ClauseBase createClauseBase( String languageName ) {
		try {
			Language language = tripleStore.getLanguage( languageName );
			Tokenizer tokenizer = tokenizers.get( languageName );

			String clauseFilePath = language.getClausesFile();
			FileInputStream clauseInput = new FileInputStream( clauseFilePath );

			ClauseBase clauseBase = new ClauseBase( language.getName(), tokenizer, tripleStore, referenceEngine );
			clauseBase.build( clauseInput );

			return clauseBase;
		} catch ( Exception ex ) {
			throw new RuntimeException( "Error while creating clause-base for language '" +
										languageName + "'", ex );
		}
	}
	
	public Set<String> getLanguages() {
		return tokenizers.keySet();
	}

	public TripleStore getTripleStore() {
		return tripleStore;
	}

	public ClauseBase getClauseBase( String language ) {
		//  there has to be a tokenizer..
		if ( !tokenizers.containsKey( language ) ) {
			return null;
		}

		//  creation of clause base can be delayed
		if ( !clauseBases.containsKey( language ) ) {
			synchronized ( clauseBases ){
				if ( !clauseBases.containsKey( language ) ) {
					clauseBases.put( language, createClauseBase( language ) );
				}
			}
		}

		return clauseBases.get( language );
	}

	public ReferenceEngine getReferenceEngine() {
		return referenceEngine;
	}

	public void reloadClauseBase( String language ) {
		synchronized ( clauseBases ){
			clauseBases.put( language, createClauseBase( language ) );
		}
	}

	public Tokenizer getTokenizer( String language ) {
		return tokenizers.get( language );
	}

	public List<MatchResult> match( String language, String input ) {
		ClauseBase clauseBase = clauseBases.get( language );
		if ( clauseBase == null ) {
			return Collections.EMPTY_LIST;
		}
		return clauseBase.match( input );
	}
}
