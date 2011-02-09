package jellyfish.matcher.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;

class OptionalNodeParser extends
        jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    public OptionalNodeParser() {
    }

    public AndClause parse( String location, Node node, Node parentNode,
                            CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Optional tag defined with no parent." );
        }
        String alias = element.getAttribute( "alias" );
        AndClause clause = new AndClause( location, null );
        if (alias.isEmpty()) {
            parentObject.addSubClause( clause, ClauseAlias.createSysInc(), true, 1 );
        } else {
            parentObject.addSubClause( clause, new ClauseAlias( alias ), true, 1 );
        }
        return clause;
    }
}
