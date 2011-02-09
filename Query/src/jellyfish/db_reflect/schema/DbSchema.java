/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.db_reflect.schema;

import jellyfish.db_reflect.DbLoadException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
public class DbSchema extends DbItem {
    
    public DbSchema( DbCatalog catalog, String name ) {
        super( catalog, name );
    }

    public DbTable getTableByName( String name ) {
        return (DbTable)this.getChildByName( name, DbTable.class );
    }

    public Collection<DbTable> getTables() {
        return this.getChildrenOfType( DbTable.class );
    }

    public Collection<DbCandidateKey> getCandidateKeys() {
        return this.getChildrenOfType( DbCandidateKey.class );
    }

    public Collection<DbForeignKey> getForeignKeys() {
        return this.getChildrenOfType( DbForeignKey.class );
    }

    @Override
    protected boolean isAnonymous() {
        return !getCatalog().isAllowsSchemasInDataManipulation();
    }

    private void loadTables() throws Exception {
        ResultSet rs = null;
        try {
            rs = getCatalog().getMetaData().getTables( null, this.getName(), null,
                                 new String[] { "TABLE" } );
            while (rs.next()) {
                String tableName = rs.getString( "TABLE_NAME" );
                DbTable table = new DbTable( getCatalog(), tableName );
                table.setParent( this );
            }
            rs.close();
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }

    private void loadTableColumns() throws Exception {
        ResultSet rs = null;
        try {
            for (DbTable table:getTables()) {
                rs = getCatalog().getMetaData().getColumns( null, this.getName(), table.getName(), null );
                while (rs.next()) {
                    String columnName = rs.getString( "COLUMN_NAME" );
                    int dataType = rs.getInt( "DATA_TYPE" );
                    String typeName = rs.getString( "TYPE_NAME" );
                    int columnSize = rs.getInt( "COLUMN_SIZE" );
                    String isNullable = rs.getString( "IS_NULLABLE" );

                    DbColumn column = new DbColumn(
                            getCatalog(), columnName, dataType, typeName, columnSize,
                            isNullable.equals( "YES" ) );
                    column.setParent( table );
                }
                rs.close();
            }
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }

    private class KeyDetails {

        public final String keyName;
        public final DbTable table;
        public final List<DbColumn> columns;
        public final Set<Integer> keySeqNos;

        public KeyDetails( String pkName, DbTable table ) {
            this.keyName = pkName;
            this.table = table;
            this.columns = new ArrayList<DbColumn>( 1 );
            this.keySeqNos = new HashSet<Integer>(table.getColumns().size());
        }

        public void includeColumn(int keySeqNo, DbColumn column ) {
            if (keySeqNos.contains( keySeqNo ))
                throw new jellyfish.db_reflect.DbLoadException(
                        "The a column with the key sequence '" + keySeqNo + "' was already inserted into the key named " + keyName );
            while (this.columns.size()<=keySeqNo)
                this.columns.add( null );
            this.columns.set( keySeqNo, column );
            this.keySeqNos.add( keySeqNo );
        }
    }

    private void loadColumnPrimaryKeys() throws Exception {
        ResultSet rs = null;
        try {
            for (DbTable table:getTables()) {
                rs = getCatalog().getMetaData().getPrimaryKeys( null, this.getName(), table.getName() );

                Map<String, KeyDetails> keyDetails = new TreeMap<String, KeyDetails>(
                        new CaseInsensitiveStringComparator() );

                while (rs.next()) {
                    String columnName = rs.getString( "COLUMN_NAME" );
                    int keySeq = rs.getInt( "KEY_SEQ" ) - 1;
                    String pkName = rs.getString( "PK_NAME" );

                    if (pkName==null || pkName.isEmpty())
                        pkName = getCatalog().getDefaultPrimaryKeyName();

                    DbColumn column = table.getColumnByName( columnName );
                    if (column == null) {
                        throw new jellyfish.db_reflect.DbLoadException(
                                "The column name '" + columnName + "' could not be found in the table " + table );
                    }

                    KeyDetails keyDetails1 = null;
                    if (keyDetails.containsKey( pkName )) {
                        keyDetails1 = keyDetails.get( pkName );
                    } else {
                        keyDetails1 = new KeyDetails( pkName, table );
                        keyDetails.put( pkName, keyDetails1 );
                    }

                    keyDetails1.includeColumn( keySeq, column );
                }

                for (KeyDetails details : keyDetails.values()) {
                    DbCandidateKey candidateKey = new DbCandidateKey(
                            getCatalog(),
                            details.keyName, details.table,
                            details.columns, true );
//                    candidateKey.setParent( table );
                }

                rs.close();
            }
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }

    private void loadUniqueKeys() throws Exception {
        ResultSet rs = null;
        try {
            for (DbTable table:getTables()) {
                rs = getCatalog().getMetaData().getIndexInfo( null, this.getName(), table.getName(), true, false );
                
                Map<String, KeyDetails> keyDetails = new TreeMap<String, KeyDetails>(
                        new CaseInsensitiveStringComparator() );

                while (rs.next()) {
                    String columnName = rs.getString( "COLUMN_NAME" );
                    int keySeq = rs.getInt( "ORDINAL_POSITION" ) - 1;
                    String indexName = rs.getString( "INDEX_NAME" );

                    if (indexName==null)
                        continue;

                    DbColumn column = table.getColumnByName( columnName );
                    if (column == null) {
                        throw new jellyfish.db_reflect.DbLoadException(
                                "The column name '" + columnName + "' could not be found in the table " + table );
                    }

                    KeyDetails keyDetails1 = null;
                    if (keyDetails.containsKey( indexName )) {
                        keyDetails1 = keyDetails.get( indexName );
                    } else {
                        keyDetails1 = new KeyDetails( indexName, table );
                        keyDetails.put( indexName, keyDetails1 );
                    }

                    keyDetails1.includeColumn( keySeq, column );
                }

                for (KeyDetails details : keyDetails.values()) {
//                    System.out.println( "candidate key: "+details.keyName );
                    if (!details.table.getPrimaryKey().getName().equals( details.keyName )) {
                        DbCandidateKey candidateKey = new DbCandidateKey(
                                getCatalog(),
                                details.keyName, details.table,
                                details.columns, false );
                    }
                }

                rs.close();
            }
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }


    private class RefDetails {

        public final String fkName;
        public final String pkName;
        public final List<DbForeignKey.KeyPair> columns;
        public final Set<Integer> keySeqNos;

        public RefDetails( String fkName, String pkName ) {
            this.fkName = fkName;
            this.pkName = pkName;
            this.columns = new ArrayList<DbForeignKey.KeyPair>(1);
            this.keySeqNos = new HashSet<Integer>();
        }

        public void includeColumn(int keySeqNo, DbColumn pkCol, DbColumn fkCol ) {
            if (keySeqNos.contains( keySeqNo ))
                throw new jellyfish.db_reflect.DbLoadException(
                        "The a column with the key sequence '" + keySeqNo + "' was already inserted into the key named " + fkName );
            while (this.columns.size()<=keySeqNo)
                this.columns.add( null );
            this.columns.set( keySeqNo, new DbForeignKey.KeyPair( fkCol, pkCol ) );
            this.keySeqNos.add( keySeqNo );
        }

        public void create() {
            DbCandidateKey pk = null;

            DbTable pkTable = null;
            DbTable fkTable = null;

            for (DbForeignKey.KeyPair keyPair:columns) {
                DbTable pkT = keyPair.getPrimaryKeyTableColumn().getParent();
                DbTable fkT = keyPair.getForeignKeyTableColumn().getParent();

                if (pkTable!=null && !pkT.equals( pkTable ))
                    throw new DbLoadException( "Foreign key '"+fkName+"' has multiple referenced key tables "+pkTable+", "+pkT );
                if (fkTable!=null && !fkT.equals( fkTable ))
                    throw new DbLoadException( "Foreign key '"+fkName+"' has multiple parent key tables "+fkTable+", "+fkT );
                
                pkTable = pkT;
                fkTable = fkT;
            }

            if (pkTable==null)
                throw new DbLoadException( "Unable to determine foreign key '"+fkName+"' referenced table " );
            if (fkTable==null)
                throw new DbLoadException( "Unable to determine foreign key '"+fkName+"' parent table " );

            if (pkName!=null) {
                pk = pkTable.getCandidateKeyByName( pkName );
            }
            
            if (pk==null)
                throw new DbLoadException( "Unable to find the referenced key '"+pkName+"' in table "+pkTable);

            DbForeignKey fk = new DbForeignKey( getCatalog(), fkName, fkTable, pkTable, columns, pk);
            fk.setParent( DbSchema.this );
        }
    }

    private void loadForeignKeys() throws Exception {
        ResultSet rs = null;
        try {
            int fkKeys = 0;

            for (DbTable pkTable:getTables()) {
                rs = getCatalog().getMetaData().getExportedKeys( null, this.getName(), pkTable.getName() );

                String prevTableName = null;
                int prevKeySeqNo = 0;
                RefDetails prevRefDetails = null;

                while (rs.next()) {
                    String fkTableName = rs.getString( "FKTABLE_NAME" );
                    String fkColName = rs.getString( "FKCOLUMN_NAME" );
                    String pkTableName = rs.getString( "PKTABLE_NAME" );
                    String pkColName = rs.getString( "PKCOLUMN_NAME" );
                    String fkName = rs.getString( "FK_NAME" );
                    String pkName = rs.getString( "PK_NAME" );
                    int keySeqNo = rs.getInt( "KEY_SEQ" )-1;

                    if (pkName==null || pkName.isEmpty())
                        pkName = getCatalog().getDefaultPrimaryKeyName();
                    
                    System.out.println( fkName+"("+fkTableName+"."+fkColName+") -> "+pkName+"("+pkTableName+"."+pkColName+") "+keySeqNo );
                    
                    DbTable fkTable = this.getTableByName( fkTableName );
                    if (fkTable==null)
                        throw new DbLoadException( "Unable to find referenced table '"+fkTableName+"' in schema "+this );

                    DbColumn fkCol = fkTable.getColumnByName( fkColName );
                    if (fkCol==null)
                        throw new DbLoadException( "Unable to find referenced column '"+fkColName+"' in table "+fkTable );

                    DbColumn pkCol = pkTable.getColumnByName( pkColName );
                    if (pkCol==null)
                        throw new DbLoadException( "Unable to find referenced column '"+pkColName+"' in table "+pkTable );

                    if (prevTableName==null || !prevTableName.equalsIgnoreCase( fkTableName ) ||
                            prevKeySeqNo>=keySeqNo)
                    {
                        if (prevRefDetails!=null)
                            prevRefDetails.create();

                        if (fkName==null)
                            fkName = String.format( "fk%04d", ++fkKeys );
                        
                        prevRefDetails = new RefDetails( fkName, pkName );
                    }

                    prevRefDetails.includeColumn( keySeqNo, pkCol, fkCol );

                    prevTableName = fkTableName;
                    prevKeySeqNo = keySeqNo;
                }

                if (prevRefDetails!=null)
                    prevRefDetails.create();

                rs.close();
            }
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }

    public void load() throws Exception  {
        this.loadTables();
        this.loadTableColumns();
        this.loadColumnPrimaryKeys();
        this.loadUniqueKeys();
        this.loadForeignKeys();
    }

    /*
    public void findRedundantForeignKeys() throws Exception {
        ResultSet rs = null;
        try {
            TreeSet<String> titles = new TreeSet<String>(new CaseInsensitiveStringComparator());

            for (DbTable pkTable:getTables()) {
                rs = getCatalog().getMetaData().getExportedKeys( null, this.getName(), pkTable.getName() );

                while (rs.next()) {
                    String fkTableName = rs.getString( "FKTABLE_NAME" );
                    String fkColName = rs.getString( "FKCOLUMN_NAME" );
                    String pkTableName = rs.getString( "PKTABLE_NAME" );
                    String pkColName = rs.getString( "PKCOLUMN_NAME" );
                    String fkName = rs.getString( "FK_NAME" );
                    String pkName = rs.getString( "PK_NAME" );
                    int keySeqNo = rs.getInt( "KEY_SEQ" )-1;

                    String title = fkTableName+"."+fkColName+"->"+pkTableName+"."+pkColName+"("+keySeqNo+")";

                    if (titles.contains( title )) {
                        System.out.println( title );
                    } else {
                        titles.add( title );
                    }
                }

                rs.close();
            }
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }
     */
    
}
