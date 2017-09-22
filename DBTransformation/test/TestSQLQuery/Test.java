/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestSQLQuery;

import SQLQuery.SQLQueryFactory;
import java.sql.SQLException;
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
            sqlF.createSQLUpdateQuery("patient", new String[]{"idPatient", "firstName", "lastName", "age", "idHospital", "idAddress"}, new String[]{"4", "zoulou", "zaza", "22", "1", "1"}, new String[]{"firstName", "lastName"}, new String[]{"coucou", "loulou"}).sqlQueryDo();
            
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
