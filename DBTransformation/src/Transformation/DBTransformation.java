/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

/**
 *
 * @author carl_
 */
public abstract class DBTransformation {
    
    public String dataBaseHostName;
    public String dataBasePortNumber;
    public String dataBaseLogin;
    public String dataBasePassword;
    
    public abstract void transfrom();    
    public abstract void unDoTransformation();

    public DBTransformation(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword) {
        this.dataBaseHostName = dataBaseHostName;
        this.dataBasePortNumber = dataBasePortNumber;
        this.dataBaseLogin = dataBaseLogin;
        this.dataBasePassword = dataBasePassword;
    }

    

}
