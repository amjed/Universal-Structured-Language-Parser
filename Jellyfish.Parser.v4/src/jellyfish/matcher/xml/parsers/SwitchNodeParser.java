package jellyfish.matcher.xml.parsers;

import jellyfish.xml.XmlNodeInfo;
import jellyfish.matcher.clauses.ClauseAlias;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.OrClause;

public class SwitchNodeParser extends jellyfish.xml.XmlNodeParser<OrClause, CompositeClause> {

    public SwitchNodeParser() {
    }

    @Override
    public OrClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {

	String alias = getAttributeOrDefault( nodeInfo, "alias", "" );
	
        OrClause clause = new OrClause( nodeInfo.getLocation(), null );
        if (alias.isEmpty()) {
            nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
        } else {
            nodeInfo.getParentObject().addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
        return clause;
    }

}
