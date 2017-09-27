package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class SQLCreateTableQuery extends SQLStructuresQuery{

    private static String QUERYFORMAT = "CREATE TABLE %s (%s)";
    private String[] column;
    private Table t;
    
    public SQLCreateTableQuery(String[] tableNames, Connection con) {
        super(tableNames, con);
    }
    public SQLCreateTableQuery(String tableName, Connection con, String[] column) {
        super(new String[]{tableName}, con);
        this.column = column;
        t = null;
    }
    
    public SQLCreateTableQuery(String tableName, Connection con, ArrayList<Column> column) {
        super(new String[]{tableName}, con);
        String[] columnArray = new String[column.size()];
        int i=0;
        for (Column col : column){
            columnArray[i]=col.getColumnName() + " " + col.getColumnType();
            i++;
        } 
        this.column = columnArray;
        t = null;
    }
    
    public SQLCreateTableQuery(Table table,Connection con){
        super(new String[]{table.getName()}, con);
        t=table;
        String[] column = new String[table.getTablecolumn().size()];
        int i=0;
        for (Column col : table.getTablecolumn()){
            column[i]=col.getColumnName() + " " + col.getColumnType();
            i++;
        }        
        this.column = column;              
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException{
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String query = String.format(QUERYFORMAT,s,StringTool.ArrayToString(this.column));
        stmt.executeUpdate(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        
        //if we used the constructor with
        if (t !=null){
            for(ForeignKey fk : t.getForeignKeys()){
                SQLAlterTableQuery addfk = SQLAlterTableQuery.CreateAddForeignKeyQuery(getTable()[0], getCon(), fk.getConstraintName(), new Column(fk.getForeingKeyColumn(), null),fk.getReferencedTableName(), fk.getReferencedColumn()); 
                addfk.sqlQueryDo();
            }
        SQLAlterTableQuery addPK = SQLAlterTableQuery.CreateAddPrimaryKeyQuery(t.getName(), getCon(), t.getPrimaryKey());    
        addPK.sqlQueryDo();
        }
        
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        SQLDropTableQuery drop = new SQLDropTableQuery(getTable(), getCon());
        drop.sqlQueryDo();
        return null;
    }
}
