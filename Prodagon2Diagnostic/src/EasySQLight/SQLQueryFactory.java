package EasySQLight;

import java.sql.SQLException;


public class SQLQueryFactory extends AbstractSQLQueryFactory {
    
    public SQLQueryFactory(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
    }
    public SQLQueryFactory(String dataBaseHostName, String dbName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        super(dataBaseHostName, dbName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
    }
    public Table loadTable (String tableName) throws SQLException{
        return (new Table(tableName, this.getConn()));
    }
    public SQLSelectQuery createSQLSelectQuery (String[] table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    public SQLSelectQuery createSQLSelectQuery (String table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    public SQLQueryFree createSQLCreateFreeQuery(SQLQueryType queryType,String query){
        return new SQLQueryFree(null, getConn(), queryType, query);
    }
}
