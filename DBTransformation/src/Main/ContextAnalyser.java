/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author carl_
 */
public class ContextAnalyser {
    public String DBHostName;
    public String DBPortNumber;
    public String DBLogin;
    public String DBPassword;

    public ContextAnalyser(String DBHostName, String DBPortNumber, String DBLogin, String DBPassword) {
        this.DBHostName = DBHostName;
        this.DBPortNumber = DBPortNumber;
        this.DBLogin = DBLogin;
        this.DBPassword = DBPassword;
    }
    
    public void exec(){
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void doThisAndWeWillMakeMoney(){
        this.exec();
    }
    
    public String getDBHostName() {
        return DBHostName;
    }

    public void setDBHostName(String DBHostName) {
        this.DBHostName = DBHostName;
    }

    public String getDBPortNumber() {
        return DBPortNumber;
    }

    public void setDBPortNumber(String DBPortNumber) {
        this.DBPortNumber = DBPortNumber;
    }

    public String getDBLogin() {
        return DBLogin;
    }

    public void setDBLogin(String DBLogin) {
        this.DBLogin = DBLogin;
    }

    public String getDBPassword() {
        return DBPassword;
    }

    public void setDBPassword(String DBPassword) {
        this.DBPassword = DBPassword;
    }
    
    
    
}
