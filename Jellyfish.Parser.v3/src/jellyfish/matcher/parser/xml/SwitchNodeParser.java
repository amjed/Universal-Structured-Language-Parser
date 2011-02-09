package jellyfish.matcher.parser.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.parser.clauses.ClauseAlias;
import jellyfish.matcher.parser.clauses.CompositeClause;
import jellyfish.matcher.parser.clauses.OrClause;

class SwitchNodeParser extends jellyfish.xml.XmlNodeParser<OrClause, CompositeClause> {

    public SwitchNodeParser() {
    }

    public OrClause parse( String location, Node node, Node parentNode,
                           CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Switch tag defined with no parent." );
        }
        String alias = element.getAttribute( "alias" );
        OrClause clause = new OrClause( location, null );
        if (alias==null || alias.isEmpty()) {
            parentObject.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
        } else {
            parentObject.addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
        return clause;
    }
}
