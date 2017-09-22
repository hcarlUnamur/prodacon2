/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author thibaud
 */
public class SQLSelectQuery extends SQLQuery {

    private static String QUERYFORMAT ="SELECT %s FROM %s WHERE %s ;";
    private String where;
    private String[] column;
    
    public SQLSelectQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLSelectQuery(String[] table, Connection con,String[] column,String where) {
        super(table, con);
        this.where =where;
        this.column=column;
    }
    public SQLSelectQuery(String table, Connection con,String[] column,String where){
        super(new String[]{table}, con);
        this.where =where;
        this.column=column;
    }

    
    
    @Override
    public ResultSet sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String query = String.format(
                QUERYFORMAT,
                StringTool.ArrayToString(this.column),
                StringTool.ArrayToString(this.getTable()),
                where
        );
        ResultSet out = stmt.executeQuery(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return out;
    }

    @Override
    public Object sqlQueryUndo()throws SQLException {
        throw new UnsupportedOperationException("Not supported. I don't know what can be the undo of a Select Sorry ^^"); //To change body of generated methods, choose Tools | Templates.
    }
}
