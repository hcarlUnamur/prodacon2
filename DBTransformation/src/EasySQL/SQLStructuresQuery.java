package EasySQL;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLStructuresQuery extends SQLQuery {

    public SQLStructuresQuery(String[] table, Connection con) {
        super(table, con);
    }
    public SQLStructuresQuery(String table, Connection con) {
        super(new String[]{table}, con);
    }
    
    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
