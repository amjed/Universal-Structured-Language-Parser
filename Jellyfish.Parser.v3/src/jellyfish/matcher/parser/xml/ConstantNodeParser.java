package jellyfish.matcher.parser.xml;

import java.util.ArrayList;
import org.w3c.dom.Node;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.parser.clauses.Clause;
import jellyfish.matcher.parser.clauses.CompositeClause;
import jellyfish.matcher.parser.clauses.AndClause;
import jellyfish.matcher.parser.clauses.ClauseAlias;
import jellyfish.matcher.parser.clauses.ConstantClause;
import jellyfish.matcher.dictionary.TokenDictionary;
import jellyfish.tokenizer.Tokenizer;

class ConstantNodeParser extends
        jellyfish.xml.XmlNodeParser<Clause, CompositeClause> {

    private Tokenizer tokenizer;
    private TokenDictionary dictionary;

    public ConstantNodeParser( Tokenizer tokenizer ) {
        this.tokenizer = tokenizer;
    }

    public TokenDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary( TokenDictionary dictionary ) {
        this.dictionary = dictionary;
    }
    
    public Clause parse( String location, Node node, Node parentNode,
                                 CompositeClause parentObject ) {
        if (dictionary == null) {
            throw new RuntimeException( "No dictionary set for constant node parser" );
        }
        if (parentObject == null) {
            throw new RuntimeException( "Token defined with no parent." );
        }
        String value = node.getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            return null;
        }
        ArrayList<String> tokens = new ArrayList<String>();
        tokenizer.tokenize( tokens, value );
        if (tokens.isEmpty()) {
            return null;
        } else {
            if (tokens.size() == 1) {
                String token = tokens.get( 0 );
                ConstantClause clause = new ConstantClause( location, null,
                                                                      dictionary,
                                                                      token );
                parentObject.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
                return clause;
            } else {
                AndClause group = new AndClause( location, null );
                for (String token : tokens) {
                    ConstantClause clause = new ConstantClause( location, null,
                                                                          dictionary,
                                                                          token );
                    group.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
                }
                parentObject.addSubClause( group, ClauseAlias.createSysInc(), false, 1 );
                return group;
            }
        }
    }
}
