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

    public SQLCreateTableQuery creatSQLCreateTableQuery(String tableName,String[] columns){
        return new SQLCreateTableQuery(tableName, getConn(), columns);
    }
    
    public SQLCreateTableQuery creatSQLCreateTableQuery(Table table){
        String[] columns = new String[table.getTablecolumn().size()]
        int i = 0;
        for (Column c : table.getTablecolumn()){
            columns[i]=c.getColumnName() + " " + c.getColumnType();
            i++;
        }
        return new SQLCreateTableQuery(table.getName(), getConn(), columns);
    }
    
    public SQLDropTableQuery creatDropTableQuery (String tableName){
        return new SQLDropTableQuery(table, getConn());
    }
    
    public SQLDropTableQuery creatDropTableQuery (Table table){
        return new SQLDropTableQuery(table.getName(), getConn());
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
    
    public SQLAlterTableQuery creatSQLAltertableAddColumnQuery(String table, Column column){
        return SQLAlterTableQuery.CreateAddColumnQuery(table, getConn(), column);  
    }
    
    public SQLAlterTableQuery creatSQLAlterDropColumnQuery(String table, Column column){
        return SQLAlterTableQuery.CreateDropColumnQuery(table, getConn(), column);
    }
;
    public SQLAlterTableQuery creatSQLAlterModifyColumnTypeQuery(String table, Column column){
        return SQLAlterTableQuery.CreateModifyColumnTypeQuery(table, getConn(), column);
    }
    
    public SQLAlterTableQuery creatSQLAlterAddForeignKeyQuery(String table,String constraintName, Column column , Table referentialTable){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), constraintName, column, referentialTable);
    }
    
    public SQLAlterTableQuery creatSQLAlterDropForeignKeyQuery(String table,String constraintName, Column column , Table referentialTable){
        return SQLAlterTableQuery.CreateDroporeignKeyQuery(table, getConn(), constraintName);
    }
  
    public SQLAlterTableQuery creatSQLAlterAddPrimaryKeyQuery(String table,Column column){
        return SQLAlterTableQuery.CreateAddPrimaryKeyQuery(table, getConn(), column);
    }
    
    public SQLAlterTableQuery creatSQLAlterAddPrimaryKeyQuery(String table,String columnName){
        return SQLAlterTableQuery.CreateAddPrimaryKeyQuery(table, getConn(), columnName);
    }
    
    public SQLAlterTableQuery creatSQLAlterDropPrimaryKeyQuery(String table){
        return SQLAlterTableQuery.CreateDropPrimaryKeyQuery(table, getConn());
    }
    
}
