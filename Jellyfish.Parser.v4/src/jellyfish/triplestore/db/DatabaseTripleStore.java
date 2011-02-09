/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.triplestore.db;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceUtil;
import jellyfish.triplestore.ReferenceEngine;
import jellyfish.triplestore.TripleStore;
import jellyfish.triplestore.model.*;

/**
 *
 * @author Xevia
 */
public class DatabaseTripleStore
		implements TripleStore
{
	private final Object updateLock = new Object();

	private EntityManager em;
	private LanguageService languageService;
	private RelationshipService relationshipService;
	private EntityService entityService;
	private TripleService tripleService;
	private EntityNameService entityNameService;

	public DatabaseTripleStore( EntityManager em ) {
		this.em = em;
		this.languageService = new LanguageService( em );
		this.relationshipService = new RelationshipService( em );
		this.entityService = new EntityService( em );
		this.tripleService = new TripleService( em );
		this.entityNameService = new EntityNameService( em );
	}

	public Collection<Language> getLanguages() {
		return PersistenceUtil.getAllList( em, Language.class );
	}

	public Collection<Relationship> getRelationships() {
		return PersistenceUtil.getAllList( em, Relationship.class );
	}

	public Collection<Entity> getEntities() {
		return PersistenceUtil.getAllList( em, Entity.class );
	}

	public Collection<Triple> getTriples() {
		return PersistenceUtil.getAllList( em, Triple.class );
	}

	public Language getLanguage( String name ) {
		return languageService.findLanguage( name );
	}

	public Relationship getRelationship( String name ) {
		return relationshipService.findRelationship( name );
	}

	public Entity getEntity( String name ) {
		return entityService.findEntity( name );
	}

	public List<EntityName> getEntityNames( Entity entity, Language language ) {
		return entityNameService.findByEntityLanguage( entity, language );
	}

	public List<Triple> getTriplesByPredicate( Relationship relationship ) {
		return tripleService.findByPredicates( relationship );
	}

	public List<Triple> getTriplesBySubject( BaseEntity subject ) {
		return tripleService.findBySubject( subject );
	}

	public List<Triple> getTriplesByObject( BaseEntity object ) {
		return tripleService.findByObject( object );
	}

	public long getLastUpdate() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public java.util.concurrent.locks.ReentrantLock getUpdateLock() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	private static class TripleService
	{

		private javax.persistence.EntityManager em;
		private final ParametizedFindEntity<Triple> findByPredicate;
		private final ParametizedFindEntity<Triple> findBySubject;
		private final ParametizedFindEntity<Triple> findByObject;

		public TripleService( javax.persistence.EntityManager em ) {
			this.em = em;

			this.findByPredicate = new ParametizedFindEntity<Triple>( em, Triple.class );
			this.findByPredicate.addCriteria( Triple.ATTR_PREDICATE );

			this.findBySubject = new ParametizedFindEntity<Triple>( em, Triple.class );
			this.findBySubject.addCriteria( Triple.ATTR_SUBJECT );

			this.findByObject = new ParametizedFindEntity<Triple>( em, Triple.class );
			this.findByObject.addCriteria( Triple.ATTR_OBJECT );
		}

		public List<Triple> findByPredicates( Relationship p ) {
			synchronized ( this.findByPredicate ){
				this.findByPredicate.setParameter( Triple.ATTR_PREDICATE, p );
				List<Triple> tripleList = this.findByPredicate.getQuery().getResultList();
				return tripleList;
			}
		}

		public List<Triple> findBySubject( BaseEntity p ) {
			synchronized ( this.findByPredicate ){
				this.findBySubject.setParameter( Triple.ATTR_SUBJECT, p );
				List<Triple> tripleList = this.findBySubject.getQuery().getResultList();
				return tripleList;
			}
		}

		public List<Triple> findByObject( BaseEntity p ) {
			synchronized ( this.findByPredicate ){
				this.findByObject.setParameter( Triple.ATTR_OBJECT, p );
				List<Triple> tripleList = this.findByObject.getQuery().getResultList();
				return tripleList;
			}
		}
	}

	private static class RelationshipService
	{

		private javax.persistence.EntityManager em;
		private final ParametizedFindEntity<Relationship> findRelationship;

		public RelationshipService( javax.persistence.EntityManager em ) {
			this.em = em;
			this.findRelationship = new ParametizedFindEntity<Relationship>( em, Relationship.class );
			this.findRelationship.addCriteria( Relationship.ATTR_NAME );
		}

		public List<Relationship> fetchAll() {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Relationship> cq = cb.createQuery( Relationship.class );
			Root<Relationship> root = cq.from( Relationship.class );
			cq.orderBy( cb.asc( root.get( Relationship.ATTR_NAME.getName() ) ) );
			return em.createQuery( cq ).getResultList();
		}

		public Relationship findRelationship( String name ) {
			synchronized ( this.findRelationship ){
				this.findRelationship.setParameter( Language.ATTR_NAME, name );
				return this.findRelationship.getQuery().getSingleResult();
			}
		}
	}

	private static class LanguageService
	{

		private javax.persistence.EntityManager em;
		private final ParametizedFindEntity<Language> findLanguage;

		public LanguageService( javax.persistence.EntityManager em ) {
			this.em = em;
			this.findLanguage = new ParametizedFindEntity<Language>( em, Language.class );
			this.findLanguage.addCriteria( Language.ATTR_NAME );
		}

		public Language findLanguage( String name ) {
			synchronized ( this.findLanguage ){
				this.findLanguage.setParameter( Language.ATTR_NAME, name );
				return this.findLanguage.getQuery().getSingleResult();
			}
		}
//        public Language createOrLoad( String name ) {
//            List<Language> preExisting = findLanguages( name );
//            if (preExisting.isEmpty()) {
//                Language language = new Language( name );
//                em.persist( language );
//                return language;
//            } else {
//                return preExisting.iterator().next();
//            }
//        }
	}

	private static class EntityNameService
	{

		private javax.persistence.EntityManager em;
		private final Object syncFindByName = new Object();
		private ParametizedFindEntity<EntityName> findByName;
		private final Object syncFindByEntityLanguage = new Object();
		private ParametizedFindEntity<EntityName> findByEntityLanguage;
		private final Object syncFindByNameEntityLanguage = new Object();
		private ParametizedFindEntity<EntityName> findByNameEntityLanguage;

		public EntityNameService( javax.persistence.EntityManager em ) {
			this.em = em;
		}

		private void initFindByName() {
			synchronized ( syncFindByName ){
				if ( this.findByName != null ) {
					return;
				}

				ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>(
						em, EntityName.class );
				findEntity.addCriteria( EntityName.ATTR_NAME );

				this.findByName = findEntity;
			}
		}

		private void initFindByEntityLanguage() {
			synchronized ( syncFindByEntityLanguage ){
				if ( this.findByEntityLanguage != null ) {
					return;
				}

				ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>(
						em, EntityName.class );
				findEntity.addCriteria( EntityName.ATTR_ENTITY );
				findEntity.addCriteria( EntityName.ATTR_LANGUAGE );

				this.findByEntityLanguage = findEntity;
			}
		}

		private void initFindByNameEntityLanguage() {
			synchronized ( syncFindByNameEntityLanguage ){
				if ( this.findByNameEntityLanguage != null ) {
					return;
				}

				ParametizedFindEntity<EntityName> findEntity = new ParametizedFindEntity<EntityName>(
						em, EntityName.class );
				this.findByNameEntityLanguage.addCriteria( EntityName.ATTR_NAME );
				this.findByNameEntityLanguage.addCriteria( EntityName.ATTR_ENTITY );
				this.findByNameEntityLanguage.addCriteria( EntityName.ATTR_LANGUAGE );

				this.findByNameEntityLanguage = findEntity;
			}
		}

		public List<EntityName> findByName( String name ) {
			if ( this.findByName == null ) {
				initFindByName();
			}

			synchronized ( syncFindByName ){
				this.findByName.setParameter( EntityName.ATTR_NAME, name );
				List<EntityName> entityNameList = this.findByName.getQuery().getResultList();
				return entityNameList;
			}
		}

		public List<EntityName> findByEntityLanguage( Entity entity, Language language ) {
			if ( this.findByEntityLanguage == null ) {
				initFindByEntityLanguage();
			}

			synchronized ( syncFindByEntityLanguage ){
				this.findByEntityLanguage.setParameter( EntityName.ATTR_ENTITY, entity );
				this.findByEntityLanguage.setParameter( EntityName.ATTR_LANGUAGE, language );
				List<EntityName> entityNameList = this.findByEntityLanguage.getQuery().getResultList();
				return entityNameList;
			}
		}

		public synchronized List<EntityName> findByNameEntityLanguage( String name, Entity entity,
																	   Language language ) {
			if ( this.findByNameEntityLanguage == null ) {
				initFindByNameEntityLanguage();
			}

			synchronized ( syncFindByNameEntityLanguage ){
				this.findByNameEntityLanguage.setParameter( EntityName.ATTR_NAME, name );
				this.findByNameEntityLanguage.setParameter( EntityName.ATTR_ENTITY, entity );
				this.findByNameEntityLanguage.setParameter( EntityName.ATTR_LANGUAGE, language );
				List<EntityName> entityNameList = this.findByNameEntityLanguage.getQuery().
						getResultList();
				return entityNameList;
			}
		}
	}

	private static class EntityService
	{

		private javax.persistence.EntityManager em;
		private final ParametizedFindEntity<Entity> findByName;

		public EntityService( javax.persistence.EntityManager em ) {
			this.em = em;
			this.findByName = new ParametizedFindEntity<Entity>( em, Entity.class );
			this.findByName.addCriteria( Entity.ATTR_NAME );

		}

		public Entity findEntity( String name ) {
			synchronized ( this.findByName ){
				this.findByName.setParameter( Entity.ATTR_NAME, name );
				return this.findByName.getQuery().getSingleResult();
			}
		}
	}
}
