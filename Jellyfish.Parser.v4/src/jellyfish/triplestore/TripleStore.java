/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore;

import java.util.Collection;
import java.util.List;
import jellyfish.triplestore.model.*;

/**
 *
 * @author Xevia
 *
 */
public interface TripleStore {

    Collection<Language> getLanguages();
    Collection<Relationship> getRelationships();
    Collection<Entity> getEntities();
    Collection<Triple> getTriples();

    Language getLanguage(String name);
    Relationship getRelationship(String name);
    Entity getEntity(String entity);

    List<EntityName> getEntityNames(Entity entity, Language language);

    List<Triple> getTriplesByPredicate(Relationship relationship);
    List<Triple> getTriplesBySubject(BaseEntity subject);
    List<Triple> getTriplesByObject(BaseEntity object);

	//	returns an object which can be used to lock the triplestore
	//		so that the state of the triplestore doesn't change in-between
	//		re-construction of other portions of this project. e.g. ReferenceEngine and
	//		VariableContext. Any class wishing to do some substancial work (other than
	//		a single fetch) should acquire the lock before attempting the work
	java.util.concurrent.locks.ReentrantLock getUpdateLock();

	long getLastUpdate();

//    ReferenceEngine compile();

}
