/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect.schema;

/**
 *
 * @author Xevia
 */
public class DbColumn extends DbItem<jellyfish.db_reflect.schema.DbTable> {

    private int sqlType;
    private String sqlTypeName;
    private int length;
    private boolean nullable;

    public DbColumn( DbCatalog catalog, String name, int sqlType, String sqlTypeName, int length,
                     boolean nullable ) {
        super( catalog, name );
        this.sqlType = sqlType;
        this.sqlTypeName = sqlTypeName;
        this.length = length;
        this.nullable = nullable;
    }

    
    public int getLength() {
        return length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public int getSqlType() {
        return sqlType;
    }

    public String getSqlTypeName() {
        return sqlTypeName;
    }

    public boolean isPartOfTablePK() {
        DbCandidateKey pk = this.getParent().getPrimaryKey();
        if (pk==null) return false;
        return pk.getColumns().indexOf( this )>=0;
    }

    public boolean isPartOfTableUniqueKey() {
        for (DbCandidateKey candidateKey:this.getParent().getUniqueKeys()) {
            if (candidateKey.getColumns().indexOf( this )>=0)
                return true;
        }
        return false;
    }

    public boolean isPartOfTableForeignKey() {
        for (DbForeignKey foreignKey:this.getParent().getForeignKeys()) {
            if (foreignKey.getForeignKeyTableColumns().indexOf( this )>=0)
                return true;
        }
        return false;
    }

}
