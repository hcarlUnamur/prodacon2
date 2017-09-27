package SQLQuery;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLQueryFactory extends AbstractSQLQueryFactory {
    
    public SQLQueryFactory(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
    }

    public Table loadTable (String tableName) throws SQLException{
        return (new Table(tableName, this.getConn()));
    }
    
    public SQLCreateTableQuery creatSQLCreateTableQuery(String tableName,String[] columnsAndType){
        return new SQLCreateTableQuery(tableName, getConn(), columnsAndType);
    }
    
    public SQLCreateTableQuery creatSQLCreateTableQuery(String tableName,ArrayList<Column> columns){
        return new SQLCreateTableQuery(tableName, getConn(), columns);
    }
    
    public SQLCreateTableQuery creatSQLCreateTableQuery(Table table){
        return new SQLCreateTableQuery(table, getConn());
    }
      
    public SQLDropTableQuery creatDropTableQuery (String tableName){
        return new SQLDropTableQuery(tableName, getConn());
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
    
    public SQLInsertQuery creatSQLInsertQuery (String table, String[][] values){
        return new SQLInsertQuery(table, getConn(), values);
    }
    
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
    
    public SQLAlterTableQuery creatSQLAlterAddForeignKeyQuery(String table,String constraintName, Column column , String referentialTable, String referentialColumn){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), constraintName, column, referentialTable, referentialColumn);
    }
    
    public SQLAlterTableQuery creatSQLAlterAddForeignKeyQuery(String table,ForeignKey fk){
        return SQLAlterTableQuery.CreateAddForeignKeyQuery(table, getConn(), fk.getConstraintName(), new Column(fk.getForeingKeyColumn()," "), fk.getReferencedTableName(), fk.getReferencedColumn());
    }
    
    public SQLAlterTableQuery creatSQLAlterDropForeignKeyQuery(String table,String constraintName, Column column , Table referentialTable){
        return SQLAlterTableQuery.CreateDropForeignKeyQuery(table, getConn(), constraintName);
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
