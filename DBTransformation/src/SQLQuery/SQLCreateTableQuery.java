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
public class SQLCreateTableQuery extends SQLStructuresQuery{

    private static String QUERYFORMAT = "CREATE TABLE %s (%s)";
    private String[] column;
    
    public SQLCreateTableQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLCreateTableQuery(String table, Connection con, String[] column) {
        super(new String[]{table}, con);
        this.column = column;
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException{
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String query = String.format(QUERYFORMAT,s,StringTool.ArrayToString(this.column));
        stmt.executeUpdate(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        SQLDropTableQuery drop = new SQLDropTableQuery(getTable(), getCon());
        drop.sqlQueryDo();
        return null;
    }
}
