package SQLQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class SQLInsertQuery extends SQLManipulationQuery {
    
    private static String QUERYFORMAT = "INSERT INTO %s (%s) VALUES (%s)";
    private String[] values;  
    private String[] columns;
    
    public SQLInsertQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    public SQLInsertQuery(String table, Connection con, String[] values) {
        super(new String[]{table}, con);
        this.values = values;
        this.columns = new String[]{""};
    }    
    
    public SQLInsertQuery(String table, Connection con, String[] columns, String[] values){
        super(new String[]{table}, con);
        this.columns = columns;
        this.values = values;
    }
    
    public SQLInsertQuery(String table, Connection con, String[][] setValues){
        super(new String[]{table}, con);
        ArrayList<String[]> ls = new ArrayList<>();
        this.values = ls.get(0);
        this.columns = ls.get(1);
    }
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String query = String.format(QUERYFORMAT,s,StringTool.ArrayToString(this.columns),StringTool.ArrayToStringInsert(this.values));
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
        StringBuilder where = new StringBuilder();
        for (int i=0;i<values.length;i++){
            where.append("AND " +columns[i] +" = \"" + values[i]+"\"");
        }
        where.delete(0, 3);
        SQLDeleteQuery del = new SQLDeleteQuery(getTable()[0], getCon(), where.toString());
        return null;
    }
}
