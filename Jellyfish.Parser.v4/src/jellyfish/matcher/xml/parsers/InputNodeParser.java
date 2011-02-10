package jellyfish.matcher.xml.parsers;

import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.clauses.ClauseAlias;
import jellyfish.matcher.clauses.CompositeClause;
import jellyfish.matcher.clauses.InputClause;

public class InputNodeParser extends jellyfish.xml.XmlNodeParser<InputClause, CompositeClause> {

    public InputNodeParser() {
    }

    @Override
    public InputClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {
        Element element = (Element)nodeInfo.getNode();

        String value = element.getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            throw new RuntimeException( "Empty input defined at location "+nodeInfo.getLocation() );
        }

        String alias = getAttributeOrDefault( nodeInfo, "alias", "" );
        InputClause clause = new InputClause(nodeInfo.getLocation(), null, value );
        if (alias.isEmpty()) {
            nodeInfo.getParentObject().addSubClause( clause, ClauseAlias.createEmpty(), false, 1 );
        } else {
            nodeInfo.getParentObject().addSubClause( clause, new ClauseAlias( alias ), false, 1 );
        }
	
        return clause;
    }
}
