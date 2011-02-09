package jellyfish.matcher.xml.parsers;

import jellyfish.base.ClauseBase;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;

public class RepeatableNodeParser extends
        jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    private ClauseBase clauseBase;

    public RepeatableNodeParser( ClauseBase clauseBase ) {
	this.clauseBase = clauseBase;
    }

    
    @Override
    public AndClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
        Element element = (Element)nodeInfo.getNode();
	
        String alias = getAttributeOrDefault( nodeInfo, "alias", "" );
        int max = getAttributeOrDefault( nodeInfo, "max", 4 );

        AndClause clause = new AndClause( nodeInfo.getLocation(), null, clauseBase );
        if (alias.isEmpty()) {
            nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createSysInc(), false, max );
        } else {
            nodeInfo.getParentObject().addSubClause( clause, new ClauseAlias( alias ), false, max );
        }
        return clause;
    }

}
