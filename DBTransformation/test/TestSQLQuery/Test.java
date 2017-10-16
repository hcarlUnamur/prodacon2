

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
import Transformation.DBTransformation;
import Transformation.Transformation;
import Transformation.TransformationTarget;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Test {
    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
             
            
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "varchar(10)"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            //sqlF.createSQLInsertQuery("testTable1", new String[]{"Strong"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("testTable1", new String[]{"Str"}).sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(5)")); 
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            /*
            sqlF.createSQLInsertQuery("testTable2", new String[]{"s"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"St"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"Str"}).sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "varchar(6)"));
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable3", new String[]{"str"}).sqlQueryDo();
            
            ArrayList<Column> listCol4 = new ArrayList<>();
            listCol4.add(new Column("4id", "varchar(6)"));
            Table t4 = new Table("testTable4", listCol4, new ArrayList<ForeignKey>(), "4id");
            SQLCreateTableQuery add4 = sqlF.createSQLCreateTableQuery(t4);
            add4.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable4", new String[]{"str"}).sqlQueryDo();
            
            ArrayList<Column> listCol5 = new ArrayList<>();
            listCol5.add(new Column("5id", "varchar(7)"));
            Table t5 = new Table("testTable5", listCol5, new ArrayList<ForeignKey>(), "5id");
            SQLCreateTableQuery add5 = sqlF.createSQLCreateTableQuery(t5);
            add5.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable5", new String[]{"str"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable5", new String[]{"nop"}).sqlQueryDo();
            */
            ForeignKey fk1 = new ForeignKey("testTable1", "1id", "2id", "testTable2", "FK1");
            //ForeignKey fk2 = new ForeignKey("testTable2", "2id", "3id", "testTable3", "FK2");
            //ForeignKey fk3 = new ForeignKey("testTable2", "2id", "4id", "testTable4", "FK3");
            //ForeignKey fk4 = new ForeignKey("testTable4", "4id", "5id", "testTable5", "FK4");
            //ForeignKey fk5 = new ForeignKey("testTable5", "5id", "1id", "testTable1", "FK5");
            
            ArrayList<ForeignKey> lFK = new ArrayList<>();
            lFK.add(fk1);
            
            /*
            sqlF.createSQLAlterAddForeignKeyQuery("testTable3", fk2).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable4", fk3).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable5", fk4).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable1", fk5).sqlQueryDo();
            */
            HashMap<String, Table> hm = new HashMap();
            hm.put("testTable1", t1);
            hm.put("testTable2", t2);
            //hm.put("testTable3", t3);
            //hm.put("testTable4", t4);
            //hm.put("testTable5", t5);
            
            ContextAnalyser ca = new ContextAnalyser("localhost/mydb", "3306", "root", "root", lFK);
            while (ca.hasNext()){
                System.out.println("OOOOOOOOOOOOOOOO");
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                    System.out.println("aaaaaaaaaa " + dbt.getTarget());
                }
                
            }
            System.out.println("NNNNNNNNNNNNNNNNN");
            add1.sqlQueryUndo();
            add2.sqlQueryUndo();
            /*
            DBTransformation bdt1 = new DBTransformation("localhost/mydb", "3306", "root", "root", hm, fk1, TransformationTarget.ForeignKeyTable, "varchar(10)");
            bdt1.analyse();
            
            System.out.println("********************************");
            bdt1.getCascadeFk().forEach(f->System.out.println(f.getConstraintName()));
            System.out.println("********************************");
            bdt1.getUnmatchingValue().forEach(System.out::println);
            */
            
            
            
            
            /*           
           
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
