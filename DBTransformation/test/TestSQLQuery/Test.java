

package TestSQLQuery;

import SQLQuery.Column;
import SQLQuery.ForeignKey;
import SQLQuery.SQLQueryFactory;
import SQLQuery.SQLUpdateQuery;
import SQLQuery.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Test {
    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
            
            sqlF.creatSQLCreateTableQuery("testUpdateTable", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testUpdateTable", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testUpdateTable", new String[]{"6", "String", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testUpdateTable", new String[]{"5", "Strang", "0"}).sqlQueryDo();
            //String where ="id=1 and name=\"Strong\" and trueFalse=\"1\" "; //new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}
            String where = "1=1";
            SQLUpdateQuery upd = sqlF.createSQLUpdateQuery("testUpdateTable", new String[][]{{"id", "6"},{"name", "Smith"}, {"trueFalse", "0"}}, where);
            upd.sqlQueryDo();
            upd.sqlQueryUndo();
            
            
            /*
            try{
            (sqlF.creatDropTableQuery("TestCarl2")).sqlQueryDo();
            (sqlF.creatDropTableQuery("TestCarl1")).sqlQueryDo();
            }   catch(SQLException e){}
            catch(Exception e){}
            
            ArrayList<Column> colList = new ArrayList();
            colList.add(new Column("c1", "int(11)"));
            colList.add(new Column("c2", "varchar(45)"));
            colList.add(new Column("c3", "varchar(45)"));
            
            Table t= new Table("TestCarl1", colList, new ArrayList<ForeignKey>(), "c1");
            try {
            sqlF.creatSQLCreateTableQuery(t).sqlQueryDo();
            } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ArrayList<Column> colList2 = new ArrayList();
            colList2.add(new Column("c1b", "int(11)"));
            colList2.add(new Column("c2b", "varchar(45)"));
            colList2.add(new Column("c3b", "varchar(45)"));
            
            ArrayList<ForeignKey> fkList = new ArrayList();
            fkList.add(new ForeignKey("testCarl1", "c1", "c1b", "const_name"));
            
            Table t2= new Table("TestCarl2", colList2, fkList, "c1b");
            try {
            sqlF.creatSQLCreateTableQuery(t2).sqlQueryDo();
            } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
            Table load = sqlF.loadTable("TestCarl2");
            System.out.println(load);
            } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
