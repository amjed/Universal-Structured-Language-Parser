package jellyfish.matcher.xml.parsers;

import jellyfish.base.ClauseBase;
import jellyfish.xml.XmlNodeInfo;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;

public class GroupNodeParser extends jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    private ClauseBase clauseBase;

    public GroupNodeParser( ClauseBase clauseBase ) {
	this.clauseBase = clauseBase;
    }
    

    @Override
    public AndClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
	
	String alias = getAttributeOrDefault( nodeInfo, "alias", "" );
	
        AndClause clause = new AndClause( nodeInfo.getLocation(), null, clauseBase );
        if (alias.isEmpty()) {
            nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
        } else {
            nodeInfo.getParentObject().addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
	
        return clause;
    }
}
