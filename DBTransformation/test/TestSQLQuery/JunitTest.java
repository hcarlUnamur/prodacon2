/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import SQLQuery.Column;
import SQLQuery.SQLCreateTableQuery;
import SQLQuery.SQLDeleteQuery;
import SQLQuery.SQLDropTableQuery;
import SQLQuery.SQLQueryFactory;
import SQLQuery.StringTool;
import SQLQuery.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 */
public class JunitTest {
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
 
    
    @Test
    public void testCreateTableQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery("testCreateTable1", new String[]{"id int","name varchar(45)","trueFalse bool"});
            add1.sqlQueryDo();
            add1.sqlQueryUndo();
            
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table test2 = new Table("testCreateTable2", listCol);
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(test2);
            add2.sqlQueryDo();
            add2.sqlQueryUndo();   
            add2.sqlQueryDo();
            add2.sqlQueryUndo();
            
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.testCreateTableQuery()");
            try {sqlF.creatDropTableQuery("testCreateTable1").sqlQueryDo();} catch (SQLException ex1) {}
            try {sqlF.creatDropTableQuery("testCreateTable2").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testDropTableQuery() {
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testDropTable1", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            SQLDropTableQuery drop1 = sqlF.creatDropTableQuery("testDropTable1");
            drop1.sqlQueryDo();
            drop1.sqlQueryUndo();
            drop1.sqlQueryDo();
            
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table test2 = new Table("testDropTable2", listCol);
            sqlF.creatSQLCreateTableQuery(test2).sqlQueryDo();
            
            SQLDropTableQuery drop2 = sqlF.creatDropTableQuery("testDropTable2");
            drop2.sqlQueryDo();
            drop2.sqlQueryUndo();
            drop2.sqlQueryDo();
            
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.testDropTableQuery()");
            try {sqlF.creatDropTableQuery("testDropTable1").sqlQueryDo();} catch (SQLException ex1) {}
            try {sqlF.creatDropTableQuery("testDropTable2").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testInsertDeleteQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testInsertTable", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testInsertTable", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            SQLDeleteQuery del = sqlF.createSQLDeleteQuery("testInsertTable", "id = \"1\" && name = \"Strong\" && trueFalse = \"1\"");
            del.sqlQueryDo();
            //del.sqlQueryUndo();
            sqlF.creatDropTableQuery("testInsertTable").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.testInsertDeleteQuery()");
            try {sqlF.creatDropTableQuery("testInsertTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testUpdateQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testUpdateTable", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testUpdateTable", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            String where ="id=1 and name=\"Strong\" and trueFalse=\"1\" "; //new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}
            sqlF.createSQLUpdateQuery("testUpdateTable", new String[][]{{"id", "6"}, {"name", "Smith"}, {"trueFalse", "0"}}, where).sqlQueryDo();
            sqlF.creatDropTableQuery("testUpdateTable").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.testUpdateQuery()");
            try {sqlF.creatDropTableQuery("testUpdateTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testSelectQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("testSelectTable", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testSelectTable", new String[]{"3", "Strong", "1"}).sqlQueryDo();
            
            ResultSet essai = sqlF.createSQLSelectQuery("testSelectTable", new String[]{"id", "name", "trueFalse"}, "id = '3'").sqlQueryDo();
            sqlF.creatDropTableQuery("testSelectTable").sqlQueryDo();
            essai.first();
            if(!(essai.getString(1).equals("3") && essai.getString(2).equals("Strong") && essai.getString(3).equals("1"))){
                System.out.println("Wrong Select Query");
                System.out.println("ko! : " + "TestSQLQuery.JunitTest.testSelectQuery()");
                result = 1;
            }else{
                System.out.println("ok");
            }
            
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.testSelectQuery()");
            try {sqlF.creatDropTableQuery("testSelectTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void AlterAddPrimaryKeyQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            ArrayList<Column> listCol = new ArrayList(); listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table t1 = new Table("testAddPrimaryKeyTable", listCol);
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            sqlF.creatSQLAlterAddPrimaryKeyQuery("testAddPrimaryKeyTable", "id").sqlQueryDo();
            add.sqlQueryUndo();     
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddPrimaryKeyQuery()");
            try {sqlF.creatDropTableQuery("testAddPrimaryKeyTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void AlterAddForeignKeyQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            ArrayList<Column> listCol1 = new ArrayList(); listCol1.add(new Column("id","int")); listCol1.add(new Column("name", "varchar(45)"));  listCol1.add(new Column("trueFalse",  "bool"));
            Table t1 = new Table("testAddForeignKeyTable1", listCol1);
            SQLCreateTableQuery add1 = sqlF.creatSQLCreateTableQuery(t1);
            add1.sqlQueryDo();
            
            ArrayList<Column> listCol2 = new ArrayList(); listCol2.add(new Column("id","int")); listCol2.add(new Column("name", "varchar(45)"));  listCol2.add(new Column("reference",  "int"));
            Table t2 = new Table("testAddForeignKeyTable2", listCol2);
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(t2);
            add2.sqlQueryDo();
            
            sqlF.creatSQLAlterAddPrimaryKeyQuery("testAddForeignKeyTable1", "id").sqlQueryDo();
            sqlF.creatSQLAlterAddForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), "testAddForeignKeyTable1", "id").sqlQueryDo();
            
            sqlF.creatDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();
            sqlF.creatDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddForeignKeyQuery()");
            try {sqlF.creatDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();} catch (SQLException ex1) {}
            try {sqlF.creatDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    
    
   
}
