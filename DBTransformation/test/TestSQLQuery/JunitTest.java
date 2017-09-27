/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import SQLQuery.Column;
import SQLQuery.ForeignKey;
import SQLQuery.SQLAlterTableQuery;
import SQLQuery.SQLCreateTableQuery;
import SQLQuery.SQLDeleteQuery;
import SQLQuery.SQLDropTableQuery;
import SQLQuery.SQLQueryFactory;
import SQLQuery.SQLSelectQuery;
import SQLQuery.SQLUpdateQuery;
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
 * 
 * MEMO : Si tout plante, il y a des chances pour que ce ne soit que le select ou/et le drop qui soit problèmatique, puisque s'il ne fonctionne pas, impossible d'effacer
 * toutes les tables créés dans ces tests.
 * 
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
            System.err.println(ex);
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
            
            sqlF.creatSQLInsertQuery("testDropTable2", new String[]{"1", "Strong", "1"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testDropTable2", new String[]{"2", "Omar", "0"}).sqlQueryDo();
            sqlF.creatSQLInsertQuery("testDropTable2", new String[]{"560", "Roussette", "1"}).sqlQueryDo();
            
            SQLDropTableQuery drop2 = sqlF.creatDropTableQuery("testDropTable2");
            drop2.sqlQueryDo();
            drop2.sqlQueryUndo();
            
            ResultSet essai = sqlF.createSQLSelectQuery("testDropTable2", new String[]{"id", "name", "trueFalse"}, "id = '1' || id = '560'").sqlQueryDo();
            essai.next(); essai.next();
            if(!(essai.getString(1).equals("560") && essai.getString(2).equals("Roussette") && essai.getString(3).equals("1"))){
                System.out.println("Wrong Select Query");
                System.out.println("ko! : " + "TestSQLQuery.JunitTest.testSelectQuery()");
                result = 1;
            }else{
                System.out.println("ok");
            }
    
    
    
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
            del.sqlQueryUndo();
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
            SQLUpdateQuery upd = sqlF.createSQLUpdateQuery("testUpdateTable", new String[][]{{"id", "6"}, {"name", "Smith"}, {"trueFalse", "0"}}, where);
            upd.sqlQueryDo();
            upd.sqlQueryUndo();
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
    public void AlterAddDropPrimaryKeyQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            ArrayList<Column> listCol = new ArrayList(); listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table t1 = new Table("testAddPrimaryKeyTable", listCol);
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery pk = sqlF.creatSQLAlterAddPrimaryKeyQuery("testAddPrimaryKeyTable", "id");
            
            pk.sqlQueryDo();
            pk.sqlQueryUndo();
            pk.sqlQueryDo();
            
            SQLSelectQuery selectPrimaryKey = sqlF.createSQLSelectQuery(
                        new String[]{"INFORMATION_SCHEMA.COLUMNS"},
                        new String[]{"COLUMN_NAME"},
                        "TABLE_NAME = '"+"testAddPrimaryKeyTable"+"' AND COLUMN_KEY = 'PRI'"
            ); 
            
            ResultSet metaData = (selectPrimaryKey.sqlQueryDo());
            metaData.next();
           
            if (!metaData.getString("COLUMN_NAME").equals("id")){
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropPrimaryKeyQuery()");
            }
               
            sqlF.creatSQLAlterDropPrimaryKeyQuery("testAddPrimaryKeyTable").sqlQueryDo();
            add.sqlQueryUndo();     
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropPrimaryKeyQuery()");
            try {sqlF.creatDropTableQuery("testAddPrimaryKeyTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void AlterAddDropForeignKeyQuery(){
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
            SQLAlterTableQuery fk = sqlF.creatSQLAlterAddForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), "testAddForeignKeyTable1", "id");
            
            fk.sqlQueryDo();

            SQLSelectQuery selectForeignKey = sqlF.createSQLSelectQuery(new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                                                               new String[]{"TABLE_NAME,COLUMN_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
                                                               "REFERENCED_TABLE_NAME = '"+"testAddForeignKeyTable1"+"' AND CONSTRAINT_NAME='"+"FK1"+"'"
                );
            ResultSet metaData = selectForeignKey.sqlQueryDo();
            metaData.next();
            if (!(metaData.getString("COLUMN_NAME").equals("reference")&&metaData.getString("CONSTRAINT_NAME").equals("FK1"))){
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropForeignKeyQuery()");
            }
                
            fk.sqlQueryUndo();
            fk.sqlQueryDo();
            sqlF.creatSQLAlterDropForeignKeyQuery("testAddForeignKeyTable2", "FK1", new Column("reference", "int"), t1).sqlQueryDo();
            sqlF.creatDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();
            sqlF.creatDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();
            System.out.println("ok");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropForeignKeyQuery()");
            try {sqlF.creatDropTableQuery("testAddForeignKeyTable2").sqlQueryDo();} catch (SQLException ex1) {}
            try {sqlF.creatDropTableQuery("testAddForeignKeyTable1").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void AlterAddDropColumnQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            ArrayList<Column> listCol = new ArrayList(); listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table t1 = new Table("testAddDropColumnTable", listCol);
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery col = sqlF.creatSQLAltertableAddColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)"));
            
            col.sqlQueryDo();
            col.sqlQueryUndo();
            col.sqlQueryDo();
            
            SQLSelectQuery selectColumn = sqlF.createSQLSelectQuery(new String[]{"information_schema.columns"},
                                                       new String[]{"column_name","column_type"},
                                                       "table_name='"+"testAddDropColumnTable"+"' AND column_name='"+"city"+"'"
            );
            
            ResultSet metaData = selectColumn.sqlQueryDo();
            metaData.next();
            if (!(metaData.getString("COLUMN_NAME").equals("city")&&metaData.getString("COLUMN_TYPE").equals("varchar(45)"))){
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropColumnQuery()");
            }
            
            sqlF.creatSQLAlterDropColumnQuery("testAddDropColumnTable", new Column("city", "varchar(45)")).sqlQueryDo();
            add.sqlQueryUndo();     
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterAddDropColumnQuery()");
            try {sqlF.creatDropTableQuery("testAddDropColumnTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void AlterModifyColumnTypeQuery(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try{
            ArrayList<Column> listCol = new ArrayList(); listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            Table t1 = new Table("testModifyColumnTypeTable", listCol);
            SQLCreateTableQuery add = sqlF.creatSQLCreateTableQuery(t1);
            add.sqlQueryDo();
            SQLAlterTableQuery mCol = sqlF.creatSQLAlterModifyColumnTypeQuery("testModifyColumnTypeTable", new Column("name", "int"));
            mCol.sqlQueryDo();
            
            
            SQLSelectQuery selectColumn = sqlF.createSQLSelectQuery(new String[]{"information_schema.columns"},
                                                       new String[]{"column_name","column_type"},
                                                       "table_name='"+"testModifyColumnTypeTable"+"' AND column_name='"+"name"+"'"
            );
            
            ResultSet metaData = selectColumn.sqlQueryDo();
            metaData.next();
            
            if (!(metaData.getString("COLUMN_NAME").equals("name")&& metaData.getString("COLUMN_TYPE").equals("int(11)"))){
                result = 1;
                System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterModifyColumnTypeQuery()");
            }
            
            mCol.sqlQueryUndo();
            mCol.sqlQueryDo();
            add.sqlQueryUndo();     
        } catch (SQLException ex) {
            System.out.println(ex);
            System.err.println("ko! : " + "TestSQLQuery.JunitTest.AlterModifyColumnTypeQuery()");
            try {sqlF.creatDropTableQuery("testModifyColumnTypeTable").sqlQueryDo();} catch (SQLException ex1) {}
            result = 1;
        }
        assertEquals(0, result);
    }
    
    @Test
    public void testCreateTable2Query(){
        SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
        int result = 0;
        try {
            
            
            ArrayList<Column> listCol = new ArrayList<>();
            listCol.add(new Column("id","int")); listCol.add(new Column("name", "varchar(45)"));  listCol.add(new Column("trueFalse",  "bool"));
            
            Table test2 = new Table("t2", listCol, new ArrayList<ForeignKey>(), "id");
            SQLCreateTableQuery add2 = sqlF.creatSQLCreateTableQuery(test2);
            add2.sqlQueryDo();
            test2 = sqlF.loadTable("t2");
            //System.out.println("éééééééééééééééééééééé" + test2.getName() + "**" + test2.getPrimaryKey());
            //test2.getTablecolumn().forEach((s)->{System.out.println(s.getColumnName()+" "+s.getColumnType());});
            
            
            
            add2.sqlQueryUndo();   
            //add2.sqlQueryDo();
            //add2.sqlQueryUndo();
            
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
    
   
}
