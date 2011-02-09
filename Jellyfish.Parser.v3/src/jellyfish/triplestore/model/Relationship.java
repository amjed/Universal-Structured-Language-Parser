/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import java.lang.reflect.Field;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
//@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
@javax.persistence.Table(name="TRIPLESTORE_RELATIONSHIP")
public class Relationship extends BaseEntity {

    public static final Attribute ATTR_TRANSITIVE = getDeclaredField(Relationship.class, "transitive");
    public static final Attribute ATTR_SYMMETRIC = getDeclaredField(Relationship.class, "symmetric");

    @javax.persistence.Column(nullable=false)
    private boolean transitive;

    @javax.persistence.Column(nullable=false)
    private boolean symmetric;

    public Relationship() {
    }

    public Relationship( String name, boolean transitive,
                                        boolean symmetric ) {
        super( name );
        this.transitive = transitive;
        this.symmetric = symmetric;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public void setSymmetric( boolean symmetric ) {
        setAttribute(ATTR_SYMMETRIC, symmetric);
    }

    public boolean isTransitive() {
        return transitive;
    }

    public void setTransitive( boolean transitive ) {
        setAttribute(ATTR_TRANSITIVE, transitive);
    }

    @Override
    public boolean isRelationship() {
        return true;
    }

    @Override
    public String toString() {
        return "Relationship{" + this.getName()+", t=" + transitive + ", s=" + symmetric + '}';
    }

    /*
    public static class RelationshipService {

        private javax.persistence.EntityManager em;

        public RelationshipService( javax.persistence.EntityManager em ) {
            this.em = em;
        }

        public List<Relationship> fetchAll() {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Relationship> cq = cb.createQuery(Relationship.class);
            Root<Relationship> root = cq.from(Relationship.class);
            cq.orderBy(cb.asc(root.get(ATTR_NAME.getName())));
            return em.createQuery(cq).getResultList();
        }
    }
    */
}
