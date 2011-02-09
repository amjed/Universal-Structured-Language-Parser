/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.xml;

import jellyfish.xml.XmlCommon;
import jellyfish.xml.XmlNodeType;
import jellyfish.matcher.ClauseParser;
import jellyfish.matcher.clauses.AndClause;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import jellyfish.tokenizer.Tokenizer;
import jellyfish.matcher.dictionary.TokenDictionary;

/**
 *
 * @author Umran
 */
public class XmlClauseParser extends jellyfish.xml.XmlParser implements ClauseParser {

    /*
     * Types of nodes:
     *      clause - clause definition (and composite)
     *      g - group (and composite)
     *      o - optional portion (and composite)
     *      r - repeatable portion (and composite)
     *      s - switch portion (or composite)
     *      var - variable (variable simple)
     *      in - input (input simple)
     *      ref - a reference to a clause in another clause
     */
    private static final int NUM_NODE_TYPES = 8;

    private XmlCommon common = new XmlCommon();
    private Tokenizer tokenizer;
    private Map<String,AndClause> namedClauses;
    private TokenDictionary latestDictionary;
    private ReferenceNodeParser referenceParser;
    private ConstantNodeParser constantParser;
    
    public XmlClauseParser(Tokenizer tokenizer) {
        super(NUM_NODE_TYPES);
        this.tokenizer = tokenizer;
        this.namedClauses = new TreeMap<String, AndClause>(new jellyfish.common.CaseInsensitiveStringComparator());

        XmlNodeType rootType = new XmlNodeType(common,Node.ELEMENT_NODE,"root");
        XmlNodeType clauseType = new XmlNodeType(common,Node.ELEMENT_NODE,"clause");
        XmlNodeType groupType = new XmlNodeType(common,Node.ELEMENT_NODE,"g");
        XmlNodeType optionalType = new XmlNodeType(common,Node.ELEMENT_NODE,"o");
        XmlNodeType repeatType = new XmlNodeType(common,Node.ELEMENT_NODE,"r");
        XmlNodeType switchType = new XmlNodeType(common,Node.ELEMENT_NODE,"s");
        XmlNodeType tokenType1 = new XmlNodeType(common,Node.TEXT_NODE);
        XmlNodeType tokenType2 = new XmlNodeType(common,Node.CDATA_SECTION_NODE);
        XmlNodeType variableType = new XmlNodeType(common,Node.ELEMENT_NODE,"var");
        XmlNodeType inputType = new XmlNodeType(common,Node.ELEMENT_NODE,"in");
        XmlNodeType referenceType = new XmlNodeType(common,Node.ELEMENT_NODE,"ref");

        this.addParser(clauseType, new ClauseNodeParser(namedClauses));
        this.addParser(groupType, new GroupNodeParser());
        this.addParser(optionalType, new OptionalNodeParser());
        this.addParser(repeatType, new RepeatableNodeParser());
        this.addParser(switchType, new SwitchNodeParser());
        this.addParser(variableType, new VariableNodeParser());
        this.addParser(inputType, new InputNodeParser());

        this.constantParser = new ConstantNodeParser(this.tokenizer);
        this.addParser(tokenType1, this.constantParser);
        this.addParser(tokenType2, this.constantParser);
        
        this.referenceParser = new ReferenceNodeParser(namedClauses);
        this.addParser(referenceType, referenceParser);
        
        this.setPossibleChild(rootType, clauseType);

        this.setPossibleChild(clauseType, groupType);
        this.setPossibleChild(clauseType, optionalType);
        this.setPossibleChild(clauseType, repeatType);
        this.setPossibleChild(clauseType, switchType);
        this.setPossibleChild(clauseType, tokenType1);
        this.setPossibleChild(clauseType, tokenType2);
        this.setPossibleChild(clauseType, variableType);
        this.setPossibleChild(clauseType, referenceType);
        this.setPossibleChild(clauseType, inputType);

        this.setPossibleChild(groupType, groupType);
        this.setPossibleChild(groupType, optionalType);
        this.setPossibleChild(groupType, repeatType);
        this.setPossibleChild(groupType, switchType);
        this.setPossibleChild(groupType, tokenType1);
        this.setPossibleChild(groupType, tokenType2);
        this.setPossibleChild(groupType, variableType);
        this.setPossibleChild(groupType, referenceType);
        this.setPossibleChild(groupType, inputType);

        this.setPossibleChild(optionalType, groupType);
        this.setPossibleChild(optionalType, optionalType);
        this.setPossibleChild(optionalType, repeatType);
        this.setPossibleChild(optionalType, switchType);
        this.setPossibleChild(optionalType, tokenType1);
        this.setPossibleChild(optionalType, tokenType2);
        this.setPossibleChild(optionalType, variableType);
        this.setPossibleChild(optionalType, referenceType);
        this.setPossibleChild(optionalType, inputType);

        this.setPossibleChild(repeatType, groupType);
        this.setPossibleChild(repeatType, optionalType);
        this.setPossibleChild(repeatType, repeatType);
        this.setPossibleChild(repeatType, switchType);
        this.setPossibleChild(repeatType, tokenType1);
        this.setPossibleChild(repeatType, tokenType2);
        this.setPossibleChild(repeatType, variableType);
        this.setPossibleChild(repeatType, referenceType);
        this.setPossibleChild(repeatType, inputType);

        this.setPossibleChild(switchType, groupType);
        this.setPossibleChild(switchType, switchType);
        this.setPossibleChild(switchType, tokenType1);
        this.setPossibleChild(switchType, tokenType2);
        this.setPossibleChild(switchType, inputType);

    }

    @Override
    public synchronized void parse(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        latestDictionary = new TokenDictionary();
        namedClauses.clear();
        constantParser.setDictionary( latestDictionary );
        super.parse(in);
        referenceParser.processReferences();
    }

    public TokenDictionary getLatestDictionary() {
        return latestDictionary;
    }

    public AndClause getNamedClause(String name) {
        return namedClauses.get(name);
    }

    public List<AndClause> getPrimaryClauses() {
        List<AndClause> primaryClauses = new ArrayList<AndClause>();
        for (AndClause clause:namedClauses.values())
            if (clause.isPrimaryClause())
                primaryClauses.add( clause );
        return primaryClauses;
    }

    public Set<String> getClauseNames() {
        return Collections.unmodifiableSet(namedClauses.keySet());
    }

}
