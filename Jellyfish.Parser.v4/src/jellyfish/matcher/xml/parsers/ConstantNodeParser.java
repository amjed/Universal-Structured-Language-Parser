package jellyfish.matcher.xml.parsers;

import java.util.ArrayList;
import jellyfish.base.ClauseBase;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Node;
import jellyfish.matcher.clauses.Clause;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;
import jellyfish.matcher.clauses.ConstantClause;
import jellyfish.matcher.dictionary.TokenDictionary;

public class ConstantNodeParser extends
        jellyfish.xml.XmlNodeParser<Clause, CompositeClause> {

    private ClauseBase clauseBase;
    private TokenDictionary dictionary;

    public ConstantNodeParser( ClauseBase clauseBase ) {
        this.clauseBase = clauseBase;
    }

    public TokenDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary( TokenDictionary dictionary ) {
        this.dictionary = dictionary;
    }
    
    @Override
    public Clause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
	assert(dictionary != null);
	
        String value = nodeInfo.getNode().getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            return null;
        }

        ArrayList<String> tokens = new ArrayList<String>();
        clauseBase.getTokenizer().tokenize( tokens, value );
        if (tokens.isEmpty()) {
            return null;
        } else {
            if (tokens.size() == 1) {
                String token = tokens.get( 0 );
                ConstantClause clause = new ConstantClause( nodeInfo.getLocation(),
							    null,
							    dictionary,
							    token );
                nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
                return clause;
            } else {
                AndClause group = new AndClause( nodeInfo.getLocation(), null, clauseBase );
                for (String token : tokens) {
                    ConstantClause clause = new ConstantClause( nodeInfo.getLocation(), null,
								dictionary,
								token );
                    group.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
                }
                nodeInfo.getParentObject().addSubClause( group, ClauseAlias.createSysInc(), false, 1 );
                return group;
            }
        }
    }
}
