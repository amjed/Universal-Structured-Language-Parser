package jellyfish.matcher.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.AndClause;
import jellyfish.matcher.clauses.ClauseAlias;

class RepeatableNodeParser extends
        jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    public RepeatableNodeParser() {
    }

    public AndClause parse( String location, Node node, Node parentNode,
                            CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Repeatable tag defined with no parent." );
        }
        String alias = element.getAttribute( "alias" );
        String max = element.getAttribute( "max" );
        if (max == null || max.isEmpty()) {
            max = "4";
        }
        int maxInt = Integer.parseInt( max );
        AndClause clause = new AndClause( location, null );
        if (alias.isEmpty()) {
            parentObject.addSubClause( clause, ClauseAlias.createSysInc(), false, maxInt );
        } else {
            parentObject.addSubClause( clause, new ClauseAlias( alias ), false, maxInt );
        }
        return clause;
    }
}
