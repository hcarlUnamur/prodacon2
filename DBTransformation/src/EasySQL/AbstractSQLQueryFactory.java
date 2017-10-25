
package EasySQL;

import EasySQL.Exception.DBConnexionErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractSQLQueryFactory {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://%s";
    static final String DB_URL2 = "jdbc:mysql://%s:%s/%s";
    
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    private Connection conn;
    private String dbName;

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
    
    public AbstractSQLQueryFactory(String dataBaseHostName,String dbName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        try {
            this.dataBaseHostName = dataBaseHostName;
            this.dataBasePortNumber = dataBasePortNumber;
            this.dataBaseLogin = dataBaseLogin;
            this.dataBasePassword = dataBasePassword;
            this.dbName= dbName;
            
            Class.forName("com.mysql.jdbc.Driver");
            
            Properties props = new Properties();
            props.setProperty("user",dataBaseLogin);
            props.setProperty("password",dataBasePassword);
            props.setProperty("u‌​seSSL","true");       
            
            conn = DriverManager.getConnection(String.format(DB_URL2,dataBaseHostName,dataBasePortNumber,dbName),props);
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AbstractSQLQueryFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(AbstractSQLQueryFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBConnexionErrorException("sqlfactory can't create connexion with database ");
        }

    
    }

    
}
