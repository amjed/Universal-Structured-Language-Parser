package jellyfish.db_reflect.schema;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import jellyfish.db_reflect.DbCommon;
import java.io.*;
import java.sql.*;
import java.util.*;
import jellyfish.common.Common;

/**
 *
 * @author Xevia
 */
public class Test {

    private static final String settingsPropertyFile = "connection.properties";

    public static String catalogName;
    public static Connection conn;

    static {
        try {
            Properties properties = Common.loadPropertiesFromResource(Test.class.getPackage(), settingsPropertyFile);
            String driver = properties.getProperty("driver");
            String serverUrl = properties.getProperty("serverURL");
            String catalogStr = properties.getProperty("database");
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
        System.out.println( "catalog.isAllowsSchemasInDataManipulation = " + catalog.isAllowsSchemasInDataManipulation());
        DbSchema schema = new DbSchema( catalog, "schema" );

        System.out.println( "schema = " + schema );
        
        DbTable table1 = new DbTable( catalog, "a" );
        DbTable table2 = new DbTable( catalog, "table2" );

        table1.setParent( schema );
        table2.setParent( schema );

        DbColumn column1 = new DbColumn( catalog, "a",
                                         java.sql.Types.INTEGER, "int",
                                         4, false );
        DbColumn column2 = new DbColumn( catalog, "column2",
                                         java.sql.Types.INTEGER, "int",
                                         4, false );
        column1.setParent( table1 );
        column2.setParent( table1 );

        DbColumn column3 = new DbColumn( catalog, "column3",
                                         java.sql.Types.INTEGER, "int",
                                         4, false );
        DbColumn column4 = new DbColumn( catalog, "column4",
                                         java.sql.Types.INTEGER, "int",
                                         4, false );

        column3.setParent( table2 );
        column4.setParent( table2 );

        for (DbTable table:schema.getTables()) {
            System.out.println( "\t"+table );
            for (DbColumn column:table.getColumns()) {
                System.out.println( "\t\t"+column );
            }
        }

        String[] fullName = "a.a".split( "\\." );
        System.out.println( "fullName = "+Arrays.toString( fullName ) );
        DbItem foundItem = schema.findChildByFullName( fullName );
        System.out.println( "foundItem = " + foundItem );
        
        String[] partialName = "a".split( "\\." );
        System.out.println( "partialName = "+Arrays.toString( partialName ) );
        System.out.println( "found tables = " + schema.findItemsByPartialName( partialName, DbTable.class ) );
        System.out.println( "found columns = " + schema.findItemsByPartialName( partialName, DbColumn.class ) );
        System.out.println( "found all = " + schema.findItemsByPartialName( partialName, null ) );

    }

}
