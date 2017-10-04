/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLAlterTableQuery;
import EasySQL.SQLCreateTableQuery;
import EasySQL.SQLDeleteQuery;
import EasySQL.SQLDropTableQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLSelectQuery;
import EasySQL.SQLUpdateQuery;
import EasySQL.StringTool;
import EasySQL.Table;
import Transformation.ANTT;
import Transformation.DTT;
import Transformation.LMTT;
import Transformation.MBT;
import Transformation.MVMT;
import Transformation.NTT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thibaud
 *
 * MEMO : Si tout plante, il y a des chances pour que ce ne soit que le select
 * ou/et le drop qui soit problèmatique, puisque s'il ne fonctionne pas,
 * impossible d'effacer toutes les tables créés dans ces tests.
 *
 */
public class JunitTest {

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");

    public Table CreateTable1(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("trueFalse", "bool"));
        Table t1 = new Table(tableName, listCol);
        return t1;
    }

    public Table CreateTable2(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("reference", "int"));
        Table t2 = new Table(tableName, listCol);
        return t2;
    }

    public void ErrorGestion(Exception ex, String functionName, ArrayList<String> ls) {
        System.err.println(ex);
        System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
        for (String l : ls) {
            try {
                sqlF.creatDropTableQuery(l).sqlQueryDo();
            } catch (Exception ex1) {
            }
        }
    }

    public int ColumnAnalyser(String functionName, String tableName, String columnName, String columnType) throws Exception {
        SQLSelectQuery selectColumn = sqlF.createSQLSelectQuery(new String[]{"information_schema.columns"},
                new String[]{"column_name", "column_type"},
                "table_name='" + tableName + "' AND column_name='" + columnName + "'"
        );
        ResultSet metaData = selectColumn.sqlQueryDo();
        metaData.next();
        if (!(metaData.getString("COLUMN_NAME").equals(columnName) && metaData.getString("COLUMN_TYPE").equals(columnType))) {
            System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
            return 1;
        } else {
            return 0;
        }
    }

    public int PrimaryKeyAnalyser(String functionName, String tableName, String columnName) throws Exception {
        SQLSelectQuery selectPrimaryKey = sqlF.createSQLSelectQuery(
                new String[]{"INFORMATION_SCHEMA.COLUMNS"},
                new String[]{"COLUMN_NAME"},
                "TABLE_NAME = '" + tableName + "' AND COLUMN_KEY = 'PRI'"
        );
        ResultSet metaData = selectPrimaryKey.sqlQueryDo();
        metaData.next();
        if (!metaData.getString("COLUMN_NAME").equals(columnName)) {
            System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
            return 1;
        } else {
            return 0;
        }
    }

    public int ForeignKeyAnalyser(String functionName, String tableName, String columnName, String constraintName) throws Exception {
        SQLSelectQuery selectForeignKey = sqlF.createSQLSelectQuery(new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                new String[]{"TABLE_NAME,COLUMN_NAME", "COLUMN_NAME", "CONSTRAINT_NAME", "REFERENCED_TABLE_NAME", "REFERENCED_COLUMN_NAME"},
                "TABLE_NAME = '" + tableName + "' AND CONSTRAINT_NAME='" + constraintName + "'"
        );
        ResultSet metaData = selectForeignKey.sqlQueryDo();
        metaData.next();
        if (!(metaData.getString("COLUMN_NAME").equals(columnName) && metaData.getString("CONSTRAINT_NAME").equals(constraintName))) {
            System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
            return 1;
        } else {
            return 0;
        }
    }

    public int SelectForTest(String functionName, String tableName, String[] columns, String[] values) throws Exception {

        ResultSet metadata = sqlF.createSQLSelectQuery(tableName, new String[]{columns[0], columns[1], columns[2]}, "id = '" + values[0] + "'").sqlQueryDo();
        metadata.first();
        if (!(metadata.getString(1).equals(values[0]) && metadata.getString(2).equals(values[1]) && metadata.getString(3).equals(values[2]))) {
            System.err.println("Wrong Select Query");
            System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
            return 1;
        } else {
            return 0;
        }

    }

    public void PeuplerTable(String tableName) throws SQLException {
        sqlF.creatSQLInsertQuery(tableName, new String[]{"1", "Strong", "1"}).sqlQueryDo();
        sqlF.creatSQLInsertQuery(tableName, new String[]{"6", "String", "1"}).sqlQueryDo();
        sqlF.creatSQLInsertQuery(tableName, new String[]{"5", "Strang", "0"}).sqlQueryDo();
    }

    public int PeuplementTest(String functionName, String tableName) throws Exception {
        int result = SelectForTest(functionName, tableName, new String[]{"id", "name", "trueFalse"}, new String[]{"1", "Strong", "1"});
        if (result == 0) {
            result = SelectForTest(functionName, tableName, new String[]{"id", "name", "trueFalse"}, new String[]{"6", "String", "1"});
        }
        if (result == 0) {
            result = SelectForTest(functionName, tableName, new String[]{"id", "name", "trueFalse"}, new String[]{"5", "Strang", "0"});
        }
        return result;
    }

    @Test
    public void testCreateTableQuery1() {
        int result = 0;
        try {
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery("testCreateTable1", new String[]{"id int", "name varchar(45)", "trueFalse bool"});
            add1.sqlQueryDo();
            add1.sqlQueryUndo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery1", new ArrayList<>(Arrays.asList("testCreateTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testCreateTableQuery2() {
        int result = 0;
        try {
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id", "int"));
            listCol.add(new Column("name", "varchar(45)"));
            listCol.add(new Column("trueFalse", "bool"));
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery("testCreateTable2", listCol);
            add2.sqlQueryDo();
            add2.sqlQueryUndo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery2", new ArrayList<>(Arrays.asList("testCreateTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testCreateTableQuery3() {
        int result = 0;
        try {
            Table t3 = CreateTable1("testCreateTable3");
            SQLCreateTableQuery add3 = sqlF.creatSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            add3.sqlQueryUndo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery3", new ArrayList<>(Arrays.asList("testCreateTable3")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testDropTableQuery1() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testDropTable1");
            sqlF.creatSQLCreateTableQuery(t1).sqlQueryDo();

            PeuplerTable("testDropTable1");

            SQLDropTableQuery drop1 = sqlF.creatDropTableQuery("testDropTable1");
            drop1.sqlQueryDo();
            drop1.sqlQueryUndo();

            result = PeuplementTest("testDropTableQuery1", "testDropTable1");
            drop1.sqlQueryDo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testDropTableQuery1", new ArrayList<>(Arrays.asList("testDropTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testDropTableQuery() {

        int result = 0;
        try {
            Table t2 = CreateTable1("testDropTable2");
            sqlF.creatSQLCreateTableQuery(t2).sqlQueryDo();

            PeuplerTable("testDropTable2");

            SQLDropTableQuery drop2 = sqlF.creatDropTableQuery(t2);
            drop2.sqlQueryDo();
            drop2.sqlQueryUndo();

            result = PeuplementTest("testDropTableQuery", "testDropTable2");
            drop2.sqlQueryDo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testDropTableQuery", new ArrayList<>(Arrays.asList("testDropTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testInsertDeleteQuery1() {

        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testInsertTable1", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            PeuplerTable("testInsertTable1");
            //SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable1", "id = \"1\" && name = \"Strong\" && trueFalse = \"1\"");
            SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable1", "1=1");
            del.sqlQueryDo();
            del.sqlQueryUndo();

            result = PeuplementTest("testInsertDeleteQuery1", "testInsertTable1");

            sqlF.creatDropTableQuery("testInsertTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testInsertDeleteQuery1", new ArrayList<>(Arrays.asList("testInsertTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testInsertDeleteQuery2() {

        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testInsertTable2", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();

            sqlF.creatSQLInsertQuery("testInsertTable2", new String[]{"id", "trueFalse"}, new String[]{"1", "1"}).sqlQueryDo();

            //SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable2", "id = \"1\" && name = \"Strong\" && trueFalse = \"1\"");
            SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable2", "1=1");
            del.sqlQueryDo();
            del.sqlQueryUndo();

            ResultSet metadata = sqlF.createSQLSelectQuery("testInsertTable2", new String[]{"id", "trueFalse"}, "id = '1'").sqlQueryDo();
            metadata.first();
            
            if(!(metadata.getString(1).equals("1") && metadata.getString(2).equals("1"))){
                System.err.println("Wrong Select Query");
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery2()");
                result = 1;
            }
            sqlF.creatDropTableQuery("testInsertTable2").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testInsertDeleteQuery2", new ArrayList<>(Arrays.asList("testInsertTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testUpdateQuery() {

        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testUpdateTable", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            PeuplerTable("testUpdateTable");
            //String where ="id=1 and name=\"Strong\" and trueFalse=\"1\" "; //new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}
            String where = "1=1";
            SQLUpdateQuery upd = sqlF.createSQLUpdateQuery("testUpdateTable", new String[][]{{"name", "Smith"}, {"trueFalse", "0"}}, where);
            upd.sqlQueryDo();
            upd.sqlQueryUndo();

            result = PeuplementTest("testUpdateQuery", "testUpdateTable");

            sqlF.creatDropTableQuery("testUpdateTable").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testUpdateQuery", new ArrayList<>(Arrays.asList("testUpdateTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testSelectQuery() {

        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testSelectTable", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testSelectTable", new String[]{"3", "Strong", "1"}).sqlQueryDo();
            result = SelectForTest("testSelectQuery", "testSelectTable", new String[]{"id", "name", "trueFalse"}, new String[]{"3", "Strong", "1"});
            sqlF.creatDropTableQuery("testSelectTable").sqlQueryDo();
        } catch (Exception ex) {
            ErrorGestion(ex, "testSelectQuery", new ArrayList<>(Arrays.asList("testSelectTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void AlterAddDropPrimaryKeyQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddPrimaryKeyTable");
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery pk = sqlF.creatSQLAlterAddPrimaryKeyQuery("testAddPrimaryKeyTable", "id");

            pk.sqlQueryDo();
            pk.sqlQueryUndo();
            pk.sqlQueryDo();

            result = PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id");

            sqlF.creatSQLAlterDropPrimaryKeyQuery("testAddPrimaryKeyTable").sqlQueryDo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropPrimaryKeyQuery", new ArrayList<>(Arrays.asList("testAddPrimaryKeyTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void AlterAddDropForeignKeyQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddForeignKeyTable1");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            sqlF.creatSQLAlterAddPrimaryKeyQuery("testAddForeignKeyTable1", "id").sqlQueryDo();

            Table t2 = CreateTable2("testAddForeignKeyTable2");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();

            ForeignKey fk = new ForeignKey(t1.getName(), "id", "reference", "FK1");
            sqlF.creatSQLAlterAddForeignKeyQuery(t2.getName(), fk).sqlQueryDo();

            result = ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1");

            sqlF.creatSQLAlterDropForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), t1).sqlQueryDo();
            sqlF.creatDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();
            sqlF.creatDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropForeignKeyQuery", new ArrayList<>(Arrays.asList("testAddForeignKeyTable2", "testAddForeignKeyTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void AlterAddDropColumnQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddDropColumnTable");
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery col = sqlF.creatSQLAltertableAddColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)"));

            col.sqlQueryDo();
            col.sqlQueryUndo();
            col.sqlQueryDo();

            result = ColumnAnalyser("AlterAddDropColumnQuery", "testAddDropColumnTable", "city", "varchar(45)");

            sqlF.creatSQLAlterDropColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)")).sqlQueryDo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropColumnQuery", new ArrayList<>(Arrays.asList("testAddDropColumnTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void AlterModifyColumnTypeQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testModifyColumnTypeTable");
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery mCol = sqlF.creatSQLAlterModifyColumnTypeQuery("testModifyColumnTypeTable", new Column("name", "int"));
            mCol.sqlQueryDo();

            result = ColumnAnalyser("AlterModifyColumnTypeQuery", "testModifyColumnTypeTable", "name", "int(11)");

            mCol.sqlQueryUndo();
            mCol.sqlQueryDo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterModifyColumnTypeQuery", new ArrayList<>(Arrays.asList("testModifyColumnTypeTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    @Test
    public void testLoadTable() {

        int result = 0;
        try {
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id", "int"));
            listCol.add(new Column("name", "varchar(45)"));
            listCol.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("tableLoadTable1", listCol, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();

            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "int"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "int"));

            ForeignKey fk = new ForeignKey("tableLoadTable1", "id", "reference", "FKLoadTable");

            Table t2 = new Table("tableLoadTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            t2.addForeignKey(fk);
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();

            Table tLoadTable = new Table();
            tLoadTable= sqlF.loadTable("tableLoadTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = tLoadTable.getForeignKeys();

            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKLoadTable"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLoadTable()");
            }
            
            if (!(tLoadTable.getPrimaryKey().equals("id"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLoadTable()");
            }
            if (!(tLoadTable.getName().equals("tableLoadTable2"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLoadTable()");
            }
            ArrayList<Column> lCol = new ArrayList<>();
            lCol = tLoadTable.getTablecolumn();
            if (!(lCol.get(0).getColumnName().equals("id") && lCol.get(1).getColumnName().equals("city") && lCol.get(2).getColumnName().equals("reference"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLoadTable()");
            }
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testLoadTable", new ArrayList<>(Arrays.asList("tableLoadTable2", "tableLoadTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testMVMT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testMVMTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testMVMTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testMVMTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testMVMTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMVMTTable1", "id", "reference", "FKMVMT");
            
            MVMT m = new MVMT("localhost/mydb", "3306", "root", "root", "testMVMTTable2", fk, (Object s)->{return ((String) s).toUpperCase();});
            m.transfrom();  
            
            t2 = sqlF.loadTable("testMVMTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();

            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKMVMT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testMVMT()");
            }
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    } catch (Exception ex) {
            ErrorGestion(ex, "testMVMT", new ArrayList<>(Arrays.asList("testMVMTTable2", "testMVMTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testMBT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testMBTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testMBTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testMBTTable1", new String[]{"coucou", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testMBTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMBTTable1", "id", "reference", "FKMBT");
            
            MBT m = new MBT("localhost/mydb", "3306", "root", "root", "testMBTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testMBTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();

            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKMBT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testMBT()");
            }
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    } catch (Exception ex) {
            ErrorGestion(ex, "testMBT", new ArrayList<>(Arrays.asList("testMBTTable2", "testMBTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testLMTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "int(30)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testLMTTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "int"));
            
            Table t2 = new Table("testLMTTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testLMTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testLMTTTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testLMTTTable1", "id", "reference", "FKLMTT");
            
            LMTT m = new LMTT("localhost/mydb", "3306", "root", "root", "testLMTTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testLMTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            /*
            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKLMTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLMTT()");
            }
            */
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    } catch (Exception ex) {
            ErrorGestion(ex, "testLMTT", new ArrayList<>(Arrays.asList("testLMTTTable2", "testLMTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testANTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testANTTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testANTTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testANTTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testANTTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testANTTTable1", "id", "reference", "FKANTT");
            
            ANTT m = new ANTT("localhost/mydb", "3306", "root", "root", "testANTTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testANTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            /*
            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKANTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testANTT()");
            }
            */
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            
        } catch (Exception ex) {
            ErrorGestion(ex, "testANTT", new ArrayList<>(Arrays.asList("testANTTTable2", "testANTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testDTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testDTTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testDTTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testDTTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testDTTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testDTTTable1", "id", "reference", "FKDTT");
            
            DTT m = new DTT("localhost/mydb", "3306", "root", "root", "testDTTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testDTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            /*
            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKDTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testDTT()");
            }
            */
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            
        } catch (Exception ex) {
            ErrorGestion(ex, "testDTT", new ArrayList<>(Arrays.asList("testDTTTable2", "testDTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testNTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testNTTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testNTTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.creatSQLInsertQuery("testNTTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testNTTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testNTTTable1", "id", "reference", "FKNTT");
            
            NTT m = new NTT("localhost/mydb", "3306", "root", "root", "testNTTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testNTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            /*
            if (!(lfk.get(0).getForeingKeyColumn().equals("reference") && lfk.get(0).getConstraintName().equals("FKNTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testNTT()");
            }
            */
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            
        } catch (Exception ex) {
            ErrorGestion(ex, "testNTT", new ArrayList<>(Arrays.asList("testNTTTable2", "testNTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
   

}
