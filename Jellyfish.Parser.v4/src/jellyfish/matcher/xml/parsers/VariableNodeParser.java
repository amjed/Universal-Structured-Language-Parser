package jellyfish.matcher.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jellyfish.xml.XmlNodeInfo;
import jellyfish.matcher.clauses.ClauseAlias;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.VariableClause;

public class VariableNodeParser extends jellyfish.xml.XmlNodeParser<VariableClause, CompositeClause> {

    private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
	    Arrays.asList( "alias" ));

    public VariableNodeParser() {
    }

    @Override
    public Set<String> getRequiredAttributes() {
	return REQUIRED_ATTRIBUTES;
    }

    @Override
    public VariableClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
	
        String alias = getAttributeOrDefault( nodeInfo, "alias", "" );
	VariableClause clause = new VariableClause( nodeInfo.getLocation(), alias );
        if (alias.isEmpty()) {
            nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createEmpty(), false, 1 );
        } else {
            nodeInfo.getParentObject().addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
	
        return clause;
    }
}
