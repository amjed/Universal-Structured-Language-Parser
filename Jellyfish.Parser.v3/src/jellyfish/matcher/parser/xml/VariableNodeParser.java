package jellyfish.matcher.parser.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.parser.clauses.ClauseAlias;
import jellyfish.matcher.parser.clauses.CompositeClause;
import jellyfish.matcher.parser.clauses.VariableClause;

class VariableNodeParser extends
        jellyfish.xml.XmlNodeParser<VariableClause, CompositeClause> {

    public VariableNodeParser() {
    }

    public VariableClause parse( String location, Node node, Node parentNode,
                                      CompositeClause parentObject ) {
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Switch tag defined with no parent." );
        }
        String alias = element.getAttribute( "alias" );
        if (alias==null || alias.isEmpty()) {
            throw new RuntimeException( "Variable declared with no alias." );
        }
        VariableClause clause = new VariableClause( location, alias );
        parentObject.addSubClause( clause, ClauseAlias.createSysInc(), false, 1 );
        return clause;
    }
}
