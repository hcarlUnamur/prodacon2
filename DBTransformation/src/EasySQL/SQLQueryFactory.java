package EasySQL;

import java.sql.SQLException;
import java.util.ArrayList;

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
    
    
    public SQLTransactionQuery creatTransactionQuery(){
        return new SQLTransactionQuery(getConn());
    }
    
    public SQLQueryFree createSQLCreateFreeQuery(SQLQueryType queryType,String query){
        return new SQLQueryFree(null, getConn(), queryType, query);
    }
    
    public SQLCreateTableQuery createSQLCreateTableQuery(String tableName,String[] columnsAndType){
        return new SQLCreateTableQuery(tableName, getConn(), columnsAndType);
    }
    
    public SQLCreateTableQuery createSQLCreateTableQuery(String tableName,ArrayList<Column> columns){
        return new SQLCreateTableQuery(tableName, getConn(), columns);
    }
    
    public SQLCreateTableQuery createSQLCreateTableQuery(Table table){
        return new SQLCreateTableQuery(table, getConn());
    }
      
    public SQLDropTableQuery createDropTableQuery (String tableName){
        return new SQLDropTableQuery(tableName, getConn());
    }
    
    public SQLDropTableQuery createDropTableQuery (Table table){
        return new SQLDropTableQuery(table.getName(), getConn());
    }
    
    public SQLInsertQuery createSQLInsertQuery (String table, String[] values){
        return new SQLInsertQuery(table, getConn(), values);
    }
    
    public SQLInsertQuery createSQLInsertQuery (String table, String[] columns, String[] values){
        return new SQLInsertQuery(table, getConn(), columns, values);
    }
    
    //public SQLInsertQuery creatSQLInsertQuery (String table, String[][] values){
    //    return new SQLInsertQuery(table, getConn(), values);
    //}
    
    public SQLSelectQuery createSQLSelectQuery (String[] table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    public SQLSelectQuery createSQLSelectQuery (String table,String[] column,String where){
        return new SQLSelectQuery(table, getConn(), column, where);
    }
    
    public SQLDeleteQuery createSQLDeleteQuery (String table, String whereValues){
        return new SQLDeleteQuery(table, getConn(), whereValues);
    }
    
    public SQLUpdateQuery createSQLUpdateQuery (String table, String[][] setValues, String whereValues){
        return new SQLUpdateQuery(table, getConn(), setValues, whereValues);
    }
    
    public SQLAlterTableQuery createSQLAltertableAddColumnQuery(String table, Column column){
        return SQLAlterTableQuery.CreateAddColumnQuery(table, getConn(), column);  
    }
    
    public SQLAlterTableQuery createSQLAlterDropColumnQuery(String table, Column column){
        return SQLAlterTableQuery.CreateDropColumnQuery(table, getConn(), column);
    }
;
    public SQLAlterTableQuery createSQLAlterModifyColumnTypeQuery(String table, Column column){
        return SQLAlterTableQuery.CreateModifyColumnTypeQuery(table, getConn(), column);
    }
    
    public SQLAlterTableQuery createSQLAlterAddForeignKeyQuery(String table,String constraintName, Column column , Table referentialTable){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), constraintName, column, referentialTable);
    }
    
    public SQLAlterTableQuery createSQLAlterAddForeignKeyQuery(String table,String constraintName, Column column , String referentialTable, String referentialColumn){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), constraintName, column, referentialTable, referentialColumn);
    }
    
    public SQLAlterTableQuery createSQLAlterAddForeignKeyQuery(String table,ForeignKey fk){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), fk.getConstraintName(), new Column(fk.getForeingKeyColumn(),null), fk.getReferencedTableName(), fk.getReferencedColumn());
    }
    
    public SQLAlterTableQuery createSQLAlterDropForeignKeyQuery(String table,String constraintName, Column column , Table referentialTable){
        return SQLAlterTableQuery.CreateDropForeignKeyQuery(table, getConn(), constraintName);
    }
    
    public SQLAlterTableQuery createSQLAlterDropForeignKeyQuery(String table,String constraintName){
        return SQLAlterTableQuery.CreateDropForeignKeyQuery(table, getConn(), constraintName);
    }
    
    public SQLAlterTableQuery createSQLAlterDropForeignKeyQuery(String table,ForeignKey fk){
        return SQLAlterTableQuery.CreateDropForeignKeyQuery(table, getConn(), fk.getConstraintName());
    }
  
    public SQLAlterTableQuery createSQLAlterAddPrimaryKeyQuery(String table,Column column){
        return SQLAlterTableQuery.CreateAddPrimaryKeyQuery(table, getConn(), column);
    }
    
    public SQLAlterTableQuery createSQLAlterAddPrimaryKeyQuery(String table,String columnName){
        return SQLAlterTableQuery.CreateAddPrimaryKeyQuery(table, getConn(), columnName);
    }
    
    public SQLAlterTableQuery createSQLAlterDropPrimaryKeyQuery(String table){
        return SQLAlterTableQuery.CreateDropPrimaryKeyQuery(table, getConn());
    }
    
    public SQLCreateTriggerQuery createSQLcreateTriggerQuery(ForeignKey fk, String logTable){
        return new SQLCreateTriggerQuery(getConn(), fk, logTable);
    }
    
}
