/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;
import java.sql.*;
/**
 *
 * @author thibaud
 */
public abstract class SQLQuery {
    private String[] table;
    private Connection con;

    public String[] getTable() {
        return table;
    }

    public void setTable(String[] table) {
        this.table = table;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    
    
    public abstract void sqlQueryDo() throws SQLException ;    
    public abstract void sqlQueryUndo() throws SQLException ;

    public SQLQuery(String[] table, Connection con) {
        this.table = table;
        this.con = con;
    }  
    
}
