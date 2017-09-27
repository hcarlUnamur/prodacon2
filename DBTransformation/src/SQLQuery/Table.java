/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author carl_
 */
public class Table {

    private ArrayList<Column> Tablecolumn;
    private String name;
    private ArrayList<ForeignKey> foreignKeys;
    private String primaryKey;

    public ArrayList<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public Table( String name, ArrayList<Column> Tablecolumn, ArrayList<ForeignKey> foreignKeys, String primaryKey) {
        this.Tablecolumn = Tablecolumn;
        this.name = name;
        this.foreignKeys = foreignKeys;
        this.primaryKey = primaryKey;
    }

    public void setForeignKeys(ArrayList<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
    
    public void addForeignKey(ForeignKey fk){
        foreignKeys.add(fk);
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Table( String name, ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
        this.name = name;
        foreignKeys = new ArrayList<ForeignKey>();
        primaryKey=null;
    }
    
    public  Table(String name,Connection con) throws SQLException{
        foreignKeys = new ArrayList<ForeignKey>();
        Tablecolumn = new ArrayList<Column>();
        this.name = name;
        //create Tablecolumn
        SQLSelectQuery select = new SQLSelectQuery(new String[]{"information_schema.columns"},con, new String[]{"column_name","column_type"},"table_name='"+name+"'" );
        ResultSet rs = select.sqlQueryDo();
        while(rs.next()){
                String colName = rs.getString("column_name");
                String colType = rs.getString("column_type");
                this.addColumn(new Column(colName, colType));             
            }
        rs.close();
        //creat foreignkeys
        SQLSelectQuery select2 = new SQLSelectQuery(
                        new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                        con,
                        new String[]{"TABLE_NAME,COLUMN_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
                        "TABLE_NAME = '"+name+"' "
                );
        ResultSet resultfk = select2.sqlQueryDo();
        while(resultfk.next()){
            foreignKeys.add(new ForeignKey(resultfk.getString("REFERENCED_TABLE_NAME"), resultfk.getString("REFERENCED_COLUMN_NAME"), resultfk.getString("COLUMN_NAME"),resultfk.getString("CONSTRAINT_NAME")));
        }
        //load PrimaryKey
        SQLSelectQuery select3 = new SQLSelectQuery(
                        new String[]{"INFORMATION_SCHEMA.COLUMNS"},
                        con,
                        new String[]{"COLUMN_NAME"},
                        "TABLE_NAME = '"+name+"' AND COLUMN_KEY = 'PRI'"
                );
        primaryKey=null;
        ResultSet pri = select3.sqlQueryDo();
        while(pri.next()){
            primaryKey = pri.getString("COLUMN_NAME");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public Table() {
        this.Tablecolumn = new ArrayList<Column>();
    }
    
    public Table(ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
    }

    public ArrayList<Column> getTablecolumn() {
        return Tablecolumn;
    }

    public void setTablecolumn(ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
    }
    
    public void addColumn(Column col){
        this.Tablecolumn.add(col);
    }
    
    public String[] toArray(){
        ArrayList<String> out = new ArrayList<String>();
        for( Column col : Tablecolumn){
            out.add(col.getColumnName()+" "+col.getColumnType());
        }
        return out.toArray(new String[out.size()]);
    }
    
}
