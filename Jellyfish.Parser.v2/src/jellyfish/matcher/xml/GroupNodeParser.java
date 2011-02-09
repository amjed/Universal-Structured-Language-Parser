package jellyfish.matcher.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;

class GroupNodeParser extends jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    public GroupNodeParser() {
    }

    public AndClause parse( String location, Node node, Node parentNode,
                            CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Group tag defined with no parent." );
        }
        String alias = element.getAttribute( "alias" );
        AndClause clause = new AndClause( location, null );
        if (alias.isEmpty()) {
            parentObject.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
        } else {
            parentObject.addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
        return clause;
    }
}
