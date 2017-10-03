
package Transformation;

import EasySQL.ForeignKey;
import EasySQL.SQLQuery;
import EasySQL.SQLQueryFactory;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *
 * @author carl_
 */
public abstract class DBTransformation {
    
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    
    private SQLQueryFactory sqlFactory;
    private ForeignKey fk;
    private ArrayList<SQLQuery> listQuery;

    public String getDataBaseHostName() {
        return dataBaseHostName;
    }

    public String getDataBasePortNumber() {
        return dataBasePortNumber;
    }

    public String getDataBaseLogin() {
        return dataBaseLogin;
    }

    public SQLQueryFactory getSQLFactory() {
        return sqlFactory;
    }

    public ForeignKey getFk() {
        return fk;
    }

    public void setFk(ForeignKey fk) {
        this.fk = fk;
    }
    
    public void transfrom() throws SQLException{
        for(SQLQuery query : listQuery){
            query.sqlQueryDo();
        }
    }    
    public void unDoTransformation() throws SQLException{
        for (int i=(this.listQuery.size()-1);i>0;i--){
            listQuery.get(i).sqlQueryUndo();
        }
    }
    
    

    public DBTransformation(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ForeignKey fk) {
        this.dataBaseHostName = dataBaseHostName;
        this.dataBasePortNumber = dataBasePortNumber;
        this.dataBaseLogin = dataBaseLogin;
        this.dataBasePassword = dataBasePassword;
        this.sqlFactory = new SQLQueryFactory(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fk = fk;
    }

    

}
