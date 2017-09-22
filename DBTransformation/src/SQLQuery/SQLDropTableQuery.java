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
    public void sqlQueryDo() throws SQLException {
     Statement stmt = this.getCon().createStatement();
        for (String s : this.getTable()){
            String query = String.format(QUERYFORMAT,s);
            System.out.println("query execution = " + query);
            stmt.executeUpdate(query);
        }
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
    }

    @Override
    public void sqlQueryUndo() throws SQLException {
        SQLCreateTableQuery create = new SQLCreateTableQuery(getTable(), getCon());
        create.sqlQueryDo();
    }
}
