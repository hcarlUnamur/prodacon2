

package TestSQLQuery;

import ContextAnalyser.ContextAnalyser;
import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLCreateTableQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryType;
import EasySQL.SQLTransactionQuery;
import EasySQL.SQLUpdateQuery;
import EasySQL.Table;
import Transformation.MVMT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Test {
    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
             
            
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "varchar(40)"));
            listCol1.add(new Column("1name", "varchar(45)"));
            listCol1.add(new Column("1trueFalse", "int(11)"));
            listCol1.add(new Column("1float", "float(3,2)"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "int"));
            listCol2.add(new Column("2city", "varchar(10)"));
            listCol2.add(new Column("2reference", "int signed"));
            listCol2.add(new Column("2float", "float(8,2)"));
          
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "varchar(40)"));
            listCol3.add(new Column("3city", "varchar(40)"));
            listCol3.add(new Column("3reference", "int unsigned"));
            listCol3.add(new Column("3float", "float(3,3)"));
            
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable2", new String[]{"1", "Strong", "1", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"Strong", "Strong", "1", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"Strang", "23", "6", "7"}).sqlQueryDo();
            //sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "START TRANSACTION; ALTER TABLE testTable2 MODIFY COLUMN 2city varchar(40); COMMIT;").sqlQueryDo();
            
            SQLTransactionQuery transac = sqlF.creatTransactionQuery();
            transac.addQuery(sqlF.createSQLAlterModifyColumnTypeQuery("testTable2", new Column("2city", "varchar(40)")));
            transac.addQuery(sqlF.createSQLAlterAddForeignKeyQuery("testTable2", "fk1", new Column("2city", "varchar(40)"), "testTable1", "1id"));
            transac.addQuery(sqlF.createSQLUpdateQuery("testTable2", new String[][]{{"2city", "Strang"}}, "1=1"));
            transac.sqlQueryDo();
            transac.sqlQueryUndo();
            
            /*
            
                       
            ForeignKey fk1 = new ForeignKey("testTable1", "1name", "2city", "testTable2", "FK1");
            ForeignKey fk2 = new ForeignKey("testTable1", "1id", "2id", "testTable2", "FK2");
            ForeignKey fk3 = new ForeignKey("testTable1", "1float", "2float", "testTable2", "FK3");
            ForeignKey fk4 = new ForeignKey("testTable1", "1float", "3float", "testTable3", "FK4");
            ForeignKey fk5 = new ForeignKey("testTable2", "2id", "3id", "testTable3", "FK5");
            ForeignKey fk6 = new ForeignKey("testTable2", "2city", "3city", "testTable3", "FK6");
            ForeignKey fk7 = new ForeignKey("testTable2", "2reference", "3reference", "testTable3", "FK7");
            
            
            ArrayList<ForeignKey> lFK = new ArrayList<>();
            lFK.add(fk1);
            lFK.add(fk2);
            lFK.add(fk3);
            lFK.add(fk4);
            lFK.add(fk5);
            lFK.add(fk6);
            lFK.add(fk7);
            
            ContextAnalyser ca = new ContextAnalyser("localhost/mydb", "3306", "root", "root", lFK);
            
            ca.analyse();
            
            add1.sqlQueryUndo();
            add2.sqlQueryUndo();
            add3.sqlQueryUndo();
            */
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
