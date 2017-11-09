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

public class TestCA {

    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost", "mydb", "3306", "root", "root");

            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable1", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"3"}).sqlQueryDo();
*/
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "tinyInt"));
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable2", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"3"}).sqlQueryDo();
*/
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "int"));
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable3", new String[]{"12"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable3", new String[]{"24"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable3", new String[]{"35"}).sqlQueryDo();
*/
            ArrayList<Column> listCol4 = new ArrayList<>();
            listCol4.add(new Column("4id", "int"));
            Table t4 = new Table("testTable4", listCol4, new ArrayList<ForeignKey>(), "4id");
            SQLCreateTableQuery add4 = sqlF.createSQLCreateTableQuery(t4);
            add4.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable4", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable4", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable4", new String[]{"3"}).sqlQueryDo();
*/
            ArrayList<Column> listCol5 = new ArrayList<>();
            listCol5.add(new Column("5id", "tinyInt"));
            Table t5 = new Table("testTable5", listCol5, new ArrayList<ForeignKey>(), "5id");
            SQLCreateTableQuery add5 = sqlF.createSQLCreateTableQuery(t5);
            add5.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable5", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable5", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable5", new String[]{"3"}).sqlQueryDo();
*/
            ArrayList<Column> listCol6 = new ArrayList<>();
            listCol6.add(new Column("6id", "tinyInt"));
            Table t6 = new Table("testTable6", listCol6, new ArrayList<ForeignKey>(), "6id");
            SQLCreateTableQuery add6 = sqlF.createSQLCreateTableQuery(t6);
            add6.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable6", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable6", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable6", new String[]{"3"}).sqlQueryDo();
*/
            ArrayList<Column> listCol7 = new ArrayList<>();
            listCol7.add(new Column("7id", "tinyInt"));
            Table t7 = new Table("testTable7", listCol7, new ArrayList<ForeignKey>(), "7id");
            SQLCreateTableQuery add7 = sqlF.createSQLCreateTableQuery(t7);
            add7.sqlQueryDo();
/*
            sqlF.createSQLInsertQuery("testTable7", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable7", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable7", new String[]{"3"}).sqlQueryDo();
*//*
            ArrayList<Column> listCol8 = new ArrayList<>();
            listCol8.add(new Column("8id", "bigInt"));
            Table t8 = new Table("testTable8", listCol8, new ArrayList<ForeignKey>(), "8id");
            SQLCreateTableQuery add8 = sqlF.createSQLCreateTableQuery(t8);
            add8.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable8", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable8", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable8", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol9 = new ArrayList<>();
            listCol9.add(new Column("9id", "varchar(10)"));
            Table t9 = new Table("testTable9", listCol9, new ArrayList<ForeignKey>(), "9id");
            SQLCreateTableQuery add9 = sqlF.createSQLCreateTableQuery(t9);
            add9.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable9", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable9", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable9", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol10 = new ArrayList<>();
            listCol10.add(new Column("10id", "mediumInt"));
            Table t10 = new Table("testTable10", listCol10, new ArrayList<ForeignKey>(), "10id");
            SQLCreateTableQuery add10 = sqlF.createSQLCreateTableQuery(t10);
            add10.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable10", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable10", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable10", new String[]{"3"}).sqlQueryDo();
            
            ArrayList<Column> listCol11 = new ArrayList<>();
            listCol11.add(new Column("11id", "tinyInt"));
            Table t11 = new Table("testTable11", listCol11, new ArrayList<ForeignKey>(), "11id");
            SQLCreateTableQuery add11 = sqlF.createSQLCreateTableQuery(t11);
            add11.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable11", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable11", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable11", new String[]{"3"}).sqlQueryDo();
            
            ArrayList<Column> listCol12 = new ArrayList<>();
            listCol12.add(new Column("12id", "tinyInt"));
            Table t12 = new Table("testTable12", listCol12, new ArrayList<ForeignKey>(), "12id");
            SQLCreateTableQuery add12 = sqlF.createSQLCreateTableQuery(t12);
            add12.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable12", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable12", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable12", new String[]{"3"}).sqlQueryDo();
            
            ArrayList<Column> listCol13 = new ArrayList<>();
            listCol13.add(new Column("13id", "int"));
            Table t13 = new Table("testTable13", listCol13, new ArrayList<ForeignKey>(), "13id");
            SQLCreateTableQuery add13 = sqlF.createSQLCreateTableQuery(t13);
            add13.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable13", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable13", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable13", new String[]{"3"}).sqlQueryDo();
            
            ArrayList<Column> listCol14 = new ArrayList<>();
            listCol14.add(new Column("14id", "tinyInt"));
            Table t14 = new Table("testTable14", listCol14, new ArrayList<ForeignKey>(), "14id");
            SQLCreateTableQuery add14 = sqlF.createSQLCreateTableQuery(t14);
            add14.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable14", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable14", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable14", new String[]{"3"}).sqlQueryDo();
*/

            ForeignKey fk0 = new ForeignKey("testTable2", "2id", "1id", "testTable1", "FK0");

            ForeignKey fk1 = new ForeignKey("testTable1", "1id", "3id", "testTable3", "FK1");
            ForeignKey fk2 = new ForeignKey("testTable4", "4id", "1id", "testTable1", "FK2");
            ForeignKey fk3 = new ForeignKey("testTable2", "2id", "5id", "testTable5", "FK3");
            ForeignKey fk4 = new ForeignKey("testTable2", "2id", "6id", "testTable6", "FK4");
            ForeignKey fk5 = new ForeignKey("testTable7", "7id", "6id", "testTable6", "FK5");

            ArrayList<ForeignKey> lFK = new ArrayList<>();
            lFK.add(fk0);

            sqlF.createSQLAlterAddForeignKeyQuery("testTable3", fk1).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable1", fk2).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable5", fk3).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable6", fk4).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("testTable6", fk5).sqlQueryDo();

            
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
                           
                    dbt.setTarget(TransformationTarget.All);
                    dbt.setNewType("varchar(30)");
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
            /*
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
