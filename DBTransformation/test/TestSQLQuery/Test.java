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
            sqlF.createSQLDeleteQuery("patient", new String[]{"idPatient", "firstName", "lastName", "age", "idHospital", "idAddress"}, new String[]{"2", "zoulou", "zaza", "22", "1", "1"}).sqlQueryDo();
            System.out.println("voilà");
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
