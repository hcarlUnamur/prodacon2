/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

/**
 *
 * @author thibaud
 */
public class SQLCreateTableQuery extends SQLStructuresQuery{

    public SQLCreateTableQuery(String[] table) {
        super(table);
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
