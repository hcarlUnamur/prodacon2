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
public abstract class SQLQuery {
    public String[] table;
    
    public abstract void sqlQueryDo();    
    public abstract void sqlQueryUndo();

    public SQLQuery(String[] table) {
        this.table = table;
    }
    
    
}
