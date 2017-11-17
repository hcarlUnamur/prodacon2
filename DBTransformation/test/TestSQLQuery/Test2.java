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

public class Test2 {

    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost", "mydb", "3306", "root", "root");

            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "varchar(10) character set utf8"));
            Table t1 = new Table("testTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable1", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable1", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(20) character set greek"));
            Table t2 = new Table("testTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable2", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable2", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "int(11)"));
            Table t3 = new Table("testTable3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable3", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable3", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable3", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol4 = new ArrayList<>();
            listCol4.add(new Column("4id", "float(4,1)"));
            Table t4 = new Table("testTable4", listCol4, new ArrayList<ForeignKey>(), "4id");
            SQLCreateTableQuery add4 = sqlF.createSQLCreateTableQuery(t4);
            add4.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable4", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable4", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable4", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol5 = new ArrayList<>();
            listCol5.add(new Column("5id", "char"));
            Table t5 = new Table("testTable5", listCol5, new ArrayList<ForeignKey>(), "5id");
            SQLCreateTableQuery add5 = sqlF.createSQLCreateTableQuery(t5);
            add5.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable5", new String[]{"1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable5", new String[]{"2"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable5", new String[]{"3"}).sqlQueryDo();

            ArrayList<Column> listCol6 = new ArrayList<>();
            listCol6.add(new Column("6id", "timestamp"));
            Table t6 = new Table("testTable6", listCol6, new ArrayList<ForeignKey>(), "6id");
            SQLCreateTableQuery add6 = sqlF.createSQLCreateTableQuery(t6);
            add6.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable6", new String[]{"2017-10-25 00:00:00"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable6", new String[]{"2006-12-15 00:00:00"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable6", new String[]{"2010-04-15 00:00:00"}).sqlQueryDo();

            ArrayList<Column> listCol7 = new ArrayList<>();
            listCol7.add(new Column("7id", "datetime"));
            Table t7 = new Table("testTable7", listCol7, new ArrayList<ForeignKey>(), "7id");
            SQLCreateTableQuery add7 = sqlF.createSQLCreateTableQuery(t7);
            add7.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable7", new String[]{"2017-10-25 00:00:00"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable7", new String[]{"2006-12-15 00:00:00"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable7", new String[]{"2010-04-15 00:00:00"}).sqlQueryDo();
            
            ArrayList<Column> listCol8 = new ArrayList<>();
            listCol8.add(new Column("8id", "year(4)"));
            Table t8 = new Table("testTable8", listCol8, new ArrayList<ForeignKey>(), "8id");
            SQLCreateTableQuery add8 = sqlF.createSQLCreateTableQuery(t8);
            add8.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable8", new String[]{"2017"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable8", new String[]{"2006"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable8", new String[]{"2010"}).sqlQueryDo();


            ArrayList<Column> listCol9 = new ArrayList<>();
            listCol9.add(new Column("9id", "date"));
            Table t9 = new Table("testTable9", listCol9, new ArrayList<ForeignKey>(), "9id");
            SQLCreateTableQuery add9 = sqlF.createSQLCreateTableQuery(t9);
            add9.sqlQueryDo();

            sqlF.createSQLInsertQuery("testTable9", new String[]{"2017-10-25"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable9", new String[]{"2006-12-15"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTable9", new String[]{"2010-04-15"}).sqlQueryDo();

            
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
