package EasySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLDeleteQuery extends SQLManipulationQuery{

    private static String QUERYFORMAT = "DELETE FROM %s WHERE (%s)"; 
    private String whereValues;
    private ResultSet datasave;
    
    public SQLDeleteQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLDeleteQuery(String table, Connection con, String whereValues){
        super(new String[]{table}, con);
        this.whereValues = whereValues;        
    }

    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        SQLSelectQuery select = new SQLSelectQuery(getTable()[0], getCon(), new String[]{"*"}, whereValues);
        try {
            datasave = select.sqlQueryDo();
        } catch (SQLException ex) {
            Logger.getLogger(SQLDeleteQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];       
        String cond = whereValues;
        String query = String.format(QUERYFORMAT,s,cond);
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
        while(datasave.next()){
            ResultSetMetaData meta = datasave.getMetaData();
            int length = meta.getColumnCount();
            //System.out.println("++++++++++++++length : "+ length);
            String[] cols = new String[length];
            String[] values = new String[length];
            for (int i=0;i<length;i++){
                //System.out.println("+++++++++++ "+meta.getColumnName(i+1) +" / "+datasave.getString(i+1));
                cols[i]= meta.getColumnName(i+1);
                values[i] = datasave.getString(i+1);
            }
            SQLInsertQuery insert = new SQLInsertQuery(getTable()[0], getCon(), cols, values);
            insert.sqlQueryDo();
        }
        return null;
    }
}
