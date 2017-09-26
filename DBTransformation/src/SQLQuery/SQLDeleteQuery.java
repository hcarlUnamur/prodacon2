/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thibaud
 */
public class SQLDeleteQuery extends SQLManipulationQuery{

    private static String QUERYFORMAT = "DELETE FROM %s WHERE (%s)"; 
    private String whereValues;
    private ResultSet datasave;
    
    public SQLDeleteQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLDeleteQuery(String table, Connection con, String whereValues){
        super(new String[]{table}, con);
        this.whereValues = whereValues;
        SQLSelectQuery select = new SQLSelectQuery(table, getCon(), new String[]{"*"}, whereValues);
        try {
            datasave = select.sqlQueryDo();
        } catch (SQLException ex) {
            Logger.getLogger(SQLDeleteQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];       
        String cond = whereValues;
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.         
        ResultSetMetaData meta = datasave.getMetaData();
        int length = meta.getColumnCount();
        String[] cols = new String[length];
        String[] values = new String[length];
        for (int i=0;i<length;i++){
            cols[i]= meta.getColumnClassName(i+1);
            values[i] = datasave.getNString(i);
        }
        SQLInsertQuery insert = new SQLInsertQuery(getTable()[0], getCon(), cols, values);
        insert.sqlQueryDo();
        return null;
    }
}
