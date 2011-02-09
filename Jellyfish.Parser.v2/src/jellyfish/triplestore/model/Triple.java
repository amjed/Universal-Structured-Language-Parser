/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.PersistenceObject;
import java.io.Serializable;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import jellyfish.common.persistence.Attribute;
import jellyfish.common.persistence.ParametizedFindEntity;

/**
 *
 * @author Xevia
 */

@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_TRIPLE")
public class Triple extends PersistenceObject {

    public static final Attribute ATTR_SUBJECT = getDeclaredField(Triple.class, "subject");
    public static final Attribute ATTR_PREDICATE = getDeclaredField(Triple.class, "predicate");
    public static final Attribute ATTR_OBJECT = getDeclaredField(Triple.class, "object");

    @javax.persistence.ManyToOne(fetch=FetchType.EAGER,optional=false)
    private BaseEntity subject;

    @javax.persistence.ManyToOne(fetch=FetchType.EAGER,optional=false)
    private Relationship predicate;

    @javax.persistence.ManyToOne(fetch=FetchType.EAGER,optional=false)
    private BaseEntity object;

    public Triple() {
    }

    public Triple( BaseEntity subject, Relationship predicate, BaseEntity object ) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }
    
    public BaseEntity getObject() {
        return object;
    }

    public void setObject( BaseEntity object ) {
        setAttribute(ATTR_OBJECT, object);
    }

    public Relationship getPredicate() {
        return predicate;
    }

    public void setPredicate( Relationship predicate ) {
        setAttribute(ATTR_PREDICATE, predicate);
    }

    public BaseEntity getSubject() {
        return subject;
    }

    public void setSubject( BaseEntity subject ) {
        setAttribute(ATTR_SUBJECT, subject);
    }

    @Override
    public String toString() {
        return "Triple(" + subject +"," + predicate + "," +object +")";
    }

    public static class TripleService {

        private javax.persistence.EntityManager em;
        private final ParametizedFindEntity<Triple> findByPredicate;

        public TripleService( javax.persistence.EntityManager em ) {
            this.em = em;
            this.findByPredicate = new ParametizedFindEntity<Triple>( em, Triple.class );
            this.findByPredicate.addCriteria( ATTR_PREDICATE );
        }

        public List<Triple> findByPredicates( Relationship p ) {
            synchronized (this.findByPredicate) {
                this.findByPredicate.setParameter( ATTR_PREDICATE, p );
                List<Triple> tripleList = this.findByPredicate.getQuery().getResultList();
                return tripleList;
            }
        }
        

    }


    
}
