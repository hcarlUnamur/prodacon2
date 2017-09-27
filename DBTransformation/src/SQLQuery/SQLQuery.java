package SQLQuery;

import java.sql.*;

public abstract class SQLQuery {
    private String[] table;
    private Connection con;

    public String[] getTable() {
        return table;
    }

    public void setTable(String[] table) {
        this.table = table;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    
    
    public abstract Object sqlQueryDo() throws SQLException ;    
    public abstract Object sqlQueryUndo() throws SQLException ;

    public SQLQuery(String[] table, Connection con) {
        this.table = table;
        this.con = con;
    }  
    
}
