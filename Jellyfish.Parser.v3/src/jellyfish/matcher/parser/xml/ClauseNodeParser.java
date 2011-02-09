package jellyfish.matcher.parser.xml;

import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.parser.clauses.CompositeClause;
import jellyfish.matcher.parser.clauses.AndClause;

class ClauseNodeParser extends jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    private Map<String, AndClause> namedClauses;

    public ClauseNodeParser( Map<String, AndClause> namedClauses ) {
        this.namedClauses = namedClauses;
    }

    public AndClause parse( String location, Node node, Node parentNode,
                            CompositeClause parentObject ) {
        Element element = (Element)node;
        String name = element.getAttribute( "name" );
        if (name == null || name.isEmpty()) {
            throw new RuntimeException( "Found clause with no name." );
        }
        name = name.toLowerCase();
        if (namedClauses.containsKey( name )) {
            throw new RuntimeException( "Clause with the name '" + name + "' already defined." );
        }
        String onMatch = element.getAttribute( "onMatch" );
        if (onMatch != null) {
            onMatch = onMatch.toLowerCase();
        }
        String primary = element.getAttribute( "primary" );
        if (primary != null) {
            primary = primary.toLowerCase();
        }
        AndClause clause = new AndClause( location, name, primary.equals("true") );
        if (onMatch != null && !onMatch.isEmpty()) {
            clause.setOnMatchFunctionName( onMatch );
        }
        namedClauses.put( name, clause );
        return clause;
    }
}
