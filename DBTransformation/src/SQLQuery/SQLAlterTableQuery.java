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

    
    public SQLAlterTableQuery(String[] table, Connection con) {
        super(table, con);
    }

    public SQLAlterTableQuery(String table, Connection con, Alteration alteration) {
        super(table, con);
        this.alteration=alteration;
    }
    
    public SQLAlterTableQuery CreateAddColumnQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.AddColumn);
        out.setColumn(column);
        return out;
    }
    
    public SQLAlterTableQuery CreateDropColumnQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropColumn);
        out.setColumn(column);
        return out;
    }
    
    public SQLAlterTableQuery CreateDropColumnQuery(String table, Connection con, String columnName) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.DropColumn);
        out.setColumn(column);
        return out;
    }
    
    public SQLAlterTableQuery CreateModifyColumnTypeQuery(String table, Connection con, Column column) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.ModifyColumnType);
        out.setColumn(column);
        return out;
    }
    
    public SQLAlterTableQuery CreateAddForeignKeyQuery(String table, Connection con,String constraintName, Column column , Table referentialTable) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.ModifyColumnType);
        out.setColumn(column);
        out.setReferentialTable(referentialTable);
        out.setConstraintName(constraintName);
        return out;
    }
    
    public SQLAlterTableQuery CreateAddForeignKeyQuery(String table, Connection con,String constraintName, Column column , String referentialTable, String referentialColumn) {
        SQLAlterTableQuery out = new SQLAlterTableQuery(table, con, Alteration.ModifyColumnType);
        out.setColumn(column);
        out.setReferentialTable(new Table(referentialTable, (new ArrayList<Column>().add(new Column(referentialColumn, null)))));        
        out.setConstraintName(constraintName);
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
