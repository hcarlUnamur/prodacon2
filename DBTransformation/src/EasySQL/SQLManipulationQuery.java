package EasySQL;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLManipulationQuery extends SQLQuery{

    public SQLManipulationQuery(String[] table, Connection con) {
        super(table, con);
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
