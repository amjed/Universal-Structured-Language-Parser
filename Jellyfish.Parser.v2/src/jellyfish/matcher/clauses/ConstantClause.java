/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.matcher.clauses;

import jellyfish.matcher.AliasMatcherMap;
import jellyfish.matcher.AliasTreeNode;
import jellyfish.matcher.nodes.ConstantMatcherNode;
import jellyfish.matcher.nodes.MatcherNode;
import jellyfish.matcher.dictionary.*;

/**
 *
 * @author Umran
 */
public final class ConstantClause extends SimpleClause {

    private TokenDictionary dictionary;
    private DictionaryEntry entry;

    public ConstantClause( String xmlLocation, String name, TokenDictionary dictionary,
                           String value )
    {
        super(xmlLocation, name);
        this.dictionary = dictionary;
        this.entry = dictionary.registerEntry( value );
    }

    public TokenDictionary getDictionary() {
        return dictionary;
    }

    public DictionaryEntry getEntry() {
        return entry;
    }
    
    @Override
    public SimpleClauseType getSimpleClauseType() {
        return SimpleClauseType.CONSTANT_CLAUSE;
    }

    @Override
    public String toString() {
        return "'" + entry.getWord() + "'";
    }

    protected void buildMatchTree(AliasTreeNode alias, NodeAliasList prevNodes, NodeAliasList ends) {
        for (int i=0; i<prevNodes.size(); ++i) {
            
            MatcherNode prevNode = prevNodes.getMatcherNode( i );
            
            MatcherNode newNode = prevNode.registerNextNode(
                    new ConstantMatcherNode(dictionary, entry.getWord() ));

            AliasMatcherMap aliasMatcherMap = prevNodes.getAliasMatcherMap( i );

            if (aliasMatcherMap!=null)
                ends.add( newNode,
                          AliasMatcherMap.createMap( aliasMatcherMap, alias, newNode ) );
            else
                ends.add( newNode, null );
        }
    }
    
}
