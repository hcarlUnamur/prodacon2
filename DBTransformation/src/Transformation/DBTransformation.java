package Transformation;

import EasySQL.ForeignKey;
import EasySQL.SQLQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryType;
import EasySQL.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author carl_
 */
public abstract class DBTransformation {
    
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    
    private String tableName;
    private SQLQueryFactory sqlFactory;
    private ForeignKey fk;
    private ArrayList<SQLQuery> listQuery;
    
    private ArrayList<String> unmatchingValue;
    private ArrayList<ForeignKey> cascadeFk;
    private ArrayList<SQLQuery> cascadeTransforamtion;
    private boolean encodageMatching;

    public DBTransformation(SQLQueryFactory sqlFactory,ForeignKey fk) {
        this.tableName=fk.getForeingKeyTable();
        this.listQuery = new ArrayList();
        this.fk = fk;
        this.sqlFactory = sqlFactory;
        this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList();
    }

    public String getDataBasePassword() {
        return dataBasePassword;
    }

    public void setDataBasePassword(String dataBasePassword) {
        this.dataBasePassword = dataBasePassword;
    }

    public SQLQueryFactory getSqlFactory() {
        return sqlFactory;
    }

    public void setSqlFactory(SQLQueryFactory sqlFactory) {
        this.sqlFactory = sqlFactory;
    }

    public ArrayList<SQLQuery> getListQuery() {
        return listQuery;
    }

    public void setListQuery(ArrayList<SQLQuery> listQuery) {
        this.listQuery = listQuery;
    }

    public ArrayList<String> getUnmatchingValue() {
        return unmatchingValue;
    }

    public void setUnmatchingValue(ArrayList<String> unmatchingValue) {
        this.unmatchingValue = unmatchingValue;
    }

    public ArrayList<ForeignKey> getCascadeFk() {
        return cascadeFk;
    }

    public void setCascadeFk(ArrayList<ForeignKey> cascadeFk) {
        this.cascadeFk = cascadeFk;
    }

    public boolean isEncodageMatching() {
        return encodageMatching;
    }

    public void setEncodageMatching(boolean encodageMatching) {
        this.encodageMatching = encodageMatching;
    }

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
    
    public void addQuery(SQLQuery query){
        this.listQuery.add(query);
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

    public String getTableName() {
        return tableName;
    } 

    public DBTransformation(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword,String tableName, ForeignKey fk) {
        this.dataBaseHostName = dataBaseHostName;
        this.dataBasePortNumber = dataBasePortNumber;
        this.dataBaseLogin = dataBaseLogin;
        this.dataBasePassword = dataBasePassword;
        this.tableName=tableName;
        this.listQuery = new ArrayList();
        this.sqlFactory = new SQLQueryFactory(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fk = fk;
        this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList(); 
    }

    public void analyse(ArrayList<Table> tables){
        analyseValues();
        analyseCascade(tables);      
    }
    
    public void makeCascadeTransformation(){
        for(SQLQuery query : cascadeTransforamtion){
            try {
                query.sqlQueryDo();
            } catch (SQLException ex) {
                Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE,"SQL Exception from makeCascadeTransformation() on DB transformation class", ex);
            }
        }
    }
    
    public void analyseValues(){
        String s = String.format(
                "SELECT %s FROM %s WHERE CONVERT(%s,char) NOT IN (SELECT CONVERT(%s,char) FROM %s);",
                fk.getForeingKeyColumn(),
                fk.getForeingKeyTable(),
                fk.getForeingKeyColumn(),
                fk.getReferencedColumn(),
                fk.getReferencedTableName()
                );
        
        try {
            ResultSet queryResult =sqlFactory.createSQLCreateFreeQuery(SQLQueryType.Getter,s).sqlQueryDo();
            while(queryResult.next()){
                unmatchingValue.add(queryResult.getString(0));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
    
    public abstract void analyseCascade(ArrayList<Table> tables);

    public ArrayList<SQLQuery> getCascadeTransforamtion() {
        return cascadeTransforamtion;
    }

    public void setCascadeTransformation(ArrayList<SQLQuery> cascadeTransforamtion) {
        this.cascadeTransforamtion = cascadeTransforamtion;
    }
    
    public void addCascadeTransformation(SQLQuery e){
        cascadeTransforamtion.add(e);
    }
    
}
