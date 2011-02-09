package jellyfish.common.persistence;

import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ParametizedFindEntity<EntityType> {

    private Class<EntityType> entityClass;
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<EntityType> cq;
    private Root<EntityType> root;
    private Map<String, ParameterExpression<?>> parameters;
    private boolean compiled;
    private TypedQuery<EntityType> query;

    public ParametizedFindEntity( EntityManager em,
                                  Class<EntityType> entityClass ) {
        this.em = em;
        this.entityClass = entityClass;
        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery( entityClass );
        this.root = cq.from( entityClass );
        
        this.parameters = new TreeMap<String, ParameterExpression<?>>( new jellyfish.common.CaseSensitiveStringComparator() );
    }

    protected <T> void addCriteria( String attributeName, Class<T> attributeType ) {
        parameters.put( attributeName, cb.parameter( attributeType, attributeName ) );
    }

    public <T> void addCriteria( Attribute attribute ) {
        addCriteria(attribute.getName(), attribute.getType());
    }

    private synchronized void compileCriteriaQuery()
    {
        if (compiled) return;

        Predicate lastPred = null;
        for (Map.Entry<String, ParameterExpression<?>> entry : parameters.entrySet()) {
            Predicate currentPred = cb.equal( root.get( entry.getKey() ), entry.getValue() );
            if (lastPred == null) {
                lastPred = currentPred;
            } else {
                lastPred = cb.and( lastPred, currentPred );
            }
        }

        if (lastPred != null) {
            cq.where( lastPred );
        }

        this.query = em.createQuery( cq );

        this.compiled = true;
    }

    public TypedQuery<EntityType> getQuery() {
        if (!compiled) compileCriteriaQuery();
        
        return query;
    }

    protected <T> void setParameter( String attributeName, T value ) {
        if (!compiled) compileCriteriaQuery();

        this.query.setParameter( (ParameterExpression<Object>)parameters.get( attributeName ),
                                 (Object)value );
    }

    public void setParameter( Attribute attribute, Object value ) {
        setParameter(attribute.getName(), value);
    }

}
