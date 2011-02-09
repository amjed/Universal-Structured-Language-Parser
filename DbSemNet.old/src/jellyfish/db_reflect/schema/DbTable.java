/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import jellyfish.common.CaseInsensitiveStringComparator;

/**
 *
 * @author Xevia
 */
public class DbTable extends DbItem<DbSchema> {

    private DbCandidateKey primaryKey;

    private boolean candidateKeysInitialized;
    private Map<String,DbCandidateKey> candidateKeys;

    private boolean foreignKeysInitialized;
    private Map<String,DbForeignKey> foreignKeys;

    private boolean tablesReferedToNeedsRecomputing;
    private Set<DbTable> tablesReferedTo;
    
    public DbTable( DbCatalog catalog, String name ) {
        super( catalog, name );
        this.primaryKey = null;

        this.candidateKeysInitialized = false;
        this.candidateKeys = Collections.EMPTY_MAP;

        this.foreignKeysInitialized = false;
        this.foreignKeys = Collections.EMPTY_MAP;

        this.tablesReferedToNeedsRecomputing = true;
        tablesReferedTo = Collections.EMPTY_SET;
    }

    private synchronized void initializeUniqueKeys() {
        if (candidateKeysInitialized) return;

        this.candidateKeys = new TreeMap<String,DbCandidateKey>( new CaseInsensitiveStringComparator() );

        candidateKeysInitialized = true;
    }
    
    private synchronized void initializeForeignKeys() {
        if (foreignKeysInitialized) return;

        this.foreignKeys = new TreeMap<String,DbForeignKey>( new CaseInsensitiveStringComparator() );

        foreignKeysInitialized = true;
    }

    public Collection<DbColumn> getColumns() {
        return this.getChildrenOfType( DbColumn.class );
    }

    public DbColumn getColumnByName(String name) {
        return this.getChildByName( name, DbColumn.class );
    }

    public DbCandidateKey getCandidateKeyByName(String name) {
        return candidateKeys.get( name );
    }

    public void setPrimaryKey( DbCandidateKey primaryKey ) {
        if (!candidateKeysInitialized) initializeUniqueKeys();
        
        this.primaryKey = primaryKey;
        this.candidateKeys.put( primaryKey.getName(), primaryKey );
    }

    public void addUniqueKey(  DbCandidateKey uniqueKey ) {
        if (!candidateKeysInitialized) initializeUniqueKeys();

        this.candidateKeys.put( uniqueKey.getName(), uniqueKey );
    }

    public void addForeignKey(  DbForeignKey foreignKey ) {
        if (!foreignKeysInitialized) initializeForeignKeys();

        foreignKeys.put( foreignKey.getName(), foreignKey );
        tablesReferedToNeedsRecomputing = true;
    }
    
    public DbCandidateKey getPrimaryKey() {
        return primaryKey;
    }

    public Collection<DbCandidateKey> getCandidateKeys() {
        return Collections.unmodifiableCollection( candidateKeys.values() );
    }

    public Collection<DbForeignKey> getForeignKeys() {
        return Collections.unmodifiableCollection( foreignKeys.values() );
    }

    public List<DbCandidateKey> getUniqueKeys() {
        List<DbCandidateKey> keys = new ArrayList<DbCandidateKey>(candidateKeys.values());
        if (primaryKey!=null)
            keys.remove( primaryKey );
        return keys;
    }

    private synchronized void recomputeTablesReferedTo() {
        if (!tablesReferedToNeedsRecomputing) return;

        tablesReferedTo = new TreeSet<DbTable>();
        for (DbForeignKey fk:foreignKeys.values()) {
            tablesReferedTo.add( fk.getPrimaryKeyTable() );
        }

        tablesReferedToNeedsRecomputing = false;
    }

    public Set<DbTable> getTablesReferedTo() {
        if (tablesReferedToNeedsRecomputing) recomputeTablesReferedTo();
        return tablesReferedTo;
    }

//    private Set<DbTable> get

}
