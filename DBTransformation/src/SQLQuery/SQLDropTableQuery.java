package SQLQuery;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLDropTableQuery extends SQLStructuresQuery {
    
    private static String QUERYFORMAT = "DROP TABLE %s";
    private HashMap<String,Table> tableSave; 
    
    public SQLDropTableQuery(String[] table, Connection con) {
        super(table, con);
        for(String s : table){
            try {
                SQLSelectQuery select = new SQLSelectQuery(new String[]{"information_schema.columns"}, getCon(), new String[]{"column_name","column_type"},"table_name='"+s+"'" );
                ResultSet rs = select.sqlQueryDo();
                Table t =new  Table();
                while(rs.next()){
                    String name = rs.getString("column_name");
                    String type = rs.getString("column_type");
                    t.addColumn(new Column(name, type));                
                }
                tableSave.put(s,t);
            } catch (SQLException ex) {
                Logger.getLogger(SQLDropTableQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /*
    select column_name,
       column_type 
  from information_schema.columns 
 where table_name='person';
    */
    
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
        for(String t : this.getTable()){
            SQLCreateTableQuery create = new SQLCreateTableQuery(t, getCon(), tableSave.get(t).toArray());
            create.sqlQueryDo();
        }
        return null;
    }
}
