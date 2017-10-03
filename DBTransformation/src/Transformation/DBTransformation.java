
package Transformation;

import EasySQL.ForeignKey;
/**
 *
 * @author carl_
 */
public abstract class DBTransformation {
    
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    private ForeignKey fk; 

    public ForeignKey getFk() {
        return fk;
    }

    public void setFk(ForeignKey fk) {
        this.fk = fk;
    }
    
    public abstract void transfrom();    
    public abstract void unDoTransformation();

    public DBTransformation(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ForeignKey fk) {
        this.dataBaseHostName = dataBaseHostName;
        this.dataBasePortNumber = dataBasePortNumber;
        this.dataBaseLogin = dataBaseLogin;
        this.dataBasePassword = dataBasePassword;
        this.fk = fk;
    }

    

}
