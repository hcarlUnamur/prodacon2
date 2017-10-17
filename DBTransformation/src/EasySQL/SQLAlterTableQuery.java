package EasySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLAlterTableQuery extends SQLStructuresQuery implements StringQueryGetter{

    private static String sqlQuery="ALTER TABLE %s %s %s;";
    private static String sqlQueryForeignKey="ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s;";
    private Alteration alteration;
    private Column column;
    private Table referentialTable;
    private String constraintName;
    private ResultSet datasave;

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
    
    public static SQLAlterTableQuery CreateDropForeignKeyQuery(String table, Connection con,String constraintName) {
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
    
    
    private String createQueryAndSaveData(){
        String out="";
        switch(alteration){
            case AddColumn: 
                out=String.format(sqlQuery, getTable()[0], "ADD", column.toString());
                break;
            case DropColumn:
                SQLSelectQuery select = new SQLSelectQuery(new String[]{"information_schema.columns"}, getCon(), new String[]{"column_name","column_type"},"table_name='"+getTable()[0]+"' AND column_name='"+column.getColumnName()+"'");       
                try {
                    datasave = select.sqlQueryDo();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                out=String.format(sqlQuery, getTable()[0], "DROP COLUMN", column.getColumnName());
                break;
            case ModifyColumnType:
                SQLSelectQuery select2 = new SQLSelectQuery(new String[]{"information_schema.columns"}, getCon(), new String[]{"column_name","column_type"},"table_name='"+getTable()[0]+"' AND column_name='"+column.getColumnName()+"'");       
                try {
                    datasave = select2.sqlQueryDo();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                /*
                SELECT 
                    TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME
                FROM
                    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
                WHERE
                    REFERENCED_TABLE_NAME = 'x' AND CONSTRAINT_NAME='y'   ;
                */
                SQLSelectQuery select3 = new SQLSelectQuery(
                        new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                        getCon(),
                        new String[]{"TABLE_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
                        "CONSTRAINT_NAME='"+constraintName+"'"
                ); 
        
                try {
                    datasave = select3.sqlQueryDo();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
   
                out=String.format(sqlQuery, getTable()[0], "DROP FOREIGN KEY", constraintName,"");
                break;
            case AddPrimaryKey:
                out=String.format(sqlQuery, getTable()[0], "ADD PRIMARY KEY", "("+column.getColumnName()+")");
                break;
            case DropPrimaryKey:
                /*
                SELECT
                    COLUMN_NAME
                FROM 
                    INFORMATION_SCHEMA.COLUMNS
                WHERE
                    TABLE_NAME = 'cours'
                    AND COLUMN_KEY = 'PRI';
                */
                SQLSelectQuery select4 = new SQLSelectQuery(
                        new String[]{"INFORMATION_SCHEMA.COLUMNS"},
                        getCon(),
                        new String[]{"COLUMN_NAME"},
                        "TABLE_NAME = '"+getTable()[0]+"' AND COLUMN_KEY = 'PRI'"
                ); 
        
                try {
                    datasave = select4.sqlQueryDo();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                out=String.format(sqlQuery, getTable()[0], "DROP PRIMARY KEY", "");
                break;                
        }
        return out;
    }

   
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String query = createQueryAndSaveData();
        System.out.println(query);
        stmt.executeUpdate(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    
    // memo l'instance des sauvegardes des états utilisés lors du undo est raliser lors du sqlQuerydo() et non pas au moment du la création de l'objet
    @Override
    public Object sqlQueryUndo() throws SQLException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        switch(alteration){
            case AddColumn: 
                SQLAlterTableQuery dropC = CreateDropColumnQuery(getTable()[0], getCon(), column);
                dropC.sqlQueryDo();
                break;
            case DropColumn: 
                datasave.next();
                SQLAlterTableQuery addC = CreateAddColumnQuery(getTable()[0], getCon(),new Column(datasave.getString(1), datasave.getString(2)));
                addC.sqlQueryDo();
                break;
            case ModifyColumnType: 
                datasave.next();
                SQLAlterTableQuery mod = CreateModifyColumnTypeQuery(getTable()[0], getCon(),new Column(datasave.getString(1), datasave.getString(2)));
                mod.sqlQueryDo();
                break;
            case AddForeignKey: 
                SQLAlterTableQuery dropfk = CreateDropForeignKeyQuery(getTable()[0], getCon(), constraintName);
                dropfk.sqlQueryDo();
                break;
            case DropForeignKey: 
                //TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME
                datasave.next();
                SQLAlterTableQuery addfk = CreateAddForeignKeyQuery(getTable()[0], getCon(), constraintName, new Column(datasave.getString("COLUMN_NAME"),null), datasave.getString("REFERENCED_TABLE_NAME"), datasave.getString("REFERENCED_COLUMN_NAME"));
                addfk.sqlQueryDo();
                break;
            case AddPrimaryKey:
                SQLAlterTableQuery dropPK = CreateDropPrimaryKeyQuery(getTable()[0], getCon());
                dropPK.sqlQueryDo();
                break;
            case DropPrimaryKey:
                SQLAlterTableQuery addpk = CreateAddPrimaryKeyQuery(getTable()[0], getCon(), datasave.getString("COLUMN_NAME"));
                addpk.sqlQueryDo();
                break;                
        }
        return null;
    }
    
    public String getStringSQLQueryDo() throws SQLException{
        Statement stmt = this.getCon().createStatement();
        String query = createQueryAndSaveData();
        return query;
    
    }
    
    public String getStringSQLQueryUndo() throws SQLException{
        String out ="";   
        switch(alteration){
            case AddColumn: 
                SQLAlterTableQuery dropC = CreateDropColumnQuery(getTable()[0], getCon(), column);
                out=dropC.getStringSQLQueryDo();
                break;
            case DropColumn: 
                datasave.next();
                SQLAlterTableQuery addC = CreateAddColumnQuery(getTable()[0], getCon(),new Column(datasave.getString(1), datasave.getString(2)));
                out=addC.getStringSQLQueryDo();
                break;
            case ModifyColumnType: 
                datasave.next();
                SQLAlterTableQuery mod = CreateModifyColumnTypeQuery(getTable()[0], getCon(),new Column(datasave.getString(1), datasave.getString(2)));
                out=mod.getStringSQLQueryDo();
                break;
            case AddForeignKey: 
                SQLAlterTableQuery dropfk = CreateDropForeignKeyQuery(getTable()[0], getCon(), constraintName);
                out=dropfk.getStringSQLQueryDo();
                break;
            case DropForeignKey: 
                //TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME
                datasave.next();
                SQLAlterTableQuery addfk = CreateAddForeignKeyQuery(getTable()[0], getCon(), constraintName, new Column(datasave.getString("COLUMN_NAME"),null), datasave.getString("REFERENCED_TABLE_NAME"), datasave.getString("REFERENCED_COLUMN_NAME"));
                out =addfk.getStringSQLQueryDo();
                break;
            case AddPrimaryKey:
                SQLAlterTableQuery dropPK = CreateDropPrimaryKeyQuery(getTable()[0], getCon());
                out = dropPK.getStringSQLQueryDo();
                break;
            case DropPrimaryKey:
                datasave.next();
                SQLAlterTableQuery addpk = CreateAddPrimaryKeyQuery(getTable()[0], getCon(), datasave.getString("COLUMN_NAME"));
                out = addpk.getStringSQLQueryDo();
                break;                
        }
        return out;
    }
    
}
