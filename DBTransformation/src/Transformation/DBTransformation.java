package Transformation;

import ContextAnalyser.TransformationType;
import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLAlterTableQuery;
import EasySQL.SQLQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryType;
import EasySQL.SQLSelectQuery;
import EasySQL.Table;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import EasySQL.SQLTransactionQuery;
import java.sql.SQLException;
/**
 *
 * @author carl_
 */
public class DBTransformation extends Transformation {
    
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
    private boolean encodageMatching;
    private SQLTransactionQuery cascadTransformation;
    private HashMap<String,Table> tableDico;
    private TransformationTarget target;
    private String newType;
    private ContextAnalyser.TransformationType transforamtiontype;

    public DBTransformation(SQLQueryFactory sqlFactory,HashMap<String,Table> tableDico,ForeignKey fk, TransformationTarget target,String newType,ContextAnalyser.TransformationType transforamtiontype) {
        this.tableName=fk.getForeingKeyTable();
        this.listQuery = new ArrayList();
        this.fk = fk;
        this.sqlFactory = sqlFactory;
        this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList();
        this.target=target;
        this.tableDico=tableDico;
        this.newType = newType;
        this.transforamtiontype = transforamtiontype;
    }
    public DBTransformation(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword,HashMap<String,Table> tableDico, ForeignKey fk, TransformationTarget target, String newType, ContextAnalyser.TransformationType transforamtiontype) {
        this.dataBaseHostName = dataBaseHostName;
        this.dataBasePortNumber = dataBasePortNumber;
        this.dataBaseLogin = dataBaseLogin;
        this.dataBasePassword = dataBasePassword;
        this.tableName=fk.getForeingKeyTable();
        this.listQuery = new ArrayList();
        this.sqlFactory = new SQLQueryFactory(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fk = fk;
        this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList(); 
        this.target=target;
        this.tableDico=tableDico;
        this.newType = newType;
        this.transforamtiontype = transforamtiontype;
    }
    
    public TransformationTarget getTarget() {
        return target;
    }
    public void setTarget(TransformationTarget target) {
        this.target = target;
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
    public String getTableName() {
        return tableName;
    } 
    public SQLTransactionQuery getCascadTransformation() {
        return cascadTransformation;
    }
    public void setCascadTransformation(SQLTransactionQuery cascadTransformation) {
        this.cascadTransformation = cascadTransformation;
    }
    public HashMap<String, Table> getTableDico() {
        return tableDico;
    }
    public void setTableDico(HashMap<String, Table> tableDico) {
        this.tableDico = tableDico;
    }
    public String getNewType() {
        return newType;
    }
    public void setNewType(String newType) {
        this.newType = newType;
    }
    public TransformationType getTransforamtiontype() {
        return transforamtiontype;
    }
    public void setTransforamtiontype(TransformationType transforamtiontype) {
        this.transforamtiontype = transforamtiontype;
    }
    
    public void transfrom() throws SQLException{
        try{
            makeCascadeTransformation();
        }catch(SQLException e){
             Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, "SQLException during Cascade transformation", e);
             throw new SQLException(); 
        }
        for(SQLQuery query : listQuery){
            query.sqlQueryDo();
        }
    }    
    public void unDoTransformation() throws SQLException{
        try{
            undoCascadeTransformation();
        }catch(SQLException e){
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, "SQLException during undoing Cascade transformation", e);
        }
        for (int i=(this.listQuery.size()-1);i>=0;i--){
            listQuery.get(i).sqlQueryUndo();
        }
    }

    public void analyse(){
        analyseValues();
        analyseCascade();
        if (target.equals(TransformationTarget.ForeignKeyTable)){
            addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)));
        }else if(target.equals(TransformationTarget.ReferencedTable)){
            addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)));
        }
        addQuery(sqlFactory.createSQLAlterAddForeignKeyQuery(tableName, fk));
    }
    
    public void makeCascadeTransformation() throws SQLException{
        SQLTransactionQuery transaction = sqlFactory.creatTransactionQuery();
        for(ForeignKey fk : this.cascadeFk){
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)));
        }
        this.cascadTransformation = transaction;
        transaction.sqlQueryDo();
    }
    
    public void undoCascadeTransformation() throws SQLException{
        this.cascadTransformation.sqlQueryUndo();
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
                unmatchingValue.add(queryResult.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                if (this.transforamtiontype.equals(TransformationType.MBT) && !unmatchingValue.isEmpty()){
                    this.transforamtiontype=TransformationType.MVMT;
                }
        
    };
    
    public void analyseCascade(){
        if(target.equals(TransformationTarget.ForeignKeyTable)){
            loadCascadFk(fk.getForeingKeyTable(), fk.getForeingKeyColumn());
        }else if(target.equals(TransformationTarget.ReferencedTable)){
            loadCascadFk(fk.getReferencedTableName(), fk.getReferencedColumn());
        }
    }

    
    //ajoute a la liste cascadeFk toutes les foreign key pointant sur la colonne de la table donnée
    private void loadCascadFk(String tablename, String columnName){
        try {
            if(!tableDico.containsKey(tablename)){tableDico.put(tablename, sqlFactory.loadTable(tablename));}
            SQLSelectQuery select2 = new SQLSelectQuery(
                    new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                    sqlFactory.getConn(),
                    new String[]{"TABLE_NAME,COLUMN_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
                    "REFERENCED_TABLE_NAME IS NOT NULL AND REFERENCED_TABLE_NAME = '"+tablename+"' AND REFERENCED_COLUMN_NAME = '"+columnName+"';"
            );
            ResultSet result =select2.sqlQueryDo();
            while(result.next()){
                ForeignKey clef =   new ForeignKey( result.getString("REFERENCED_TABLE_NAME"),
                                                    result.getString("REFERENCED_COLUMN_NAME"),
                                                    result.getString("COLUMN_NAME"),
                                                    result.getString("TABLE_NAME"),
                                                    result.getString("CONSTRAINT_NAME")
                                                  );
                //test pour éviter les cycles
                if(!cascadeFk.contains(clef)){
                    loadCascadFk(result.getString("TABLE_NAME"),result.getString("COLUMN_NAME"));
                    cascadeFk.add(clef);
                }  
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
