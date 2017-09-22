/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

/**
 *
 * @author carl_
 */
public class SQLQueryFactory extends AbstractSQLQueryFactory {

    public SQLQueryFactory(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
    }

    public SQLCreateTableQuery creatSQLCreateTableQuery(String[] table){
        return new SQLCreateTableQuery(table, this.getConn());
    }







    
    
}
