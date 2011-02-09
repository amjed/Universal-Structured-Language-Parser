/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.triplestore.xml;

import jellyfish.triplestore.xml.parsers.EntityCollectionParser;
import jellyfish.triplestore.xml.parsers.EntityNameCollectionParser;
import jellyfish.triplestore.xml.parsers.EntityNameParser;
import jellyfish.triplestore.xml.parsers.EntityParser;
import jellyfish.triplestore.xml.parsers.LanguageCollectionParser;
import jellyfish.triplestore.xml.parsers.LanguageParser;
import jellyfish.triplestore.xml.parsers.ObjectParser;
import jellyfish.triplestore.xml.parsers.PredicateParser;
import jellyfish.triplestore.xml.parsers.RelationshipCollectionParser;
import jellyfish.triplestore.xml.parsers.RelationshipParser;
import jellyfish.triplestore.xml.parsers.RootParser;
import jellyfish.triplestore.xml.parsers.SubjectParser;
import jellyfish.triplestore.xml.parsers.TripleCollectionParser;
import jellyfish.triplestore.xml.parsers.TripleParser;
import jellyfish.triplestore.xml.parsers.ValueParser;
import jellyfish.xml.XmlCommon;
import jellyfish.xml.XmlNodeType;
import org.w3c.dom.Node;

/**
 *
 * @author Xevia
 */
class XmlTripleStoreParser extends jellyfish.xml.XmlParser
{

	private XmlCommon common = new XmlCommon();
	private XmlTripleStore tripleStore;

	public XmlTripleStoreParser(XmlTripleStore tripleStore) {
		super( 15 );

		this.tripleStore = tripleStore;

		XmlNodeType rootType = new XmlNodeType( common, Node.ELEMENT_NODE, "root" );
		XmlNodeType languageCollectionType = new XmlNodeType( common, Node.ELEMENT_NODE, "languages" );
		XmlNodeType languageType = new XmlNodeType( common, Node.ELEMENT_NODE, "language" );
		XmlNodeType relationshipCollectionType = new XmlNodeType( common, Node.ELEMENT_NODE,
																  "relationships" );
		XmlNodeType relationshipType = new XmlNodeType( common, Node.ELEMENT_NODE, "relationship" );
		XmlNodeType entityCollectionType = new XmlNodeType( common, Node.ELEMENT_NODE, "entities" );
		XmlNodeType entityType = new XmlNodeType( common, Node.ELEMENT_NODE, "entity" );
		XmlNodeType entityNameCollectionType = new XmlNodeType( common, Node.ELEMENT_NODE, "names" );
		XmlNodeType entityNameType = new XmlNodeType( common, Node.ELEMENT_NODE, "name" );
		XmlNodeType tripleCollectionType = new XmlNodeType( common, Node.ELEMENT_NODE, "triples" );
		XmlNodeType tripleType = new XmlNodeType( common, Node.ELEMENT_NODE, "triple" );
		XmlNodeType subjectType = new XmlNodeType( common, Node.ELEMENT_NODE, "sub" );
		XmlNodeType predicateType = new XmlNodeType( common, Node.ELEMENT_NODE, "pred" );
		XmlNodeType objectType = new XmlNodeType( common, Node.ELEMENT_NODE, "obj" );
		XmlNodeType valueType = new XmlNodeType( common, Node.ELEMENT_NODE, "val" );

		this.addParser( rootType, new RootParser( tripleStore ) );
		this.addParser( languageCollectionType, new LanguageCollectionParser() );
		this.addParser( languageType, new LanguageParser() );
		this.addParser( relationshipCollectionType, new RelationshipCollectionParser() );
		this.addParser( relationshipType, new RelationshipParser() );
		this.addParser( entityCollectionType, new EntityCollectionParser() );
		this.addParser( entityType, new EntityParser() );
		this.addParser( entityNameCollectionType, new EntityNameCollectionParser( tripleStore ) );
		this.addParser( entityNameType, new EntityNameParser( tripleStore ) );
		this.addParser( tripleCollectionType, new TripleCollectionParser() );
		this.addParser( tripleType, new TripleParser() );
		this.addParser( subjectType, new SubjectParser( tripleStore ) );
		this.addParser( predicateType, new PredicateParser( tripleStore ) );
		this.addParser( objectType, new ObjectParser( tripleStore ) );
		this.addParser( valueType, new ValueParser() );

		this.setPossibleChild( rootType, languageCollectionType );
		this.setPossibleChild( rootType, relationshipCollectionType );
		this.setPossibleChild( rootType, entityCollectionType );
		this.setPossibleChild( rootType, tripleCollectionType );

		this.setPossibleChild( languageCollectionType, languageType );
		this.setPossibleChild( relationshipCollectionType, relationshipType );
		this.setPossibleChild( entityCollectionType, entityType );
		this.setPossibleChild( entityType, entityNameCollectionType );
		this.setPossibleChild( entityNameCollectionType, entityNameType );
		this.setPossibleChild( tripleCollectionType, tripleType );

		this.setPossibleChild( tripleType, subjectType );
		this.setPossibleChild( tripleType, predicateType );
		this.setPossibleChild( tripleType, objectType );
		this.setPossibleChild( tripleType, valueType );
	}

	public XmlTripleStore getTripleStore() {
		return tripleStore;
	}
}
