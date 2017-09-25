/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import SQLQuery.Column;
import SQLQuery.SQLQueryFactory;
import SQLQuery.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thibaud
 */
public class Test {
    public static void main(String[] args) {
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
            
            
            
            
            sqlF.createSQLDeleteQuery("patient", new String[][]{{"idPatient", "69"},{"firstName", "lola"},{"lastName", "zaza"}, {"age", "22"}, {"idHospital", "1"}, {"idAddress", "1"}}).sqlQueryDo();
            
            
            /*            
            sqlF.createSQLUpdateQuery("patient", new String[][]{{"idPatient", "69"}, {"firstName", "lola"}}, new String[][]{{"idPatient", "5"},{"firstName", "zoulou"},{"lastName", "zaza"}, {"age", "22"}, {"idHospital", "1"}, {"idAddress", "1"}}).sqlQueryDo();
            */
            /*
            //test creatable + droptable + undoDroptable
            sqlF.creatSQLCreateTableQuery("carl", new String[]{"id int"," taille int "," bof varchar(33)"}).sqlQueryDo();
            SQLDropTableQuery drop = sqlF.creatDropTableQuery("carl");
            drop.sqlQueryDo();
            drop.sqlQueryUndo();
            */
            
            /*
            //test Select
            SQLSelectQuery s =  sqlF.createSQLSelectQuery("carl", new String[]{"*"}, "");
            
            ResultSet rs = s.sqlQueryDo();
                while(rs.next()){
                    System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3) );
                }
            */
            
            System.out.println("voilà");
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
