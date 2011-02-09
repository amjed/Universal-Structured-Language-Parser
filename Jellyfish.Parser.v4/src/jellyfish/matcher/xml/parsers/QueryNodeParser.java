package jellyfish.matcher.xml.parsers;

import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;
import jellyfish.matcher.clauses.AndClause;

public class QueryNodeParser extends jellyfish.xml.XmlNodeParser<AndClause, AndClause> {

    public QueryNodeParser(  ) {
    }

    @Override
    public AndClause parse( XmlNodeInfo<AndClause> nodeInfo ) {
        Element element = (Element)nodeInfo.getNode();

	assert(nodeInfo.getParentObject() instanceof AndClause);

        String value = element.getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            throw new RuntimeException( "Empty query defined at location "+nodeInfo.getLocation() );
        }

	AndClause andClause = (AndClause)nodeInfo.getParentObject();
	andClause.setQuery( value );

        return andClause;
    }
}
