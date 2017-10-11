/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author carl_
 */
public class SQLQueryFree extends SQLQuery {
    private SQLQueryType type;
    private String query;
        
    @Deprecated
    public SQLQueryFree(String[] table, Connection con) {
        super(table, con);
    }
    
     public SQLQueryFree(String[] table, Connection con,SQLQueryType type,String query) {
        super(table, con);
        this.type=type;
        this.query=query;
    }

    public SQLQueryType getType() {
        return type;
    }

    public void setType(SQLQueryType type) {
        this.type = type;
    }
    
    @Override
    public ResultSet sqlQueryDo() throws SQLException {
        if(type.equals(null)){throw new EasySQL.Exception.IncompleteClassRuntimeException("attribut SQLQueryType type missing for apperation sqlQueryDo");}
        Statement stmt = this.getCon().createStatement();
        if(type.equals(SQLQueryType.Updater)){            
            System.out.println(query);
            stmt.executeUpdate(query);
            try{
             if(stmt!=null)
                stmt.close();
            }catch(SQLException se2){}
            return null;
        }
        if(type.equals(SQLQueryType.Getter)){
            ResultSet out = stmt.executeQuery(query);
            return out;
        }
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported eppération for SqlQueryFree.");
    }
    
}
