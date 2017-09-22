/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thibaud
 */
public class SQLCreateTableQuery extends SQLStructuresQuery{

    private static String QUERYFORMAT = "CREATE DATABASE %s";
    
    public SQLCreateTableQuery(String[] table, Connection con) {
        super(table, con);
    }

    
    
    @Override
    public void sqlQueryDo() throws SQLException{

        Statement stmt = this.getCon().createStatement();
        for (String s : this.getTable()){
            String query = String.format(QUERYFORMAT,s);
            stmt.executeUpdate(query);
        }
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
       
    }

    @Override
    public void sqlQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
