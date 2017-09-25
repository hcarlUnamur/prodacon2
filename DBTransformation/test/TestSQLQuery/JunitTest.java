/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import SQLQuery.Column;
import SQLQuery.SQLCreateTableQuery;
import SQLQuery.SQLDropTableQuery;
import SQLQuery.SQLQueryFactory;
import SQLQuery.StringTool;
import SQLQuery.Table;
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
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testCreateTableQuery() {
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery("test", new String[]{"id int","name varchar(45)","trueFalse bool"});
            add.sqlQueryDo();
            add.sqlQueryUndo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testDropTableQuery() {
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("test", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            SQLDropTableQuery drop = sqlF.creatDropTableQuery("test");
            drop.sqlQueryDo();
            drop.sqlQueryUndo();
            drop.sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testInsertDeleteQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("test", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("test", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLDeleteQuery("test", new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}).sqlQueryDo();
            sqlF.creatDropTableQuery("test").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testUpdateQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("test", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("test", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLUpdateQuery("test", new String[][]{{"id", "6"}, {"name", "Smith"}, {"trueFalse", "0"}}, new String[][]{{"id", "1"}, {"name", "Strong"}, {"trueFalse", "1"}}).sqlQueryDo();
            sqlF.creatDropTableQuery("test").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testSelectQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            sqlF.creatSQLCreateTableQuery("test", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("test", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.createSQLSelectQuery("test", new String[]{"id", "name", "trueFalse"}, "id = '1'").sqlQueryDo();
            sqlF.creatDropTableQuery("test").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }
    /*
    @Test
    public void AlterAddForeignKeyQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            sqlF.creatSQLCreateTableQuery("test1", new String[]{"id int","name varchar(45)","trueFalse bool"}).sqlQueryDo();
            sqlF.creatSQLCreateTableQuery("test2", new String[]{"id int","name varchar(45)","id1 int"}).sqlQueryDo();
            
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id", "int")); listCol.add(new Column("trueFalse", "bool"));
            Table tb = new Table("test1", listCol);
            sqlF.creatSQLAlterAddForeignKeyQuery("test2", "FK1", new Column("idl", "int"), tb);
            
            //sqlF.creatDropTableQuery("test1").sqlQueryDo();
            //sqlF.creatDropTableQuery("test2").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("ko!");
            result = 1;
        }
        assertEquals(0, result);
    }*/
    
    
    
   
}
