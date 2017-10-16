/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author carl_
 */
public class SQLTransactionQuery extends SQLQuery {
    
    private ArrayList<StringQueryGetter> queries;

    public ArrayList<StringQueryGetter> getQueries() {
        return queries;
    }

    public void setQueries(ArrayList<StringQueryGetter> queries) {
        this.queries = queries;
    }
    
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
        StringBuilder log = new StringBuilder();
        log.append("Transaction{ "+System.lineSeparator());
        getCon().setAutoCommit(false);
        Statement stmt = getCon().createStatement();
        for(StringQueryGetter q : queries){
            String sq = q.getStringSQLQueryDo();
            stmt.addBatch(sq);
            log.append(sq);
        }
        log.append(System.lineSeparator()+"}");
        System.out.println(log.toString());
        stmt.executeBatch();
        getCon().commit();
        getCon().setAutoCommit(true);
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        StringBuilder log = new StringBuilder();
        log.append("Transaction{ "+System.lineSeparator());
        getCon().setAutoCommit(false);
        Statement stmt = getCon().createStatement();
        for(int i = (queries.size()-1);i>=0;i--){
            String sq = queries.get(i).getStringSQLQueryUndo();
            System.out.println("xxxxxxxxxxxxx"+sq+" "+getCon().getAutoCommit());
            stmt.addBatch(sq);
            log.append(sq);
        }
        log.append(System.lineSeparator()+"}");
        System.out.println(log.toString());
        stmt.executeBatch();
        getCon().commit();
        getCon().setAutoCommit(true);
        return null;
    }
    
    
}
