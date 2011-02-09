/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.triplestore.model;

import jellyfish.common.persistence.PersistenceObject;
import jellyfish.common.persistence.Attribute;

/**
 *
 * @author Xevia
 */
@javax.persistence.Entity
@javax.persistence.Table(name="TRIPLESTORE_LANGUAGE")
public class Language extends PersistenceObject {

    public static final Attribute ATTR_NAME = getDeclaredField(Language.class, "name");
    
    @javax.persistence.Column(length=100, nullable=false, unique=true)
    private String name;

    @javax.persistence.Column(length=250, nullable=false, unique=false)
    private String tokenizerClass;

    @javax.persistence.Column(length=500, nullable=false, unique=false)
    private String clausesFile;
    
    public Language() {
    }

    public Language( String name, String tokenizerClass, String clausesFile ) {
	this.name = name;
	this.tokenizerClass = tokenizerClass;
	this.clausesFile = clausesFile;
    }
    
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        setAttribute(ATTR_NAME, name);
    }

    public String getTokenizerClass() {
	return tokenizerClass;
    }

    public void setTokenizerClass( String tokenizerClass ) {
	this.tokenizerClass = tokenizerClass;
    }

    public String getClausesFile() {
	return clausesFile;
    }

    public void setClausesFile( String clausesFile ) {
	this.clausesFile = clausesFile;
    }


}
