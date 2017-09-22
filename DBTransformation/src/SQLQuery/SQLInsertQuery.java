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
public class SQLInsertQuery extends SQLManipulationQuery {
    
    private static String QUERYFORMAT = "INSERT INTO %s VALUES (%s)";
    private String[] values;    
    
    public SQLInsertQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLInsertQuery(String table, Connection con, String[] values) {
        super(new String[]{table}, con);
        this.values = values;
    }    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String query = String.format(QUERYFORMAT,s,StringTool.ArrayToStringInsert(this.values));
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
