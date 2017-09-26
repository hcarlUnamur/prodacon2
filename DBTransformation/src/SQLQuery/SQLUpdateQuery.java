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
public class SQLUpdateQuery extends SQLManipulationQuery{
    
    private static String QUERYFORMAT = "UPDATE %s SET %s WHERE %s;"; 
    private String[][] setValues;
    private String condValues;
    private ResultSet datasave ;

    public SQLUpdateQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLUpdateQuery(String table, Connection con, String[][] setValues, String condValues){
        super(new String[]{table}, con);
            this.setValues = setValues;
            this.condValues = condValues;
            //save data
            SQLSelectQuery select = new SQLSelectQuery(getTable()[0], getCon(), new String[]{"*"}, condValues);
            try {
                datasave = select.sqlQueryDo();
            } catch (SQLException ex) {
                Logger.getLogger(SQLUpdateQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
              
        
    }
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String setChange = StringTool.UpdateSetVal(setValues);
        String cond = condValues;
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        ResultSetMetaData meta = datasave.getMetaData();
        String[][] modif = new String[meta.getColumnCount()][2];
        int length = meta.getColumnCount();
        datasave.next();
        for(int i=0;i<meta.getColumnCount();i++){
            modif[i][0] = meta.getColumnName(i+1);
            modif[i][1] = datasave.getString(i+1);
        }
        SQLUpdateQuery update = new SQLUpdateQuery(getTable()[0], getCon(), modif, condValues);
        update.sqlQueryDo();
        return null;
    }
    
}
