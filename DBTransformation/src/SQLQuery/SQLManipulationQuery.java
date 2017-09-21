/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;

/**
 *
 * @author thibaud
 */
public class SQLManipulationQuery extends SQLQuery{

    public SQLManipulationQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    
    
    @Override
    public void sqlQueryDo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sqlQueryUndo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
