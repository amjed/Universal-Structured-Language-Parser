/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect;

import jellyfish.db_reflect.DbCommon;
import jellyfish.db_reflect.schema.DbCatalog;
import jellyfish.db_reflect.schema.DbSchema;
import jellyfish.db_reflect.schema.DbTable;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Xevia
 */
public class Test {

    private static final String settingsPropertyFile = "settings.properties";

    public static String catalogName;
    public static String schemaName;
    public static Connection conn;

    static {
        try {
            File f = new File(settingsPropertyFile);
            Properties properties = new Properties();
            properties.load(new FileInputStream(f));
            String driver = properties.getProperty("driver");
            String serverUrl = properties.getProperty("serverURL");
            String catalogStr = properties.getProperty("database");
            schemaName = properties.getProperty("schema");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            conn = DbCommon.connectTo(driver, serverUrl, catalogStr, username, password);
            System.out.println("\tConnected to: "+catalogStr);
            catalogName = catalogStr;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        if (conn==null)
            return;
        
        DbCatalog catalog = new DbCatalog( conn );
        DbSchema schema = new DbSchema( catalog, schemaName );

        System.out.println( "schema = " + schema );

        schema.load();
        
//        System.out.println( "\t\ttables:" );
//        for (DbTable table:schema.getTables()) {
//            System.out.println( "\t\t"+table );
//            for (DbColumn column:table.getColumns()) {
//                System.out.println( "\t\t\t"+column+" "+(column.isPartOfTableUniqueKey()?"U":" ")+(column.isPartOfTablePK()?"P":" ")+(column.isPartOfTableForeignKey()?"F":" ") );
//            }
//            for (DbCandidateKey key:table.getCandidateKeys()) {
//                System.out.println( "\t\t\t"+key+" "+(key.isPrimary()?"P":" ") );
//            }
//            for (DbForeignKey key:table.getForeignKeys()) {
//                System.out.println( "\t\t\t"+key);
//            }
//        }

        List<DbTable> tables = new ArrayList<DbTable>(schema.getTables());
        Collections.sort( tables, new Comparator<DbTable>() {
            public int compare( DbTable o1, DbTable o2 ) {
                boolean o1_o2 = o1.getTablesReferedTo().contains( o2 );
                boolean o2_o1 = o2.getTablesReferedTo().contains( o1 );
                if (o1_o2 && o2_o1) {
                    return 0;
                } else {
                    if (!o1_o2 && !o2_o1)
                        return 0;
                    else
                        if (o1_o2)
                            return -1;
                        else
                            return 1;
                }
            }
        } );
        for (DbTable table:tables) {
            System.out.println( table );
            System.out.println( "\t"+table.getTablesReferedTo() );
        }

        /*
        int index;

        index = 0;
        for (DbTable table:schema.getTables()) {
            StringBuilder theoryBuilder = new StringBuilder();
            theoryBuilder.append("isTable( ");
            theoryBuilder.append(table.getName().toLowerCase());
            theoryBuilder.append(", ");
            theoryBuilder.append(++index);
            theoryBuilder.append(" ).");
            System.out.println( theoryBuilder.toString() );
        }
        System.out.println(  );

        index = 0;
        for (DbTable table:schema.getTables()) {
            for (DbColumn column:table.getColumns()) {
                StringBuilder theoryBuilder = new StringBuilder();
                theoryBuilder.append("isFieldOf( ");
                theoryBuilder.append(table.getName().toLowerCase());
                theoryBuilder.append(", ");
                theoryBuilder.append(column.getName().toLowerCase());
                theoryBuilder.append(", ");
                theoryBuilder.append(++index);
                theoryBuilder.append(" ).");
                System.out.println( theoryBuilder.toString() );
            }
        }
        System.out.println(  );
        
        index = 0;
        for (DbTable table:schema.getTables()) {
            if (table.getPrimaryKey()!=null)
                for (DbColumn column:table.getPrimaryKey().getColumns()) {
                    StringBuilder theoryBuilder = new StringBuilder();
                    theoryBuilder.append("isPrimaryKeyOf( ");
                    theoryBuilder.append(table.getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(column.getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(++index);
                    theoryBuilder.append(" ).");
                    System.out.println( theoryBuilder.toString() );
                }
        }
        System.out.println(  );
        
        for (DbTable table:schema.getTables()) {
            for (DbForeignKey fk:table.getForeignKeys()) {
                for (DbForeignKey.KeyPair colPair:fk.getColumnPairing()) {
                    StringBuilder theoryBuilder = new StringBuilder();
                    theoryBuilder.append("isForeignKey( ");
                    theoryBuilder.append(fk.getName());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(colPair.getForeignKeyTableColumn().getParent().getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(colPair.getForeignKeyTableColumn().getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(colPair.getPrimaryKeyTableColumn().getParent().getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(colPair.getPrimaryKeyTableColumn().getName().toLowerCase());
                    theoryBuilder.append(", ");
                    theoryBuilder.append(++index);
                    theoryBuilder.append(" ).");
                    System.out.println( theoryBuilder.toString() );
                }
            }
        }
        System.out.println(  );
        */

    }

}
