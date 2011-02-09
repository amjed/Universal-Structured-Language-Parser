package jellyfish.matcher.xml;

import jellyfish.matcher.AliasTreeNode;
import java.util.*;
import jellyfish.matcher.nodes.MatcherNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jellyfish.matcher.clauses.*;

class ReferenceNodeParser extends
        jellyfish.xml.XmlNodeParser<AndClause, CompositeClause> {

    private class UnprocessedReference {

        public CompositeClause parentObject;
        public String referencedClause;
        public Clause spaceHolder;
        public boolean inline;

        public UnprocessedReference( CompositeClause parentObject, String referencedClause,
                                     Clause spaceHolder, boolean inline ) {
            this.parentObject = parentObject;
            this.referencedClause = referencedClause;
            this.spaceHolder = spaceHolder;
            this.inline = inline;
        }
        
    }
    private Map<String, AndClause> namedClauses;
    private List<UnprocessedReference> unprocessedReferences;

    public ReferenceNodeParser( Map<String, AndClause> namedClauses ) {
        this.namedClauses = namedClauses;
        this.unprocessedReferences = new ArrayList<UnprocessedReference>( 100 );
    }

    public void processReferences() {
        
        for (UnprocessedReference ref : unprocessedReferences) {
            if (!namedClauses.containsKey( ref.referencedClause )) {
                throw new RuntimeException(
                        "Referenced clause '" + ref.referencedClause + "' is undefined." );
            }
            AndClause refClause = namedClauses.get( ref.referencedClause );

            if (ref.inline) {
                ref.parentObject.replaceClause( ref.spaceHolder, refClause );
            } else {
                VariableClause variableClause = new VariableClause( ref.spaceHolder.getXmlLocation(), null );
                variableClause.setVariableContentTree( refClause );

                ref.parentObject.replaceClause( ref.spaceHolder, variableClause );
            }
        }

        unprocessedReferences.clear();
    }

    private static class SpaceHolderToken extends SimpleClause {

        public SpaceHolderToken( String xmlLocation )
        {
            super(xmlLocation, null);
        }
        
        @Override
        public SimpleClauseType getSimpleClauseType() {
            return SimpleClauseType.CONSTANT_CLAUSE;
        }

        @Override
        protected void buildMatchTree( AliasTreeNode parentAlias,
                                       NodeAliasList prevNodes,
                                       NodeAliasList ends ) {
            ends.addAll( prevNodes );
        }
        
    }

    public AndClause parse( String location, Node node, Node parentNode,
                            CompositeClause parentObject ) {
        
        Element element = (Element)node;
        if (parentObject == null) {
            throw new RuntimeException( "Reference defined with no parent." );
        }
        String value = element.getTextContent();
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.isEmpty()) {
            throw new RuntimeException(
                    "Reference clause defined with no clause name." );
            //            System.out.println("Reference: "+value);
        }

        String alias = element.getAttribute( "alias" );

        String inlineAttr = element.getAttribute( "inline" );
        boolean inline = true;
        if (inlineAttr!=null && !inlineAttr.isEmpty())
            inline = !("false".equalsIgnoreCase( inlineAttr ));

        Clause spaceHolder = new SpaceHolderToken(location);
        if (alias==null || alias.isEmpty()) {
            parentObject.addSubClause( spaceHolder,
                                       ClauseAlias.createSysInc(),
                                       false,
                                       1 );
        } else {
            parentObject.addSubClause( spaceHolder,
                                       new ClauseAlias( alias ),
                                       false,
                                       1 );
        }

        unprocessedReferences.add( new UnprocessedReference( parentObject,
                                                             value,
                                                             spaceHolder,
                                                             inline ) );
        
        return null;
    }
}
