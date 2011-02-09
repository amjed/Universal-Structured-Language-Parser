/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.db_reflect;

import java.io.*;
import java.sql.*;

/**
 *
 * @author QB
 */
public class DbCommon {

   public static Connection connectTo( String driver,
                                        String dbUrl, String catalog,
                                        String userName, String password )
            throws SQLException,
                   ClassNotFoundException,
                   InstantiationException,
                   IllegalAccessException, IOException
    {
        Class.forName( driver ).newInstance();
        String connStr = dbUrl + "/" + catalog;
        Connection conn = DriverManager.getConnection( connStr, userName, password );
        return conn;
    }

    public static boolean isFixedSize( int type, String name, int size ) {
        switch (type) {
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.NCHAR:
                return false;
            case java.sql.Types.CHAR:
                return size <= 1;
            case java.sql.Types.DECIMAL:
            case java.sql.Types.BIGINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.BOOLEAN:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.BIT:
            case java.sql.Types.BINARY:
            case java.sql.Types.NUMERIC:
            case 2005:  // NTEXT
            case 2004:  //IMAGE
                return true;
            default:
                throw new RuntimeException( "Unsupported type '" + name + "' (" + type + "). "
                                            + "Please rectify code to support it." );
        }
    }
    static int knownTypes[] = new int[] {
        java.sql.Types.NVARCHAR,
        java.sql.Types.VARCHAR,
        java.sql.Types.NCHAR,
        java.sql.Types.DECIMAL,
        java.sql.Types.BIGINT,
        java.sql.Types.INTEGER,
        java.sql.Types.TINYINT,
        java.sql.Types.SMALLINT,
        java.sql.Types.BOOLEAN,
        java.sql.Types.FLOAT,
        java.sql.Types.DOUBLE,
        java.sql.Types.CHAR,
        java.sql.Types.DATE,
        java.sql.Types.TIME,
        java.sql.Types.TIMESTAMP,
        java.sql.Types.BIT,
        java.sql.Types.BINARY,
        java.sql.Types.NUMERIC
    };

    public static String type2Str( int type ) {
        switch (type) {
            case java.sql.Types.NVARCHAR:
                return "nvarchar";
            case java.sql.Types.VARCHAR:
                return "varchar";
            case java.sql.Types.NCHAR:
                return "nchar";
            case java.sql.Types.DECIMAL:
                return "decimal";
            case java.sql.Types.BIGINT:
                return "bigint";
            case java.sql.Types.INTEGER:
                return "int";
            case java.sql.Types.TINYINT:
                return "tinyint";
            case java.sql.Types.SMALLINT:
                return "smallint";
            case java.sql.Types.BOOLEAN:
                return "bool";
            case java.sql.Types.FLOAT:
                return "float";
            case java.sql.Types.DOUBLE:
                return "real";
            case java.sql.Types.CHAR:
                return "char";
            case java.sql.Types.DATE:
                return "date";
            case java.sql.Types.TIME:
                return "time";
            case java.sql.Types.TIMESTAMP:
                return "timestamp";
            case java.sql.Types.BIT:
                return "bit";
            case java.sql.Types.BINARY:
                return "binary";
            case java.sql.Types.NUMERIC:
                return "numeric";
            default:
                throw new RuntimeException( "Unsupported type (" + type + "). "
                                            + "Please rectify name to support it." );
        }
    }

    public static Integer str2Type( String name ) {
        for (int type : knownTypes) {
            if (type2Str( type ).equalsIgnoreCase( name )) {
                return type;
            }
        }
        return null;
    }
}
