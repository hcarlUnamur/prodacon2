/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

import EasySQL.AbstractSQLQueryFactory;
import EasySQL.ForeignKey;
import EasySQL.SQLQueryFactory;
import EasySQL.Table;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carl_
 */
public class LMTT extends TypeMismatching {

    public LMTT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) throws ClassNotFoundException, SQLException {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        
        //récupérer le type de la colonne référente et le type de la colonne référée.
        //déterminer lequel est le plus grand
        //tranformer le type le plus petit en type le plus grand
        /*
        Connection conn;
        
            Class.forName("com.mysql.jdbc.Driver");
            
            Properties props = new Properties();
            props.setProperty("user",dataBaseLogin);
            props.setProperty("password",dataBasePassword);
            props.setProperty("ssl","true");
            
            conn = DriverManager.getConnection(String.format("jdbc:mysql://%s",dataBaseHostName),props);
            
            
        
        
        try {
            SQLQueryFactory sqlF = new SQLQueryFactory("localhost/mydb", "3306", "root", "root");
            String fke = fk.getForeingKeyColumn();
            Statement stmt = conn.createStatement();
            String query = "SELECT NUMERIC_PRECISION FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '"+tableName+"' AND column_name = '"+fke+"';";
            ResultSet out = stmt.executeQuery(query);
            
            out.next();
            System.out.println("aaaaaaaa" + out.getString(1));
            query = "SELECT NUMERIC_PRECISION FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '"+fk.getReferencedTableName()+"' AND column_name = '"+fk.getReferencedColumn()+"';";
            out = stmt.executeQuery(query);
            
            out.next();
            System.out.println("aaaaaaaa" + out.getString(1));
            
           
           
        } catch (Exception ex) {
            Logger.getLogger(MVMT.class.getName()).log(Level.SEVERE, " Erro during the database loading ", ex);
        }
        */
    }

    public LMTT(SQLQueryFactory sqlFactory, ForeignKey fk) {
        super(sqlFactory, fk);
    }
   
    @Override
    public void analyseValues() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void analyseCascade(ArrayList<Table> tables) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
