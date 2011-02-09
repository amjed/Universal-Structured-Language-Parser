/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.ParametizedFindEntity;
import jellyfish.common.persistence.PersistenceObject;
import java.util.*;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_LANGUAGE")
public class Language extends PersistenceObject {

    public static final String ATTR_NAME = "name";
    
    @javax.persistence.Column(length=100, nullable=false, unique=true)
    private String name;

    public Language() {
    }

    public Language( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        String oldValue = name;
        this.name = name;
        changeSupport.firePropertyChange(ATTR_NAME, oldValue, name);
    }

    public static class LanguageService {

        private javax.persistence.EntityManager em;
        private final ParametizedFindEntity<Language> findLanguage;

        public LanguageService( javax.persistence.EntityManager em ) {
            this.em = em;
            this.findLanguage = new ParametizedFindEntity<Language>( em, Language.class );
            this.findLanguage.addCriteria( ATTR_NAME, String.class );
        }

        public List<Language> findLanguages( String name ) {
            synchronized (this.findLanguage) {
                this.findLanguage.setParameter( ATTR_NAME, name );
                List<Language> languageList = this.findLanguage.getQuery().getResultList();
                return languageList;
            }
        }

        public Language createOrLoad( String name ) {
            List<Language> preExisting = findLanguages( name );
            if (preExisting.isEmpty()) {
                Language language = new Language( name );
                em.persist( language );
                return language;
            } else {
                return preExisting.iterator().next();
            }
        }

    }

}
