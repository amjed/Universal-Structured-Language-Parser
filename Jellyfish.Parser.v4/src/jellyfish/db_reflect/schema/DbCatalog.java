/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect.schema;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Xevia
 */
public class DbCatalog {

    private static final String[] sql92keywords = new String[] {
        "ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC",
        "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BIT_LENGTH", "BOTH",
        "BY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHAR_",
        "LENGTH", "CHARACTER_LENGTH", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN",
        "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT",
        "CORRESPONDING", "COUNT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME",
        "CURRENT_TIMESTAMP", "CURRENT_", "USER", "CURSOR", "DATE", "DAY", "DEALLOCATE", "DEC",
        "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE",
        "DESCRIPTOR", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP", "ELSE",
        "END", "END-EEC", "ESCAPE", "ECEPT", "ECEPTION", "EEC", "EECUTE", "EISTS", "ETERNAL",
        "ETRACT", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL",
        "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE",
        "IN", "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER",
        "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LAST",
        "LEADING", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOWER", "MATCH", "MA", "MIN", "MINUTE",
        "MODULE", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NET", "NO", "NOT", "NULL",
        "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER",
        "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "POSITION", "PRECISION", "PREPARE",
        "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL",
        "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "SCHEMA",
        "SCROLL", "SECOND", "SECTION", "SELECT", "SESSION", "SESSION_", "USER", "SET", "SIZE",
        "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTRING", "SUM",
        "SYSTEM_USER", "TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_", "HOUR",
        "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM",
        "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE",
        "VALUES", "VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE",
        "YEAR", "ZONE",
    };

    private Connection connection;
    private DatabaseMetaData metaData;
    private Set<String> sqlKeyWords;
    private String identifierQuoteStr;
    private String startQuoteStr;
    private String endQuoteStr;
    private boolean allowsSchemasInDataManipulation;

    public DbCatalog( Connection connection ) throws Exception {
        this.connection = connection;
        this.metaData = connection.getMetaData();
        {
            String dbSqlKeywordString = metaData.getSQLKeywords();
            String[] dbSqlKeywords = dbSqlKeywordString.split( "," );
            this.sqlKeyWords = new HashSet<String>(sql92keywords.length+dbSqlKeywords.length);
            for (String keyword:dbSqlKeywords) {
                this.sqlKeyWords.add( keyword.toLowerCase() );
            }
            for (String keyword:sql92keywords) {
                this.sqlKeyWords.add( keyword.toLowerCase() );
            }
        }
        
        identifierQuoteStr = metaData.getIdentifierQuoteString();
        if (identifierQuoteStr.equals( " " ) || identifierQuoteStr.isEmpty()) {
            startQuoteStr = "";
            endQuoteStr = "";
        } else {
            startQuoteStr = identifierQuoteStr.substring( 0, 1 );
            if (identifierQuoteStr.length()>1) {
                endQuoteStr = identifierQuoteStr.substring( 1, 2 );
            } else {
                endQuoteStr = startQuoteStr;
            }
        }
        
        allowsSchemasInDataManipulation = metaData.supportsSchemasInDataManipulation();
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isAllowsSchemasInDataManipulation() {
        return allowsSchemasInDataManipulation;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public DbSchema loadSchemas(String schemaName) throws Exception {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = null;
        DbSchema schema = null;
        try {
            rs = meta.getSchemas( null, schemaName );
            while (rs.next()) {
                String foundSchemaName = rs.getString( "TABLE_SCHEM" );
                schema = new DbSchema( this, foundSchemaName );
            }
            rs.close();
            return schema;
        } catch (Exception e) {
            if (rs != null) {
                rs.close();
            }
            throw e;
        }
    }

    public String simpleUnquote( String s ) {
        s = s.trim();
        if ((s.startsWith( startQuoteStr ) && s.endsWith( endQuoteStr ))) {
            return s.substring( 1, s.length() - 1 );
        } else {
            return s;
        }
    }

    public String simpleQuote( String s ) {
        if (s==null) return s;
        s = simpleUnquote( s );
        if (s.matches( ".*[^a-z_A-Z0-9].*" )
            || s.matches( "[0-9]+" ) ||
            sqlKeyWords.contains( s.toLowerCase() )) {
            return startQuoteStr + s + endQuoteStr;
        } else {
            return s;
        }
    }

    public String getDefaultPrimaryKeyName() {
        return "PRIMARY";
    }


}
