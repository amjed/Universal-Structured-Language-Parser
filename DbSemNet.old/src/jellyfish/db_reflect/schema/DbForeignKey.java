/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.db_reflect.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jellyfish.common.Pair;

/**
 *
 * @author Xevia
 *
 * in column pairing, first is foreign key column, second is primary key column
 *
 */
public class DbForeignKey extends DbItem<DbSchema> {

    public static class KeyPair {
        private DbColumn foreignKeyTableColumn;
        private DbColumn primaryKeyTableColumn;

        public KeyPair( DbColumn foreignKeyTableColumn, DbColumn primaryKeyTableColumn ) {
            this.foreignKeyTableColumn = foreignKeyTableColumn;
            this.primaryKeyTableColumn = primaryKeyTableColumn;
        }

        public DbColumn getForeignKeyTableColumn() {
            return foreignKeyTableColumn;
        }

        public DbColumn getPrimaryKeyTableColumn() {
            return primaryKeyTableColumn;
        }

        
    }

    private DbTable foreignKeyTable;
    private DbTable primaryKeyTable;
    private List<DbColumn> foreignKeyTableColumns;
    private List<DbColumn> primaryKeyTableColumns;
    private List<KeyPair> columnPairing;
    private DbCandidateKey referencedKey;

    public DbForeignKey( DbCatalog catalog, String name,
                         DbTable foreignKeyTable,
                         DbTable primaryKeyTable,
                         List<KeyPair> foreignKeyColumns,
                         DbCandidateKey referencedKey ) {
        super( catalog, name );
        this.foreignKeyTable = foreignKeyTable;
        this.primaryKeyTable = primaryKeyTable;
        this.columnPairing = Collections.unmodifiableList( foreignKeyColumns );
        this.foreignKeyTableColumns = new ArrayList<DbColumn>(foreignKeyColumns.size());
        this.primaryKeyTableColumns = new ArrayList<DbColumn>(foreignKeyColumns.size());
        for (KeyPair v:foreignKeyColumns) {
            foreignKeyTableColumns.add( v.getForeignKeyTableColumn() );
            primaryKeyTableColumns.add( v.getPrimaryKeyTableColumn() );
        }
        this.foreignKeyTableColumns = Collections.unmodifiableList( foreignKeyTableColumns );
        this.primaryKeyTableColumns = Collections.unmodifiableList( primaryKeyTableColumns );
        this.referencedKey = referencedKey;
        registerKey();
    }

    private void registerKey() {
        foreignKeyTable.addForeignKey( this );
    }

    public DbTable getPrimaryKeyTable() {
        return primaryKeyTable;
    }
    
    public DbTable getForeignKeyTable() {
        return foreignKeyTable;
    }

    public DbCandidateKey getReferencedKey() {
        return referencedKey;
    }

    public List<DbColumn> getForeignKeyTableColumns() {
        return foreignKeyTableColumns;
    }

    public List<DbColumn> getPrimaryKeyTableColumns() {
        return primaryKeyTableColumns;
    }

    public List<KeyPair> getColumnPairing() {
        return columnPairing;
    }
    
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append( this.getName() ).append( "{ " ).append( foreignKeyTable ).append( " ( " );
        for (int i = 0; i < columnPairing.size(); ++i) {
            if (i != 0) {
                bldr.append( ", " );
            }
            bldr.append( columnPairing.get( i ).getForeignKeyTableColumn() );
        }
        bldr.append( ") ref " ).append( referencedKey.getTable() );
        for (int i = 0; i < columnPairing.size(); ++i) {
            if (i != 0) {
                bldr.append( ", " );
            }
            bldr.append( columnPairing.get( i ).getPrimaryKeyTableColumn() );
        }
        bldr.append( ") } ");
        return bldr.toString();
    }
}
