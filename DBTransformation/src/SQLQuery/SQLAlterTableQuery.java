package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class SQLAlterTableQuery extends SQLStructuresQuery{

    private static String sqlQuery="ALTER TABLE %s %s %s;";
    private static String sqlQueryForeignKey="ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s;";
    private Alteration alteration;
    private Column column;
    private Table referentialTable;
    private String constraintName;

    public SQLAlterTableQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public void setReferentialTable(Table referentialTable) {
        this.referentialTable = referentialTable;
    }

    public void setAlteration(Alteration alteration) {
        this.alteration = alteration;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public SQLAlterTableQuery(String table, Connection con, Alteration alteration) {
        super(table, con);
        this.alteration=alteration;
    }
    
    public static SQLAlterTableQuery CreateAddColumnQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.AddColumn);
        out.setColumn(column);
        return out;
    }
    
    public static SQLAlterTableQuery CreateDropColumnQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropColumn);
        out.setColumn(column);
        return out;
    }
    
    public static SQLAlterTableQuery CreateDropColumnQuery(String table, Connection con, String columnName) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropColumn);
        out.setColumn(new Column(columnName, null));
        return out;
    }
    
    public static SQLAlterTableQuery CreateModifyColumnTypeQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.ModifyColumnType);
        out.setColumn(column);
        return out;
    }
    
    public static SQLAlterTableQuery CreateAddForeignKeyQuery(String table, Connection con,String constraintName, Column column , Table referentialTable) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.AddForeignKey);
        out.setColumn(column);
        out.setReferentialTable(referentialTable);
        out.setConstraintName(constraintName);
        return out;
    }
    
    public static SQLAlterTableQuery CreateAddForeignKeyQuery(String table, Connection con,String constraintName, Column column , String referentialTable, String referentialColumn) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.AddForeignKey);
        out.setColumn(column);
        ArrayList<Column> arr = new ArrayList<Column>();
        arr.add(new Column(referentialColumn, null));
        out.setReferentialTable(new Table(referentialTable, arr));        
        out.setConstraintName(constraintName);
        return out;
    }
    
    public static SQLAlterTableQuery CreateDroporeignKeyQuery(String table, Connection con,String constraintName) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropForeignKey);
        out.setConstraintName(constraintName);
        return out;
    }
    
    public static SQLAlterTableQuery CreateAddPrimaryKeyQuery(String table, Connection con,Column column){
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.AddPrimaryKey);
        out.setColumn(column);
        return out;
    }
    
    public static SQLAlterTableQuery CreateAddPrimaryKeyQuery(String table, Connection con,String columnName){
        return CreateAddPrimaryKeyQuery(table, con, new Column(columnName, null));
    }
    
    public static SQLAlterTableQuery CreateDropPrimaryKeyQuery(String table, Connection con){
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropPrimaryKey);
        return out;
    }
    
    private String createQuery(){
        String out="";
        switch(alteration){
            case AddColumn: 
                out=String.format(sqlQuery, getTable()[0], "ADD", column.toString());
                break;
            case DropColumn: 
                out=String.format(sqlQuery, getTable()[0], "DROP COLUMN", column.getColumnName());
                break;
            case ModifyColumnType: 
                out=String.format(sqlQuery, getTable()[0], "MODIFY COLUMN", column.toString());
                break;
            case AddForeignKey: 
                out=String.format(sqlQueryForeignKey,
                        getTable()[0],
                        constraintName,
                        column.getColumnName(),
                        referentialTable.getName()+'('+((referentialTable.getTablecolumn()).get(0)).getColumnName()+')'
                );
                break;
            case DropForeignKey: 
                out=String.format(sqlQuery, getTable()[0], "DROP FOREIGN KEY", constraintName,"");
                break;
            case AddPrimaryKey:
                out=String.format(sqlQuery, getTable()[0], "ADD PRIMARY KEY", "("+column.getColumnName()+")");
                System.out.println(out);
                break;
            case DropPrimaryKey:
                out=String.format(sqlQuery, getTable()[0], "DROP PRIMARY KEY", "");
                break;                
        }
        return out;
    }

   
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String query = createQuery();
        stmt.executeUpdate(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
