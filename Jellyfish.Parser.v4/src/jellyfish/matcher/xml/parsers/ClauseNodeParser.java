package jellyfish.matcher.xml.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import jellyfish.base.ClauseBase;
import jellyfish.xml.XmlNodeInfo;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;

public class ClauseNodeParser extends jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    private static final Set<String> REQUIRED_ATTRIBUTES = new HashSet<String>(
	    Arrays.asList( "name" ));

    private ClauseBase clauseBase;
    private Map<String, AndClause> namedClauses;

    public ClauseNodeParser( ClauseBase clauseBase,
			     Map<String, AndClause> namedClauses ) {
	this.clauseBase = clauseBase;
	this.namedClauses = namedClauses;
    }

    @Override
    public Set<String> getRequiredAttributes() {
	return REQUIRED_ATTRIBUTES;
    }

    @Override
    public boolean requiresParentObject() {
	return false;
    }
    
    @Override
    public AndClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
	
	String name = getAttributeOrDefault( nodeInfo, "name", "" );
        name = name.toLowerCase();
        if (namedClauses.containsKey( name )) {
            throw new RuntimeException( "Clause with the name '" + name + "' already defined." );
        }

        String onMatch = getAttributeOrDefault( nodeInfo, "onMatch", "" );
	onMatch = onMatch.toLowerCase();

        boolean primary = getAttributeOrDefault( nodeInfo, "primary", Boolean.FALSE );

        AndClause clause = new AndClause( nodeInfo.getLocation(), name, primary, clauseBase );
        if (onMatch != null && !onMatch.isEmpty()) {
            clause.setOnMatchFunctionName( onMatch );
        }
        namedClauses.put( name, clause );
        return clause;
    }
}
