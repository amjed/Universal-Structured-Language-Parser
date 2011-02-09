/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.triplestore.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import jellyfish.common.CaseInsensitiveStringComparator;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.model.BaseEntity;
import jellyfish.triplestore.model.Entity;
import jellyfish.triplestore.model.EntityName;
import jellyfish.triplestore.model.Language;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;

/**
 *
 * @author Xevia
 */
public class XmlTripleStore
		implements TripleStore
{
	private final java.util.concurrent.locks.ReentrantLock updateLock = new ReentrantLock( true );

	private File xmlFile;
	private XmlTripleStoreParser parser;
	private long lastParse;
	private Map<String, Language> languages;
	private Map<String, Relationship> relationships;
	private Map<String, Entity> entities;
	private Map<Language, Map<Entity, List<EntityName>>> entityNames;
	private List<Triple> triples;

	//	categorized triples... (these are lazily filled when necessary)
	private final Map<Relationship, List<Triple>> triplesByPredicate;
	private final Map<BaseEntity, List<Triple>> triplesBySubject;
	private final Map<BaseEntity, List<Triple>> triplesByObject;
	//	all triples been categorized?
	private boolean allTriplesCategorized;

	//	used to flag current state of triple-store as parsing xml,
	//		this avoids any function called from the xml parser
	//		calling ensureLatestData which would lead to parsing the xml again.
	private boolean currentlyUpdatingXml = false;

	public XmlTripleStore(File xmlFile) {
		this.xmlFile = xmlFile;
		this.parser = new XmlTripleStoreParser(this);
		this.lastParse = 0;
		this.languages = new TreeMap<String, Language>( new CaseInsensitiveStringComparator() );
		this.relationships = new TreeMap<String, Relationship>( new CaseInsensitiveStringComparator() );
		this.entities = new TreeMap<String, Entity>( new CaseInsensitiveStringComparator() );
		this.entityNames = new IdentityHashMap<Language, Map<Entity, List<EntityName>>>();
		this.triples = new ArrayList<Triple>();
		this.triplesByPredicate = new IdentityHashMap<Relationship, List<Triple>>();
		this.triplesBySubject = new IdentityHashMap<BaseEntity, List<Triple>>();
		this.triplesByObject = new IdentityHashMap<BaseEntity, List<Triple>>();
		this.allTriplesCategorized = true;
	}

	private void dumpAll()
	{
		languages.clear();
		relationships.clear();
		entities.clear();
		entityNames.clear();
		triples.clear();
		triplesByPredicate.clear();
		triplesBySubject.clear();
		triplesByObject.clear();
		allTriplesCategorized = true;
	}

	private synchronized void parseXmlFile()
	{
		System.out.println( "Parsing "+xmlFile );
		System.out.println( "=================" );
		currentlyUpdatingXml = true;
		InputStream fin = null;
		try {
			fin = new BufferedInputStream(new FileInputStream( xmlFile ));
			dumpAll();
			parser.parse( fin );
			//	categorized triples need re-filling
			allTriplesCategorized = false;
		} catch (Exception e) {
			throw new RuntimeException("Exception while parsing xml triple store: "+xmlFile, e);
		} finally {
			if ( fin != null ) {
				try {
					fin.close();
				} catch ( IOException ex ) {
				}
			}
			currentlyUpdatingXml = false;
		}
	}

	private void categorizeTriple( Triple triple ) {
		{
			List<Triple> list = null;
			synchronized (triplesByPredicate) {
				if ( triplesByPredicate.containsKey( triple.getPredicate() ) ) {
					list = triplesByPredicate.get( triple.getPredicate() );
				} else {
					list = new ArrayList<Triple>();
					triplesByPredicate.put( triple.getPredicate(), list );
				}
			}
			synchronized (list) {
				list.add( triple );
			}
		}
		{
			List<Triple> list = null;
			synchronized (triplesBySubject) {
				if ( triplesBySubject.containsKey( triple.getSubject() ) ) {
					list = triplesBySubject.get( triple.getSubject() );
				} else {
					list = new ArrayList<Triple>();
					triplesBySubject.put( triple.getSubject(), list );
				}
			}
			synchronized (list) {
				list.add( triple );
			}
		}
		{
			List<Triple> list = null;
			synchronized (triplesByObject) {
				if ( triplesByObject.containsKey( triple.getObject() ) ) {
					list = triplesByObject.get( triple.getObject() );
				} else {
					list = new ArrayList<Triple>();
					triplesByObject.put( triple.getObject(), list );
				}
			}
			synchronized (list) {
				list.add( triple );
			}
		}
	}

	private synchronized void categorizeAllTriples() {
		if ( allTriplesCategorized ) {
			return;
		}

		//	categorize all triples
		for ( Triple triple : XmlTripleStore.this.triples ) {
			categorizeTriple( triple );
		}

		allTriplesCategorized = true;
	}

	private void ensureLatestData()
	{
		//	avoid parsing while the xml is already being parsed.
		if (currentlyUpdatingXml) return;
		if (updateLock.tryLock()) {
			try {
				if (xmlFile.lastModified()>lastParse) {
					synchronized (this) {
						if (xmlFile.lastModified()>lastParse) {
							parseXmlFile();
							//	new data has been added...
							lastParse = xmlFile.lastModified();
						}
					}
				}
			} finally {
				updateLock.unlock();
			}
		}
	}
	
	public void addLanguage( Language language ) {
		if ( languages.containsKey( language.getName() ) ) {
			throw new RuntimeException( "The language '" + language.getName() + "' already existss." );
		}
		languages.put( language.getName(), language );
	}

	public void addRelationship( Relationship relationship ) {
		if ( languages.containsKey( relationship.getName() ) ) {
			throw new RuntimeException( "The relationship '" + relationship.getName() +
										"' already existss." );
		}
		relationships.put( relationship.getName(), relationship );
	}

	public void addEntity( Entity entity ) {
		if ( entities.containsKey( entity.getName() ) ) {
			throw new RuntimeException( "The entity '" + entity.getName() + "' already existss." );
		}
		entities.put( entity.getName(), entity );
	}

	public void addTriple( Triple triple ) {
		triples.add( triple );
		
		if (allTriplesCategorized)
			categorizeTriple( triple );
	}

	public Collection<Language> getLanguages() {
		ensureLatestData();
		return Collections.unmodifiableCollection( languages.values() );
	}

	public Collection<Relationship> getRelationships() {
		ensureLatestData();
		return Collections.unmodifiableCollection( relationships.values() );
	}

	public Collection<Entity> getEntities() {
		ensureLatestData();
		return Collections.unmodifiableCollection( entities.values() );
	}

	public Collection<Triple> getTriples() {
		ensureLatestData();
		return triples;
	}

	public Language getLanguage( String name ) {
		ensureLatestData();
		return languages.get( name );
	}

	public Relationship getRelationship( String name ) {
		ensureLatestData();
		return relationships.get( name );
	}

	public Entity getEntity( String name ) {
		ensureLatestData();
		return entities.get( name );
	}

	public synchronized void addEntityName( EntityName entityName ) {
		
		Entity entity = entityName.getEntity();
		Language language = entityName.getLanguage();

		Map<Entity, List<EntityName>> map = null;
		if ( entityNames.containsKey( language ) ) {
			map = entityNames.get( language );
		} else {
			map = new IdentityHashMap<Entity, List<EntityName>>();
			entityNames.put( language, map );
		}

		List<EntityName> list = null;
		if ( map.containsKey( entity ) ) {
			list = map.get( entity );
		} else {
			list = new ArrayList<EntityName>();
			map.put( entity, list );
		}

		list.add( entityName );
	}

	public List<EntityName> getEntityNames( Entity entity, Language language ) {
		if ( entityNames.containsKey( language ) ) {
			Map<Entity, List<EntityName>> map = entityNames.get( language );
			if ( map.containsKey( entity ) ) {
				return map.get( entity );
			} else {
				return Collections.EMPTY_LIST;
			}
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public List<Triple> getTriplesByPredicate( Relationship relationship ) {
		if ( !allTriplesCategorized ) {
			categorizeAllTriples();
		}

		if ( triplesByPredicate.containsKey( relationship ) ) {
			return triplesByPredicate.get( relationship );
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public List<Triple> getTriplesBySubject( BaseEntity subject ) {
		if ( !allTriplesCategorized ) {
			categorizeAllTriples();
		}

		if ( triplesBySubject.containsKey( subject ) ) {
			return triplesBySubject.get( subject );
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public List<Triple> getTriplesByObject( BaseEntity object ) {
		if ( !allTriplesCategorized ) {
			categorizeAllTriples();
		}

		if ( triplesByObject.containsKey( object ) ) {
			return triplesByObject.get( object );
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	public long getLastUpdate() {
		ensureLatestData();
		return lastParse;
	}

	public java.util.concurrent.locks.ReentrantLock getUpdateLock() {
		ensureLatestData();
		return updateLock;
	}
}
