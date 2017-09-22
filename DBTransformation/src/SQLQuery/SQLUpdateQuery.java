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
public class SQLUpdateQuery extends SQLManipulationQuery{
    
    private static String QUERYFORMAT = "UPDATE %s SET %s WHERE %s;"; 
    private String[] columns;
    private String[] values;
    private String[] newColumns;
    private String[] newValues;

    public SQLUpdateQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLUpdateQuery(String table, Connection con, String[] columns, String[] values, String[] newColumns, String[] newValues){
        super(new String[]{table}, con);
        this.columns = columns;
        this.values = values;
        this.newColumns = newColumns;
        this.newValues = newValues;
    }
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String setChange = StringTool.UpdateConcatColVal(newColumns, newValues);
        String cond = StringTool.DeleteConcatColVal(columns, values);
        String query = String.format(QUERYFORMAT,s,setChange, cond);
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
