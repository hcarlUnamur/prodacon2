package SQLQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        if (where.replaceAll(" ","").equals("") || where==null){where="1=1";}
        Statement stmt = this.getCon().createStatement();
        String query = String.format(
                QUERYFORMAT,
                StringTool.ArrayToString(this.column),
                StringTool.ArrayToString(this.getTable()),
                where
        );
        System.out.println(query);
        ResultSet out = stmt.executeQuery(query);
        return out;
    }

    @Override
    public Object sqlQueryUndo()throws SQLException {
        throw new UnsupportedOperationException("Not supported.Hi,I'm your computer i'm very sorry but I don't know what can be the undo of a Select Sorry ^^"); //To change body of generated methods, choose Tools | Templates.
    }
}
