package EasySQL;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLDropTableQuery extends SQLStructuresQuery {
    
    private static String QUERYFORMAT = "DROP TABLE %s";
    private Table tableSave; 
    //private HashMap<String,Table> tableSave; //save to be able to undo a table drop and recreat table with it column and type 
    private ResultSet datasave;
    
    private void makeDataSave(){
        SQLSelectQuery select = new SQLSelectQuery(getTable()[0], getCon(), new String[]{"*"},null );
        try {
            datasave = select.sqlQueryDo();
        } catch (SQLException ex) {
            Logger.getLogger(SQLDropTableQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    private void creatTableSave(){
        tableSave = new HashMap<String,Table>();
        for(String s : getTable()){
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
    };
    */
    public SQLDropTableQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLDropTableQuery(String table, Connection con) {
        super(new String[]{table}, con);
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        tableSave = new Table(getTable()[0],getCon());
        makeDataSave();
        Statement stmt = this.getCon().createStatement();
        for (String s : this.getTable()){
            String query = String.format(QUERYFORMAT,s);
            System.out.println(query);
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
        
        SQLCreateTableQuery recreat = new SQLCreateTableQuery(tableSave, getCon());
        recreat.sqlQueryDo();
        /*
        for(String t : this.getTable()){
            SQLCreateTableQuery create = new SQLCreateTableQuery(t, getCon(), tableSave.get(t).toArray());
            create.sqlQueryDo();
        }
        */
        while(datasave.next()){
            int length = datasave.getMetaData().getColumnCount();
            String[] values = new String[length];
            for(int i=0;i<length;i++){
                values[i]= datasave.getString(i+1);
            }            
            SQLInsertQuery insert = new SQLInsertQuery(getTable()[0], getCon(), values);
            insert.sqlQueryDo();
        }
        
        return null;
    }
}
