

package TestSQLQuery;

import ContextAnalyser.ContextAnalyser;
import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLCreateTableQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLUpdateQuery;
import EasySQL.Table;
import Transformation.MVMT;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Test {
    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
            
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int"));
            listCol1.add(new Column("1name", "varchar(45)"));
            listCol1.add(new Column("1trueFalse", "int(11)"));
            listCol1.add(new Column("1float", "float(3,2)"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "int"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "int"));
            listCol2.add(new Column("2float", "float(8,5)"));
            
            
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "int"));
            listCol3.add(new Column("3city", "varchar(10)"));
            listCol3.add(new Column("3reference", "int(3)"));
            
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.creatSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            
            ForeignKey fk1 = new ForeignKey("testTable1", "1id", "2reference", "testTable2", "FK1");
            ForeignKey fk2 = new ForeignKey("testTable1", "1id", "2city", "testTable2", "FK2");
            ForeignKey fk3 = new ForeignKey("testTable1", "1trueFalse", "3reference", "testTable3", "FK3");
            ForeignKey fk4 = new ForeignKey("testTable1", "1name", "3city", "testTable3", "FK4");
            ForeignKey fk5 = new ForeignKey("testTable1", "1float", "2float", "testTable2", "FK5");
            
            ArrayList<ForeignKey> lFK = new ArrayList<>();
            lFK.add(fk1);
            lFK.add(fk2);
            lFK.add(fk3);
            lFK.add(fk4);
            lFK.add(fk5);
            
            ContextAnalyser ca = new ContextAnalyser("localhost/mydb", "3306", "root", "root", lFK);
            
            ca.analyse();
            
            add1.sqlQueryUndo();
            add2.sqlQueryUndo();
            add3.sqlQueryUndo();
            /*
            MVMT m = new MVMT("localhost/mydb", "3306", "root", "root", "testMVMTTable2", fk, (Object s)->{return ((String) s).toUpperCase();});
            m.transfrom();   
            */
            
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
