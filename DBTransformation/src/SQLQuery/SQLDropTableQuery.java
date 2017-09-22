package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDropTableQuery extends SQLStructuresQuery {
    
    private static String QUERYFORMAT = "DROP TABLE %s";
    
    public SQLDropTableQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLDropTableQuery(String table, Connection con) {
        super(new String[]{table}, con);
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
     Statement stmt = this.getCon().createStatement();
        for (String s : this.getTable()){
            String query = String.format(QUERYFORMAT,s);
            stmt.executeUpdate(query);
        }
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
        /*
        //il faut en plus charger les info sur les column de la bd pour savoir la recrée (a faire lors du new)
        
        SQLCreateTableQuery create = new SQLCreateTableQuery(getTable(), getCon());
        create.sqlQueryDo();
        */
    }
}
