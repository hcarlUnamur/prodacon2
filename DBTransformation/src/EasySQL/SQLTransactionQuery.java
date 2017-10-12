/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author carl_
 */
public class SQLTransactionQuery extends SQLQuery {
    
    private ArrayList<StringQueryGetter> queries;
    
    @Deprecated
    public SQLTransactionQuery(String[] table, Connection con) {
        super(table, con);
        queries = new ArrayList();
    }
    
    public SQLTransactionQuery(Connection con) {
        super(null, con);
        queries = new ArrayList();
    }

    public SQLTransactionQuery(String[] table, Connection con, ArrayList<StringQueryGetter> queries) {
        super(table, con);
        this.queries = queries;
    }
    
    public void addQuery(StringQueryGetter query){
        queries.add(query);
    }
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        StringBuilder req = new StringBuilder();
        req.append(" START TRANSACTION; ");
        for(StringQueryGetter q : queries){
            req.append(q.getStringSQLQueryDo());
        }
        req.append(" COMMIT; ");
        SQLQueryFree free = new SQLQueryFree( getCon(), SQLQueryType.Updater, req.toString());
        free.sqlQueryDo();
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        StringBuilder req = new StringBuilder();
        req.append(" SET autocommit=0; ");
        req.append(" START TRANSACTION; ");
        for(int i = (queries.size()-1);i>=0;i--){
            req.append(queries.get(i).getStringSQLQueryUndo());
        }
        req.append(" COMMIT; ");
        req.append("SET autocommit=1;");
        SQLQueryFree free = new SQLQueryFree( getCon(), SQLQueryType.Updater, req.toString());
        free.sqlQueryDo();
        return null;
    }
    
    
}
