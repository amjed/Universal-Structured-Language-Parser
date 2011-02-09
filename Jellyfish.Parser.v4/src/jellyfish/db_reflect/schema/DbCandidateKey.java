/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Xevia
 */
public class DbCandidateKey extends DbItem<DbTable> {

    private DbTable table;
    private List<DbColumn> columns;

    public DbCandidateKey( DbCatalog catalog, String name, DbTable table, List<DbColumn> columns, boolean isPrimary ) {
        super( catalog, name );
        this.table = table;
        this.columns = new ArrayList<DbColumn>(columns);
        setParent( table );

        for (DbColumn column:columns)
            if (!column.getParent().equals( table )) {
                throw new jellyfish.db_reflect.DbLoadException("The column "+column+" in the candidate key "+this+" isn't a member of the key's table "+table);
            }

        registerKey( isPrimary );
    }

    private void registerKey(boolean isPrimary) {
        if (isPrimary) {
            if (table.getPrimaryKey()!=null) {
                throw new jellyfish.db_reflect.DbLoadException("The table "+table+" already has a primary key while trying to register primary key "+this);
            }
            table.setPrimaryKey( this );
        } else {
            table.addUniqueKey( this );
        }
    }

    public List<DbColumn> getColumns() {
        return Collections.unmodifiableList( columns );
    }

    public DbTable getTable() {
        return table;
    }

    public boolean isPrimary() {
        return table!=null && table.getPrimaryKey()==this;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append( this.getName() ).append( "{ " ).append( table ).append( " ( " );
        for (int i=0; i<columns.size(); ++i) {
            if (i!=0) bldr.append( ", " );
            bldr.append( columns.get( i ) );
        }
        bldr.append( ") }");
        return bldr.toString();
    }

    

}
