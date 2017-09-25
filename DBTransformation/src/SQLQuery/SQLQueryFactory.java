/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

/**
 *
 * @author carl_
 */
public class SQLQueryFactory extends AbstractSQLQueryFactory {
    
       public SQLQueryFactory(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
    }

    public SQLCreateTableQuery creatSQLCreateTableQuery(String table,String[] columns){
        return new SQLCreateTableQuery(table, getConn(), columns);
    }
    public SQLDropTableQuery creatDropTableQuery (String table){
        return new SQLDropTableQuery(table, getConn());
    }
    
    public SQLInsertQuery creatSQLInsertQuery (String table, String[] values){
        return new SQLInsertQuery(table, getConn(), values);
    }
    
    public SQLInsertQuery creatSQLInsertQuery (String table, String[] columns, String[] values){
        return new SQLInsertQuery(table, getConn(), columns, values);
    }
    
    public SQLSelectQuery createSQLSelectQuery (String[] table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    public SQLSelectQuery createSQLSelectQuery (String table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    
    public SQLDeleteQuery createSQLDeleteQuery (String table, String[][] whereValues){
        return new SQLDeleteQuery(table, getConn(), whereValues);
    }
    
    public SQLUpdateQuery createSQLUpdateQuery (String table, String[][] setValues, String[][] whereValues){
        return new SQLUpdateQuery(table, getConn(), setValues, whereValues);
    }

   
}
