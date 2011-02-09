package jellyfish.matcher.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.clauses.ClauseAlias;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.InputClause;

class InputNodeParser extends jellyfish.xml.XmlNodeParser<InputClause, CompositeClause> {

    public InputNodeParser() {
    }

    public InputClause parse( String location, Node node, Node parentNode,
                                   CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Switch tag defined with no parent." );
        }
        String value = element.getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            throw new RuntimeException( "Empty input defined." );
        }
        String alias = element.getAttribute( "alias" );
        InputClause clause = new InputClause( location, null, value );
        if (alias.isEmpty()) {
            parentObject.addSubClause( clause, ClauseAlias.createEmpty(), false, 1 );
        } else {
            parentObject.addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
        return clause;
    }
}
