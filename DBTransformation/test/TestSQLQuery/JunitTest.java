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
import EasySQL.SQLQueryType;
import EasySQL.SQLSelectQuery;
import EasySQL.SQLTransactionQuery;
import EasySQL.SQLUpdateQuery;
import EasySQL.Table;
import Transformation.DBTransformation;
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
 * MEMO : Si tout plante, il y a des chances pour que ce ne soit que le select
 * ou/et le drop qui soit problèmatiques, puisque s'ils ne fonctionnent pas,
 * impossible d'effacer toutes les tables créés dans ces tests.
 *
 */
public class JunitTest {

    
    SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");

    //méthode servant à créer rapidement une table. Cette méthode n'est pas un test.
    public Table CreateTable1(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("trueFalse", "bool"));
        Table t1 = new Table(tableName, listCol);
        return t1;
    }

    //méthode servant à créer rapidement une autre table. Cette méthode n'est pas un test.
    public Table CreateTable2(String tableName) {
        ArrayList<Column> listCol = new ArrayList<>();
        listCol.add(new Column("id", "int"));
        listCol.add(new Column("name", "varchar(45)"));
        listCol.add(new Column("reference", "int"));
        Table t2 = new Table(tableName, listCol);
        return t2;
    }

    //gère une exception arrivant dans n'importe quel test. On imprimme le message de l'exception,
    //de quel test cette exception vient et enfin on détruit toutes les tables construites dans ce test.
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

    //permet d'analyser rapidement si pour une table, un nom de colonne et un type de colonne donné, le type de la colonne de la table est bien le type donné en paramètre.
    //si ce n'est pas le cas fait échouer le test (return 1).
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
    
    //permet d'analyser rapidement si pour une table et une colonne donnée, la colonne de cette table est une primary key ou non?
    //si ce n'est pas le cas, fait échouer le test (result = 1);
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

    //permet d'analyser rapidement si pour une table, un nom de colonne et un nom de contrainte donné, la colonne est une foreign key du nom du paramètre donné?
    //si ce n'est pas le cas, fait échouer le test (result = 1)
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

    //permet de vérifier rapidemment si pour une liste de colonne et une liste de valeurs données, la première ligne de la table à bien les même valeurs que la liste de valeurs passée en paramètre.
    //si ce n'est pas le cas, fait échouer le test (result = 1).
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

    //permet de rapidement peupler une table donnée avec des valeurs prédéfinie (typiquement, il faut utiliser cette méthode uniquement sur une table crée avec les méthodes
    //CreateTable1 ou CreateTable2.
    public void PeuplerTable(String tableName) throws SQLException {
        sqlF.createSQLInsertQuery(tableName, new String[]{"1", "Strong", "1"}).sqlQueryDo();
        sqlF.createSQLInsertQuery(tableName, new String[]{"6", "String", "1"}).sqlQueryDo();
        sqlF.createSQLInsertQuery(tableName, new String[]{"5", "Strang", "0"}).sqlQueryDo();
    }

    //permet de vérifier rapidemment si la première ligne d'une table correspond à au moins une des ligne d'une table sortant de la méthode PeuplerTable
    //si ce n'est pas le cas, fait échouer le test (result = 1)
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

    //permet de tester si la première méthode d'ajout de table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la deuxième méthode d'ajout de table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la troisième méthode d'ajout de table en BD ainsi que son Undo fonctionne.
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
    
    //permet de tester si la premire méthode de drop de table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la deuxième méthode de drop de table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la première méthode de delete de valeur dans une table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la deuxième méthode de delete de valeur dans une table en BD ainsi que son Undo fonctionne.
    @Test
    public void testInsertDeleteQuery2() {

        int result = 0;
        try {
            sqlF.createSQLCreateTableQuery("testInsertTable2", new String[]{"id int", "name varchar(45)", "trueFalse bool"}).sqlQueryDo();

            sqlF.createSQLInsertQuery("testInsertTable2", new String[]{"id", "trueFalse"}, new String[]{"1", "1"}).sqlQueryDo();

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
            sqlF.createDropTableQuery("testInsertTable2").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "testInsertDeleteQuery2", new ArrayList<>(Arrays.asList("testInsertTable2")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //permet de tester si la méthode d'update de valeur dans une table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la méthode de select de valeur dans une table en BD ainsi que son Undo fonctionne.
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

    //permet de tester si la méthode d'ajout de primary key dans une table en BD ainsi que son Undo fonctionne.
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
            pk.sqlQueryDo();

            result = PrimaryKeyAnalyser("AlterAddDropPrimaryKeyQuery", "testAddPrimaryKeyTable", "id");

            sqlF.createSQLAlterDropPrimaryKeyQuery("testAddPrimaryKeyTable").sqlQueryDo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropPrimaryKeyQuery", new ArrayList<>(Arrays.asList("testAddPrimaryKeyTable")));
            result = 1;
        }
        assertEquals(0, result);
    }
    
    //permet de tester si la méthode d'ajout de foreign key dans une table vers une autre en BD ainsi que son Undo fonctionne.
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
            sqlF.createSQLAlterAddForeignKeyQuery(t2.getName(), fk).sqlQueryDo();

            result = ForeignKeyAnalyser("AlterAddDropForeignKeyQuery", "testAddForeignKeyTable2", "reference", "FK1");

            sqlF.createSQLAlterDropForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), t1).sqlQueryDo();
            sqlF.createDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();
            sqlF.createDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropForeignKeyQuery", new ArrayList<>(Arrays.asList("testAddForeignKeyTable2", "testAddForeignKeyTable1")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //permet de tester si la méthode d'ajout et de supprémtion de colonnes dans une table en BD ainsi que leur Undo fonctionne.
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

            sqlF.createSQLAlterDropColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)")).sqlQueryDo();
            add.sqlQueryUndo();
        } catch (Exception ex) {
            ErrorGestion(ex, "AlterAddDropColumnQuery", new ArrayList<>(Arrays.asList("testAddDropColumnTable")));
            result = 1;
        }
        assertEquals(0, result);
    }

    //permet de tester si la méthode de modification du type d'une colonne dans une table en BD ainsi que son Undo fonctionne.
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
    /*
    @Test
    public void testMVMT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("id", "varchar(45)"));
            listCol1.add(new Column("name", "varchar(45)"));
            listCol1.add(new Column("trueFalse", "bool"));
            Table t1 = new Table("testMVMTTable1", listCol1, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testMVMTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testMVMTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testMVMTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMVMTTable1", "id", "reference", "FKMVMT");
             
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
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("id", "varchar(45)"));
            listCol2.add(new Column("city", "varchar(45)"));
            listCol2.add(new Column("reference", "varchar(45)"));
            
            Table t2 = new Table("testMBTTable2", listCol2, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testMBTTable1", new String[]{"coucou", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testMBTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testMBTTable1", "id", "reference", "FKMBT");
             
            
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
            HashMap<String, Table> hm = new HashMap();
            hm.put("testLMTTTable1", t1);
            hm.put("testLMTTTable2", t2);
            LMTT m = new LMTT("localhost/mydb", "3306", "root", "root", "testLMTTTable2", fk, hm, "float(6,2)");
            m.transfrom();  
            
            t2 = sqlF.loadTable("testLMTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            
            if (!(lfk.get(0).getForeingKeyColumn().equals("2reference") && lfk.get(0).getConstraintName().equals("FKLMTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testLMTT()");
            }
            if (!(hm.get("testLMTTTable2").getTablecolumn().get(2).getColumnType().equals("float(6,2)"))) {
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
            listCol1.add(new Column("1id", "varchar(45)"));
            listCol1.add(new Column("1city", "varchar(10)"));
            listCol1.add(new Column("1reference", "bool"));
            Table t1 = new Table("testANTTTable1", listCol1, new ArrayList<ForeignKey>(), "1city");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "varchar(45)"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "varchar(45)"));
            
            Table t2 = new Table("testANTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testANTTTable1", new String[]{"CouCou", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testANTTTable2", new String[]{"deux", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testANTTTable1", "1city", "2city", "testANTTTable2", "FKANTT");
            
            HashMap<String, Table> hm = new HashMap();
            hm.put("testANTTTable1", t1);
            hm.put("testANTTTable2", t2);
            LMTT m = new LMTT("localhost/mydb", "3306", "root", "root", "testANTTTable2", fk, hm, "varchar(45)");
            m.transfrom();  
            
            t2 = sqlF.loadTable("testANTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            
            if (!(lfk.get(0).getForeingKeyColumn().equals("2city") && lfk.get(0).getConstraintName().equals("FKANTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testANTT()");
            }
            if (!(hm.get("testANTTTable2").getTablecolumn().get(2).getColumnType().equals("varchar(45)"))) {
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
    public void testDTT() {
        int result = 0;
        try {
            ArrayList<Column> listCol1 = new ArrayList<>();
            listCol1.add(new Column("1id", "int"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "bool"));
            Table t1 = new Table("testDTTTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
           
            //this method is used 
                       
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "int"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "varchar(45)"));
            
            Table t2 = new Table("testDTTTable2", listCol2, new ArrayList<ForeignKey>(), "2id");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            sqlF.createSQLInsertQuery("testDTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testDTTTable2", new String[]{"2", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testDTTTable1", "1id", "2city", "FKDTT");
            
            DTT m = new DTT("localhost/mydb", "3306", "root", "root", "testDTTTable2", fk);
            m.transfrom();  
            
            t2 = sqlF.loadTable("testDTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            
            if (!(lfk.get(0).getForeingKeyColumn().equals("2city") && lfk.get(0).getConstraintName().equals("FKDTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testDTT()");
            }
            
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
            listCol1.add(new Column("1id", "tinyInt"));
            listCol1.add(new Column("1city", "varchar(45)"));
            listCol1.add(new Column("1reference", "bool"));
            Table t1 = new Table("testNTTTable1", listCol1, new ArrayList<ForeignKey>(), "1id");
            SQLCreateTableQuery add1 = sqlF.createSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList<>();
            listCol2.add(new Column("2id", "int"));
            listCol2.add(new Column("2city", "varchar(45)"));
            listCol2.add(new Column("2reference", "varchar(45)"));
            
            Table t2 = new Table("testNTTTable2", listCol2, new ArrayList<ForeignKey>(), "2city");
            SQLCreateTableQuery add2 = sqlF.createSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            
            
            sqlF.createSQLInsertQuery("testNTTTable1", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLInsertQuery("testNTTTable2", new String[]{"1", "Strong", "coucou"}).sqlQueryDo();
            
            ForeignKey fk = new ForeignKey("testNTTTable1", "1id", "2id", "testNTTTable2", "FKNTT");
            HashMap<String, Table> hm = new HashMap();
            hm.put("testNTTTable1", t1);
            hm.put("testNTTTable2", t2);
            LMTT m = new LMTT("localhost/mydb", "3306", "root", "root", "testNTTTable2", fk, hm, "int");
            m.transfrom();  
            
            t2 = sqlF.loadTable("testNTTTable2");
            ArrayList<ForeignKey> lfk = new ArrayList<>();
            lfk = t2.getForeignKeys();
            
            if (!(lfk.get(0).getForeingKeyColumn().equals("2id") && lfk.get(0).getConstraintName().equals("FKNTT"))) {
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.testNTT()");
            }
            if (!(hm.get("testNTTTable1").getTablecolumn().get(0).getColumnType().equals("int"))) {
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
    */
    //permet de tester si les methodes permettant d'exécuter rapidement des instruction SQL directement (SQLFree) fonctionnent.
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
