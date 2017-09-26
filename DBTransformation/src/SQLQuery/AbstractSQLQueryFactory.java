/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thibaud
 */
public abstract class AbstractSQLQueryFactory {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://%s";
    
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    private Connection conn;

    public Connection getConn() {
        return conn;
    }
    
    
    
    public AbstractSQLQueryFactory(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        try {
            this.dataBaseHostName = dataBaseHostName;
            this.dataBasePortNumber = dataBasePortNumber;
            this.dataBaseLogin = dataBaseLogin;
            this.dataBasePassword = dataBasePassword;
            
            Class.forName("com.mysql.jdbc.Driver");
            
            Properties props = new Properties();
            props.setProperty("user",dataBaseLogin);
            props.setProperty("password",dataBasePassword);
            props.setProperty("ssl","true");
            
            conn = DriverManager.getConnection(String.format(DB_URL,dataBaseHostName),props);
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AbstractSQLQueryFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AbstractSQLQueryFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    
    }

    
}
