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
    
    public abstract void sqlQueryDo();    
    public abstract void sqlQueryUndo();

    public SQLQuery(String[] table, Connection con) {
        this.table = table;
        this.con = con;
    }  
    
}
