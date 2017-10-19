

package TestSQLQuery;

import ContextAnalyser.ContextAnalyser;
import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLCreateTableQuery;
import EasySQL.SQLQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryType;
import EasySQL.SQLTransactionQuery;
import EasySQL.SQLUpdateQuery;
import EasySQL.Table;
import Transformation.DBTransformation;
import Transformation.EmptyTransformation;
import Transformation.ImpossibleTransformation;
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
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost", "mydb", "3306", "root", "root");
             
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int unsigned"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable1", new String[]{"1"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("testTable1", new String[]{"2"}).sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "tinyInt unsigned")); 
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable2", new String[]{"1"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("testTable2", new String[]{"2"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("testTable2", new String[]{"3"}).sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "tinyInt unsigned"));
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable3", new String[]{"1"}).sqlQueryDo();
            
            ArrayList<Column> listCol4 = new ArrayList<>();
            listCol4.add(new Column("4id", "tinyInt unsigned"));
            Table t4 = new Table("testTable4", listCol4, new ArrayList<ForeignKey>(), "4id");
            SQLCreateTableQuery add4 = sqlF.createSQLCreateTableQuery(t4);
            add4.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable4", new String[]{"1"}).sqlQueryDo();
            
            ArrayList<Column> listCol5 = new ArrayList<>();
            listCol5.add(new Column("5id", "tinyInt unsigned"));
            Table t5 = new Table("testTable5", listCol5, new ArrayList<ForeignKey>(), "5id");
            SQLCreateTableQuery add5 = sqlF.createSQLCreateTableQuery(t5);
            add5.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTable5", new String[]{"1"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("testTable5", new String[]{"nop"}).sqlQueryDo();
            
            ForeignKey fk1 = new ForeignKey("testTable1", "1id", "2id", "testTable2", "FK1");
            ForeignKey fk2 = new ForeignKey("testTable2", "2id", "3id", "testTable3", "FK2");
            ForeignKey fk3 = new ForeignKey("testTable2", "2id", "4id", "testTable4", "FK3");
            ForeignKey fk4 = new ForeignKey("testTable4", "4id", "5id", "testTable5", "FK4");
            
            ArrayList<ForeignKey> lFK = new ArrayList<>();
            lFK.add(fk1);
            
            //sqlF.createSQLAlterAddForeignKeyQuery("testTable2", fk1).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable3", fk2).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable4", fk3).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable5", fk4).sqlQueryDo();

            /*
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFK);
            
            
            while (ca.hasNext()){
                System.out.println("OOOOOOOOOOOOOOOO");
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                    System.out.println("aaaaaaaaaa " + dbt.getTarget() + " + " + dbt.getTransforamtiontype() + " + encodage match : " + dbt.isEncodageMatching());
                    for (SQLQuery q : dbt.getListQuery()){
                        System.out.println("query : " + q.toString());
                    }
                    
                    for (String s : dbt.getUnmatchingValue()){
                        System.out.println("unmatching Values : " + s);
                    }
                                       
                    dbt.transfrom();
                    System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuundo");
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.out.println("MESSAGE : " + impT.getMessage());
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.out.println("MESSAGE : " + empt.getMessage());
                }
                
            }
            System.out.println("NNNNNNNNNNNNNNNNN");
            add5.sqlQueryUndo();
            add4.sqlQueryUndo();
            add3.sqlQueryUndo();
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
           */
            
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
