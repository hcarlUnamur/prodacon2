/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import ContextAnalyser.ContextAnalyser;
import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLAlterTableQuery;
import EasySQL.SQLCreateTableQuery;
import EasySQL.SQLCreateTriggerQuery;
import EasySQL.SQLDeleteQuery;
import EasySQL.SQLDropTableQuery;
import EasySQL.SQLInsertQuery;
import EasySQL.SQLQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryFree;
import EasySQL.SQLQueryType;
import EasySQL.SQLSelectQuery;
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
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thibaud
 * 
 * MEMO : if everything crashes, there's a chance that it is only the 'select' and/or the 'drop' that has an issue because if it/they don't work, 
 * it is impossible to erase all the tables created in the tests.
 *
 */
public class JunitTest {

    
    SQLQueryFactory sqlF = new SQLQueryFactory("localhost", "mydb", "3306", "root", "root");

    //method that create rapidely a table. This method is not a test but is used only inside tests.
    public Table CreateTable1(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("trueFalse", "bool"));
        Table t1 = new Table(tableName, listCol);
        return t1;
    }

    //method that create rapidely a table different than with 'CreateTable1'. This method is not a test but is used only inside tests.
    public Table CreateTable2(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("reference", "int"));
        Table t2 = new Table(tableName, listCol);
        return t2;
    }

    //This method handels an exception that could arise in any test. It prints the exception message, 
    //from which test the exception has arisen and finally, it destroys all the tables created in that test. 
    //This method is not a test but is used only inside tests.
    public void ErrorGestion(Exception ex, String functionName, ArrayList<String> ls) {
        System.err.println(ex);
        System.err.println("ko! : " + "TestSQLQuery.JunitTest." + functionName + "()");
        for (String l : ls) {
            try {
                sqlF.createDropTableQuery(l).sqlQueryDo();
            } catch (Exception ex1) {
            }
        }
    }

    
    //This method allows us to analyse rapidly if, for a given table, a given column name and a given column type, the column type of the table is effectively the type given in parameter.
    //if that's not the case, it fails the test (return 1).
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
    
    //This method allows us to analyse rapidly if, for a given table and a given column, the column of the table is a primery key or not.
    //if that's not the case, it fails the test (return 1).
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

    //This method allows us to analyse rapidly if, for a given table, a given column name and a given constraint name, the column of the table is a foreign key of the column name parameter.
    //if that's not the case, it fails the test (return 1).
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

    //This method allows us to analyse rapidly if, for a given columns list and a given data values list, the first line of the table has effectively the same values than in the list of values given in parameter.
    //if that's not the case, it fails the test (return 1).
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

    //This method allows us to populate rapidely a given table with predifined values 
    //(typicaly, it's recommended to use this method only on tables created with the 'CreateTable1' or 'CreateTable2' methods).
    public void PeuplerTable(String tableName) throws SQLException {
        sqlF.createSQLInsertQuery(tableName, new String[]{"1", "Strong", "1"}).sqlQueryDo();
        sqlF.createSQLInsertQuery(tableName, new String[]{"6", "String", "1"}).sqlQueryDo();
        sqlF.createSQLInsertQuery(tableName, new String[]{"5", "Strang", "0"}).sqlQueryDo();
    }

    //This method allows us to analyse rapidly if the first line of a given table correspond with at least one of the lines of a table returned by the 'PeuplerTable' method.
    //if that's not the case, it fails the test (return 1).
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

    //This method allows us to test if the first method for adding a table in DB and its 'UNDO' work.
    @Test
    public void testCreateTableQuery1() {
        int result = 0;
        try {
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery("testCreateTable1", new String[]{"id int", "name varchar(45)", "trueFalse bool"});
            add1.sqlQueryDo();
            add1.sqlQueryUndo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery1", new ArrayList<>(Arrays.asList("testCreateTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the second method for adding a table in DB and its 'UNDO' work.
    @Test
    public void testCreateTableQuery2() {
        int result = 0;
        try {
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id", "int"));
            listCol.add(new Column("name", "varchar(45)"));
            listCol.add(new Column("trueFalse", "bool"));
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery("testCreateTable2", listCol);
            add2.sqlQueryDo();
            add2.sqlQueryUndo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery2", new ArrayList<>(Arrays.asList("testCreateTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the third method for adding a table in DB and its 'UNDO' work.
    @Test
    public void testCreateTableQuery3() {
        int result = 0;
        try {
            Table t3 = CreateTable1("testCreateTable3");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            add3.sqlQueryUndo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testCreateTableQuery3", new ArrayList<>(Arrays.asList("testCreateTable3")));
            result = 1;
        }
        assertEquals(0, result);
    }
    
    //This method allows us to test if the first method for dropping a table in DB and its 'UNDO' work.
    @Test
    public void testDropTableQuery1() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testDropTable1");
            sqlF.createSQLCreateTableQuery(t1).sqlQueryDo();

            PeuplerTable("testDropTable1");

            SQLDropTableQuery drop1 = sqlF.createDropTableQuery("testDropTable1");
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

    //This method allows us to test if the second method for dropping a table in DB and its 'UNDO' work.
    @Test
    public void testDropTableQuery2() {

        int result = 0;
        try {
            Table t2 = CreateTable1("testDropTable2");
            sqlF.createSQLCreateTableQuery(t2).sqlQueryDo();

            PeuplerTable("testDropTable2");

            SQLDropTableQuery drop2 = sqlF.createDropTableQuery(t2);
            drop2.sqlQueryDo();
            drop2.sqlQueryUndo();

            result = PeuplementTest("testDropTableQuery2", "testDropTable2");
            drop2.sqlQueryDo();

            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testDropTableQuery2", new ArrayList<>(Arrays.asList("testDropTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the first method for deleting values in a table in DB and its 'UNDO' work.
    @Test
    public void testInsertDeleteQuery1() {

        int result = 0;
        try {
            sqlF.createSQLCreateTableQuery("testInsertTable1", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            PeuplerTable("testInsertTable1");
            //SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable1", "id = \"1\" && name = \"Strong\" && trueFalse = \"1\"");
            SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable1", "1=1");
            del.sqlQueryDo();
            del.sqlQueryUndo();
            
            result = PeuplementTest("testInsertDeleteQuery1", "testInsertTable1");

            sqlF.createDropTableQuery("testInsertTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testInsertDeleteQuery1", new ArrayList<>(Arrays.asList("testInsertTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the second method for deleting values in a table in DB and its 'UNDO' work.
    @Test
    public void testInsertDeleteQuery2() {

        int result = 0;
        try {
            sqlF.createSQLCreateTableQuery("testInsertTable2", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();

            SQLInsertQuery ins = sqlF.createSQLInsertQuery("testInsertTable2", new String[]{"id", "trueFalse"}, new String[]{"1", "1"});
            ins.sqlQueryDo();
            
            ResultSet metadata = sqlF.createSQLSelectQuery("testInsertTable2", new String[]{"id", "trueFalse"}, "id = '1'").sqlQueryDo();
            metadata.first();
            if(!(metadata.getString(1).equals("1") && metadata.getString(2).equals("1"))){
                System.err.println("Wrong Select Query");
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery2()");
                result = 1;
            }
            ins.sqlQueryUndo();
            try{
                metadata = sqlF.createSQLSelectQuery("testInsertTable2", new String[]{"id", "trueFalse"}, "id = '1'").sqlQueryDo();
                metadata.first();
                if(!(metadata.getString(1).equals("1") && metadata.getString(2).equals("1"))){
                    System.err.println("Wrong Select Query");
                    System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery2()");
                    result = 1;
                }
            }catch (Exception ex){}
            ins.sqlQueryDo();
            
            SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable2", "1=1");
            del.sqlQueryDo();
            
            try{
                metadata = sqlF.createSQLSelectQuery("testInsertTable2", new String[]{"id", "trueFalse"}, "id = '1'").sqlQueryDo();
                metadata.first();
                if(!(metadata.getString(1).equals("1") && metadata.getString(2).equals("1"))){
                    System.err.println("Wrong Select Query");
                    System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery2()");
                    result = 1;
                }
            }catch (Exception ex){}
            
            del.sqlQueryUndo();

            metadata = sqlF.createSQLSelectQuery("testInsertTable2", new String[]{"id", "trueFalse"}, "id = '1'").sqlQueryDo();
            metadata.first();
            
            if(!(metadata.getString(1).equals("1") && metadata.getString(2).equals("1"))){
                System.err.println("Wrong Select Query");
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery2()");
                result = 1;
            }
            sqlF.createDropTableQuery("testInsertTable2").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testInsertDeleteQuery2", new ArrayList<>(Arrays.asList("testInsertTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the method for updating values in a table in DB and its 'UNDO' work.
    @Test
    public void testUpdateQuery() {

        int result = 0;
        try {
            sqlF.createSQLCreateTableQuery("testUpdateTable", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            PeuplerTable("testUpdateTable");
            //String where ="id=1 and name=\"Strong\" and trueFalse=\"1\" "; //new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}
            String where = "1=1";
            SQLUpdateQuery upd = sqlF.createSQLUpdateQuery("testUpdateTable", new String[][]{{"name", "Smith"}, {"trueFalse", "0"}}, where);
            upd.sqlQueryDo();
            upd.sqlQueryUndo();

            result = PeuplementTest("testUpdateQuery", "testUpdateTable");

            sqlF.createDropTableQuery("testUpdateTable").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testUpdateQuery", new ArrayList<>(Arrays.asList("testUpdateTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the method for selecting values in a table in DB and its 'UNDO' work.
    @Test
    public void testSelectQuery() {

        int result = 0;
        try {
            sqlF.createSQLCreateTableQuery("testSelectTable", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testSelectTable", new String[]{"3", "Strong", "1"}).sqlQueryDo();
            result = SelectForTest("testSelectQuery", "testSelectTable", new String[]{"id", "name", "trueFalse"}, new String[]{"3", "Strong", "1"});
            sqlF.createDropTableQuery("testSelectTable").sqlQueryDo();
        } catch (Exception ex) {
            ErrorGestion(ex, "testSelectQuery", new ArrayList<>(Arrays.asList("testSelectTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the method for adding a primary in a table in DB and its 'UNDO' work.
    @Test
    public void AlterAddDropPrimaryKeyQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddPrimaryKeyTable");
            SQLCreateTableQuery add = sqlF.createSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery pk = sqlF.createSQLAlterAddPrimaryKeyQuery("testAddPrimaryKeyTable", "id");

            pk.sqlQueryDo();
            pk.sqlQueryUndo();
            try{
                PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id");
                result = 1;
            }catch(Exception ex){}
            pk.sqlQueryDo();

            if (PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id")==1){
                result = 1;
            }

            SQLAlterTableQuery apq = sqlF.createSQLAlterDropPrimaryKeyQuery("testAddPrimaryKeyTable");
            apq.sqlQueryDo();
            try{
                PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id");
                result = 1;
            }catch(Exception ex){}
            apq.sqlQueryUndo();
            if (PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id")==1){
                result = 1;
            }
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropPrimaryKeyQuery", new ArrayList<>(Arrays.asList("testAddPrimaryKeyTable")));
            result = 1;
        }
        assertEquals(0, result);
    }
    
    //This method allows us to test if the method for adding a foreign key in a table in DB and its 'UNDO' work.
    @Test
    public void AlterAddDropForeignKeyQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddForeignKeyTable1");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            sqlF.createSQLAlterAddPrimaryKeyQuery("testAddForeignKeyTable1", "id").sqlQueryDo();

            Table t2 = CreateTable2("testAddForeignKeyTable2");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();

            ForeignKey fk = new ForeignKey(t1.getName(), "id", "reference", "FK1");
            SQLAlterTableQuery cfk = sqlF.createSQLAlterAddForeignKeyQuery(t2.getName(), fk);
            cfk.sqlQueryDo();
            result = ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1");
            cfk.sqlQueryUndo();
            try{
                ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1");
                result = 1;
            }catch(Exception ex){}
            cfk.sqlQueryDo();

            
            SQLAlterTableQuery dfk = sqlF.createSQLAlterDropForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), t1);
            dfk.sqlQueryDo();
            try{
                ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1");
                result = 1;
            }catch(Exception ex){}
            dfk.sqlQueryUndo();
            if (ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1") == 1){
                result = 1;
            }
            
            sqlF.createDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();
            sqlF.createDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropForeignKeyQuery", new ArrayList<>(Arrays.asList("testAddForeignKeyTable2", "testAddForeignKeyTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the method for adding and deleting columns in a table in DB and theirs 'UNDO' work.
    @Test
    public void AlterAddDropColumnQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testAddDropColumnTable");
            SQLCreateTableQuery add = sqlF.createSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery col = sqlF.createSQLAltertableAddColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)"));

            col.sqlQueryDo();
            col.sqlQueryUndo();
            col.sqlQueryDo();

            result = ColumnAnalyser("AlterAddDropColumnQuery", "testAddDropColumnTable", "city", "varchar(45)");

            SQLAlterTableQuery dcq = sqlF.createSQLAlterDropColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)"));
            dcq.sqlQueryDo();
            dcq.sqlQueryUndo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropColumnQuery", new ArrayList<>(Arrays.asList("testAddDropColumnTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //This method allows us to test if the method for modifying a column type in a table in DB and its 'UNDO' work.
    @Test
    public void AlterModifyColumnTypeQuery() {

        int result = 0;
        try {
            Table t1 = CreateTable1("testModifyColumnTypeTable");
            SQLCreateTableQuery add = sqlF.createSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery mCol = sqlF.createSQLAlterModifyColumnTypeQuery("testModifyColumnTypeTable", new Column("name", "int"));
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

    //permet de tester si la méthode de chargement d'une table (loadTable) fonctionne.
    //This method allows us to test if the method for loading a table (loadTable) works.
    @Test
    public void testLoadTable() {

        int result = 0;
        try {
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id", "int"));
            listCol.add(new Column("name", "varchar(45)CHARACTER SET latin1"));
            listCol.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("tableLoadTable1", listCol, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();

            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "int"));
            listCol2.add(new Column("city", "varchar(45)CHARACTER SET latin1"));
            listCol2.add(new Column("reference", "int"));

            ForeignKey fk = new ForeignKey("tableLoadTable1", "id", "reference", "FKLoadTable");

            Table t2 = new Table("tableLoadTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            t2.addForeignKey(fk);
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
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
            
            
            for(Column c : lCol){
                if (c.getColumnType().equals("varchar(45)")){
                    if (!c.getCharset().equals("latin1")){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLoadTable() : wrong charset");
                    }
                }
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
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "float(6,2)"));
            Table t1 = new Table("testMVMTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "float(5,2)"));
            
            Table t2 = new Table("testMVMTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testMVMTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testMVMTTable2", new String[]{"deux", "Strong", "12"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMVMTTable1", "1reference", "2reference", "testMVMTTable2", "FKMVMT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                    
                    
                    if (dbt.getUnmatchingValue().size() < 1){
                        result = 1;
                    }
                      /*                
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                      
                    if (!(dbt.getTransforamtiontype().toString().equals("MVMT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testMVMT()");
                    }
                    dbt.transfrom();*/
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testMVMTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            /*if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKMVMT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testMVMT()");
            }*/
            
            
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
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "int"));
            Table t1 = new Table("testMBTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "int"));
            
            Table t2 = new Table("testMBTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testMBTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testMBTTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMBTTable1", "1reference", "2reference", "testMBTTable2", "FKMBT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("MBT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testMBT()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testMBTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKMBT"))) {
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
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "float(6,2)"));
            Table t1 = new Table("testLMTTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "float(5,2)"));
            
            Table t2 = new Table("testLMTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testLMTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testLMTTTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testLMTTTable1", "1reference", "2reference", "testLMTTTable2", "FKLMTT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("LMTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLMTT()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testLMTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKLMTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLMTT()");
            }
            
            
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
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "char"));
            Table t1 = new Table("testANTTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "varchar(10)"));
            
            Table t2 = new Table("testANTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testANTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testANTTTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testANTTTable1", "1reference", "2reference", "testANTTTable2", "FKANTT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("ANTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testANTT()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testANTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKANTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testANTT()");
            }
            
            
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    
        } catch (Exception ex) {
            ErrorGestion(ex, "testANTT", new ArrayList<>(Arrays.asList("testANTTTable2", "testANTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
     @Test
    public void testTTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "timeStamp"));
            Table t1 = new Table("testTTTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "dateTime"));
            
            Table t2 = new Table("testTTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testTTTTable1", new String[]{"1", "Strong", "2013-01-17 00:00:00"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTTTTable2", new String[]{"deux", "Strong", "2013-01-17 00:00:00"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testTTTTable1", "1reference", "2reference", "testTTTTable2", "FKTTT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("TTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testTTT()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testTTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKTTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testTTT()");
            }
            
            
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    
        } catch (Exception ex) {
            ErrorGestion(ex, "testTTT", new ArrayList<>(Arrays.asList("testTTTTable2", "testTTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testNTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "int"));
            Table t1 = new Table("testNTTTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "bigInt"));
            
            Table t2 = new Table("testNTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testNTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testNTTTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testNTTTable1", "1reference", "2reference", "testNTTTable2", "FKNTT");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("NTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testNTT()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
            
            
            t2 = sqlF.loadTable("testNTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKNTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testNTT()");
            }
            
            
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    
        } catch (Exception ex) {
            ErrorGestion(ex, "testNTT", new ArrayList<>(Arrays.asList("testNTTTable2", "testNTTTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testEMPTY() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "float(6,2)"));
            Table t1 = new Table("testEMPTYTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "float(5,2)"));
            
            Table t2 = new Table("testEMPTYTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testEMPTYTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testEMPTYTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testEMPTYTable1", "1reference", "2reference", "testEMPTYTable2", "FKEMPTY");
            
            SQLAlterTableQuery cfk = sqlF.createSQLAlterAddForeignKeyQuery(t2.getName(), fk);
            cfk.sqlQueryDo();
            
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    /*
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("LMTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testEMPTY()");
                    }
                    dbt.transfrom();
                    //dbt.unDoTransformation();
                    */
                    System.err.println("MESSAGE : should be EmptyTransformation");
                    result = 1;
                }
                else if (transfo instanceof ImpossibleTransformation){
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                }else if (transfo instanceof EmptyTransformation){
                    /*
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                    */
                }
                
            }
            
            
            t2 = sqlF.loadTable("testEMPTYTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKEMPTY"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testEMPTY()");
            }
            
            
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    
        } catch (Exception ex) {
            ErrorGestion(ex, "testEMPTY", new ArrayList<>(Arrays.asList("testEMPTYTable2", "testEMPTYTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testIMP() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int(30)"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "varchar(10)"));
            Table t1 = new Table("testIMPTable1", listCol1, new ArrayList<ForeignKey>(), "1reference");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "int"));
            
            Table t2 = new Table("testIMPTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testIMPTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testIMPTable2", new String[]{"deux", "Strong", "1"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testIMPTable1", "1reference", "2reference", "testIMPTable2", "FKIMP");
            ArrayList<ForeignKey> lFk = new ArrayList<>();
            lFk.add(fk);
            ContextAnalyser ca = new ContextAnalyser("localhost", "mydb", "3306", "root", "root", lFk);
            
            
            while (ca.hasNext()){
                Transformation transfo = ca.next();
                
                if (transfo instanceof DBTransformation){
                    /*
                    DBTransformation dbt = (DBTransformation)transfo;
                    dbt.analyse();
                                        
                    for (String s : dbt.getUnmatchingValue()){
                        System.err.println("unmatching Values : " + s);
                        result = 1;
                    }
                       
                    if (!(dbt.getTransforamtiontype().toString().equals("LMTT"))){
                        result = 1;
                        System.err.println("ko! : " + "TestSQLQuery.JunitTest.testIMP()");
                    }
                    dbt.transfrom();
                    */
                    //result = 1;
                    //System.err.println("MESSAGE : sould be IMP transformation");
                    //dbt.unDoTransformation();
                }
                else if (transfo instanceof ImpossibleTransformation){
                    
                    /*
                    ImpossibleTransformation impT = (ImpossibleTransformation)transfo;
                    System.err.println("MESSAGE : " + impT.getMessage());
                    result = 1;
                    */
                }else if (transfo instanceof EmptyTransformation){
                    
                    EmptyTransformation empt = (EmptyTransformation) transfo;
                    System.err.println("MESSAGE : " + empt.getMessage());
                    result = 1;
                }
                
            }
           
            /*
            t2 = sqlF.loadTable("testIMPTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKIMP"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testIMP()");
            }
            */
            
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
                    
        } catch (Exception ex) {
            ErrorGestion(ex, "testIMP", new ArrayList<>(Arrays.asList("testIMPTable2", "testIMPTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    //This method allows us to test if the methods for directly and rapidely executing SQL instructions work (SQLFree).
   @Test
    public void testSQLFree() {
        int result = 0;
        try {
            sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "create table testSQLFreeTable (name varchar(45));").sqlQueryDo();
            sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "insert into testSQLFreeTable(name) values (\"coucou\" );").sqlQueryDo();
            sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "ALTER TABLE testSQLFreeTable MODIFY COLUMN name varchar(10);").sqlQueryDo();
            ResultSet res = (ResultSet) sqlF.createSQLCreateFreeQuery(SQLQueryType.Getter, "select * from testSQLFreeTable;").sqlQueryDo();
            res.first();
            
            if(!(res.getString(1).equals("coucou"))){
                System.err.println("Wrong SQLFreeQuery");
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testSQLFree()");
                result = 1;
            }
            sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "drop table testSQLFreeTable;").sqlQueryDo();
            
        } catch (Exception ex) {
            ErrorGestion(ex, "testSQLFree", new ArrayList<>(Arrays.asList("testSQLFreeTable")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    
     @Test
    public void testTriggerQuery() {

        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int"));
            Table t1 = new Table("testTrigger1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "int"));
            
            Table t2 = new Table("testTrigger2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();                        
            listCol3.add(new Column("foreignKeyName", "varchar(100) NOT NULL"));
            listCol3.add(new Column("foreignKeyTable", "varchar(100) NOT NULL"));
            listCol3.add(new Column("foreignKeyColumn", "varchar(100) NOT NULL"));
            listCol3.add(new Column("referencedTable", "varchar(100) NOT NULL"));
            listCol3.add(new Column("referencedColumn", "varchar(100) NOT NULL"));
            listCol3.add(new Column("problemAction", "varchar(40) NOT NULL"));
            listCol3.add(new Column("dateAndTime", "TimeStamp NOT NULL DEFAULT now()"));
            
            Table t3 = new Table("tableTriggerLog", listCol3);
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testTrigger2", "2id", "1id", "testTrigger1", "TriggerFk");
            
            SQLCreateTriggerQuery tri = sqlF.createSQLcreateTriggerQuery(fk, "tableTriggerLog");
            tri.sqlQueryDo();
            
            SQLInsertQuery ins1 = sqlF.createSQLInsertQuery("testTrigger1", new String[]{"1"});
            ins1.sqlQueryDo();
            SQLInsertQuery ins2 = sqlF.createSQLInsertQuery("testTrigger2", new String[]{"1"});
            ins2.sqlQueryDo();
            SQLDeleteQuery ins3 = sqlF.createSQLDeleteQuery("testTrigger2", "testTrigger2.2id=\"1\"");
            ins3.sqlQueryDo();
            SQLUpdateQuery ins4 = sqlF.createSQLUpdateQuery("testTrigger1", new String[][]{{"1id", "2"}}, "testTrigger1.1id=\"1\"");
            ins4.sqlQueryDo();
            SQLInsertQuery ins5 = sqlF.createSQLInsertQuery("testTrigger2", new String[]{"2"});
            ins5.sqlQueryDo();
            SQLUpdateQuery ins6 = sqlF.createSQLUpdateQuery("testTrigger2", new String[][]{{"2id", "3"}}, "testTrigger2.2id=\"2\"");
            ins6.sqlQueryDo();
                                    
            SQLQueryFree free = sqlF.createSQLCreateFreeQuery(SQLQueryType.Getter, "select COUNT(*) from tableTriggerLog");
            ResultSet count = free.sqlQueryDo();
            count.next();
            if(!count.getString(1).equals("4")){
                result = 1;
                System.err.println("trigger problem + " + count.getString(1));
            }
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            add3.sqlQueryUndo();

            System.out.println("ok");

        } catch (Exception ex) {
            ErrorGestion(ex, "testTriggerQuery", new ArrayList<>(Arrays.asList("testTrigger2", "testTrigger1", "tableTriggerLog")));
            result = 1;
        }
        assertEquals(0, result);
    }
    
    /*
    //permet de tester si la méthode de création de transaction fonctionne.
    @Test
    public void testTransaction(){
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int"));
            listCol1.add(new Column("1name", "varchar(10)"));
            Table t1 = new Table("testTransactionTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "tinyInt"));
          
            Table t2 = new Table("testTransactionTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("testTransactionTable1", new String[]{"1", "Strong"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTransactionTable1", new String[]{"2", "Strang"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testTransactionTable2", new String[]{"1"}).sqlQueryDo();
            
            SQLTransactionQuery transac = sqlF.creatTransactionQuery();
            transac.addQuery(sqlF.createSQLAlterModifyColumnTypeQuery("testTransactionTable2", new Column("2id", "int")));
            transac.addQuery(sqlF.createSQLAlterAddForeignKeyQuery("testTransactionTable2", "fk1", new Column("2id", "int"), "testTransactionTable1", "1id"));
            transac.addQuery(sqlF.createSQLUpdateQuery("testTransactionTable2", new String[][]{{"2id", "2"}}, "1=1"));
            transac.sqlQueryDo();
            result = ForeignKeyAnalyser("testTransaction", "testTransactionTable2", "2id", "fk1");
            transac.sqlQueryUndo();
            
            
            try{
                ForeignKeyAnalyser("testTransaction", "testTransactionTable2", "2id", "fk1");
                result = 1;
            } catch (SQLException ex){}
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            
        } catch (Exception ex) {
            ErrorGestion(ex, "testTransaction", new ArrayList<>(Arrays.asList("testTransactionTable2", "testTransactionTable1")));
            result = 1;     
        }
        assertEquals(0, result);
    }
    */
    /*
    //permet de tester si lors d'une transformation, la méthode transformation regarde bien en cascade si on doit modifier les 
    //types des colonne de foreign key pointant vers la première table référencée et ainsi de suite.
    @Test 
    public void testTransfoCascade(){
        int result = 0;
        try{
           ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "varchar(10)"));
            Table t1 = new Table("transfoCascade1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            //sqlF.createSQLInsertQuery("transfoCascade1", new String[]{"Strong"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("transfoCascade1", new String[]{"Str"}).sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(5)")); 
            Table t2 = new Table("transfoCascade2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            //sqlF.createSQLInsertQuery("transfoCascade2", new String[]{"s"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("transfoCascade2", new String[]{"st"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("transfoCascade2", new String[]{"str"}).sqlQueryDo();
            
            ArrayList<Column> listCol3 = new ArrayList<>();
            listCol3.add(new Column("3id", "varchar(6)"));
            Table t3 = new Table("transfoCascade3", listCol3, new ArrayList<ForeignKey>(), "3id");
            SQLCreateTableQuery add3 = sqlF.createSQLCreateTableQuery(t3);
            add3.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("transfoCascade3", new String[]{"str"}).sqlQueryDo();
            
            ArrayList<Column> listCol4 = new ArrayList<>();
            listCol4.add(new Column("4id", "varchar(6)"));
            Table t4 = new Table("transfoCascade4", listCol4, new ArrayList<ForeignKey>(), "4id");
            SQLCreateTableQuery add4 = sqlF.createSQLCreateTableQuery(t4);
            add4.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("transfoCascade4", new String[]{"str"}).sqlQueryDo();
            
            ArrayList<Column> listCol5 = new ArrayList<>();
            listCol5.add(new Column("5id", "varchar(7)"));
            Table t5 = new Table("transfoCascade5", listCol5, new ArrayList<ForeignKey>(), "5id");
            SQLCreateTableQuery add5 = sqlF.createSQLCreateTableQuery(t5);
            add5.sqlQueryDo();
            
            sqlF.createSQLInsertQuery("transfoCascade5", new String[]{"str"}).sqlQueryDo();
            //sqlF.createSQLInsertQuery("transfoCascade5", new String[]{"nop"}).sqlQueryDo();
            
            ForeignKey fk1 = new ForeignKey("transfoCascade1", "1id", "2id", "transfoCascade2", "FK1");
            ForeignKey fk2 = new ForeignKey("transfoCascade2", "2id", "3id", "transfoCascade3", "FK2");
            ForeignKey fk3 = new ForeignKey("transfoCascade2", "2id", "4id", "transfoCascade4", "FK3");
            ForeignKey fk4 = new ForeignKey("transfoCascade4", "4id", "5id", "transfoCascade5", "FK4");
            
            sqlF.createSQLAlterAddForeignKeyQuery("transfoCascade3", fk2).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("transfoCascade4", fk3).sqlQueryDo();
            sqlF.createSQLAlterAddForeignKeyQuery("transfoCascade5", fk4).sqlQueryDo();
            
            HashMap<String, Table> hm = new HashMap();
            hm.put("transfoCascade1", t1);
            hm.put("transfoCascade2", t2);
            hm.put("transfoCascade3", t3);
            hm.put("transfoCascade4", t4);
            hm.put("transfoCascade5", t5);
            
            
            DBTransformation bdt1 = new DBTransformation("localhost/mydb", "3306", "root", "root", hm, fk1, TransformationTarget.ForeignKeyTable, "varchar(10)");
            bdt1.analyse();
            
            System.out.println("********************************");
            bdt1.getCascadeFk().forEach(f->System.out.println(f.getConstraintName()));
            System.out.println("********************************");
            bdt1.getUnmatchingValue().forEach(System.out::println);
             
            t2 = sqlF.loadTable("transfoCascade2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            
            if (!(lfk.get(0).getForeingKeyColumn().equals("2id") && lfk.get(0).getConstraintName().equals("FK1"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testNTT()");
            }
            if (!(hm.get("transfoCascade5").getTablecolumn().get(0).getColumnType().equals("varchar(10)"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testANTT()");
            }
            add5.sqlQueryUndo();
            add4.sqlQueryUndo();
            add3.sqlQueryUndo();
            add2.sqlQueryUndo();
            add1.sqlQueryUndo();
            
        }catch (Exception ex) {
            ErrorGestion(ex, "testTransformationCascade", new ArrayList<>(Arrays.asList("transfoCascade5", "transfoCascade4", "transfoCascade3", "transfoCascade2", "transfoCascade1")));
            result = 1; 
        }
        assertEquals(0, result);
    }
*/
}
