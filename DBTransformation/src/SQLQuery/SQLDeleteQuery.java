/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author thibaud
 */
public class SQLDeleteQuery extends SQLManipulationQuery{

    private static String QUERYFORMAT = "DELETE FROM %s WHERE (%s)"; 
    private String[][] whereValues;
    
    public SQLDeleteQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLDeleteQuery(String table, Connection con, String[][] whereValues){
        super(new String[]{table}, con);
        this.whereValues = whereValues;
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        
        String cond = StringTool.WhereToStringVal(whereValues);
        String query = String.format(QUERYFORMAT,s,cond);
        System.out.println(query);
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
