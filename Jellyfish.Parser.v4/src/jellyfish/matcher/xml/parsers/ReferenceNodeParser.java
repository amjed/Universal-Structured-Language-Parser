package jellyfish.matcher.xml.parsers;

import jellyfish.matcher.AliasTreeNode;
import java.util.*;
import jellyfish.matcher.VariableContext;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;
import jellyfish.matcher.clauses.*;

/**
 * 
 * @author Umran
 */
public class ReferenceNodeParser
		extends jellyfish.xml.XmlNodeParser<AndClause, CompositeClause>
{

	private class UnprocessedReference
	{

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

		for ( UnprocessedReference ref : unprocessedReferences ) {
			if ( !namedClauses.containsKey( ref.referencedClause ) ) {
				throw new RuntimeException(
						"Referenced clause '" + ref.referencedClause + "' is undefined." );
			}
			AndClause referedClause = namedClauses.get( ref.referencedClause );

			if ( referedClause.containsClause( ref.parentObject ) ) {
				throw new RuntimeException(
						"Cyclic reference detected: " + ref.spaceHolder.getXmlLocation() +
						" refers to " + ref.referencedClause );
			}

			if ( ref.inline ) {
				ref.parentObject.replaceClause( ref.spaceHolder, referedClause );
			} else {
				ReferenceClause referenceClause = new ReferenceClause(
						ref.spaceHolder.getXmlLocation(), null, referedClause );

				ref.parentObject.replaceClause( ref.spaceHolder, referenceClause );
			}
		}

		unprocessedReferences.clear();
	}

	private static class SpaceHolderToken
			extends SimpleClause
	{

		public SpaceHolderToken( String xmlLocation ) {
			super( xmlLocation, null );
		}

		@Override
		public SimpleClauseType getSimpleClauseType() {
			return SimpleClauseType.CONSTANT_CLAUSE;
		}

		@Override
		protected void buildMatchTree( AliasTreeNode parentAlias,
									   VariableContext variableContext,
									   NodeAliasList prevNodes,
									   NodeAliasList ends,
									   boolean last ) {
			ends.addAll( prevNodes );
		}
	}

	@Override
	public AndClause parse( XmlNodeInfo<CompositeClause> nodeInfo ) {

		Element element = (Element) nodeInfo.getNode();

		String value = element.getTextContent();
		if ( value != null ) {
			value = value.trim();
		}
		if ( value == null || value.isEmpty() ) {
			throw new RuntimeException(
					"Reference clause defined with no clause name." );
			//            System.out.println("Reference: "+value);
		}

		String alias = getAttributeOrDefault( nodeInfo, "alias", "" );

		boolean inline = getAttributeOrDefault( nodeInfo, "inline", Boolean.FALSE );

		Clause spaceHolder = new SpaceHolderToken( nodeInfo.getLocation() );
		if ( alias.isEmpty() ) {
			nodeInfo.getParentObject().addSubClause( spaceHolder,
													 ClauseAlias.createSysInc(),
													 false,
													 1 );
		} else {
			nodeInfo.getParentObject().addSubClause( spaceHolder,
													 new ClauseAlias( alias ),
													 false,
													 1 );
		}

		unprocessedReferences.add( new UnprocessedReference( nodeInfo.getParentObject(),
															 value,
															 spaceHolder,
															 inline ) );

		return null;
	}
}
