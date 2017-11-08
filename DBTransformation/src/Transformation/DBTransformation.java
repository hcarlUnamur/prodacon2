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
import EasySQL.StringQueryGetter;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
/**
 *
 * @author carl_
 */
public class DBTransformation extends Transformation {
    private ReentrantLock lock = new ReentrantLock();
    private String dataBaseHostName;
    private String dataBasePortNumber;
    private String dataBaseLogin;
    private String dataBasePassword;
    private String tableName;
    private SQLQueryFactory sqlFactory;
    private ForeignKey fk;
    private ArrayList<SQLQuery> listQuery;
    private SQLAlterTableQuery addFkQuery;
    private ArrayList<String> unmatchingValue;
    
    //private ArrayList<ForeignKey> cascadeFk;
    private HashMap<TransformationTarget,ArrayList<ForeignKey>> cascadeFkMap;
    
    private boolean encodageMatching;
    private SQLTransactionQuery cascadTransformation;
    private HashMap<String,Table> tableDico;
    private TransformationTarget target;
    private String newType;
    private ContextAnalyser.TransformationType transforamtiontype;
    
    private Column fkColumnBeforeTransformation;
    private Column refColumnBeforeTransformation;
    
    public DBTransformation(SQLQueryFactory sqlFactory,HashMap<String,Table> tableDico,ForeignKey fk, TransformationTarget target,String newType,ContextAnalyser.TransformationType transforamtiontype) {
        this.tableName=fk.getForeingKeyTable();
        this.listQuery = new ArrayList();
        this.fk = fk;
        this.sqlFactory = sqlFactory;
        //this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList();
        this.target=target;
        this.tableDico=tableDico;
        this.newType = newType;
        this.transforamtiontype = transforamtiontype;
        
        this.cascadeFkMap= new HashMap();
        this.cascadeFkMap.put(TransformationTarget.ForeignKeyTable, new ArrayList());
        this.cascadeFkMap.put(TransformationTarget.ReferencedTable, new ArrayList());
        
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
        //this.cascadeFk = new ArrayList();
        this.unmatchingValue = new ArrayList(); 
        this.target=target;
        this.tableDico=tableDico;
        this.newType = newType;
        this.transforamtiontype = transforamtiontype;
        
        this.cascadeFkMap= new HashMap();
        this.cascadeFkMap.put(TransformationTarget.ForeignKeyTable, new ArrayList());
        this.cascadeFkMap.put(TransformationTarget.ReferencedTable, new ArrayList());
        
    }
    
    public Column getFkColumnBeforeTransformation() {
        return fkColumnBeforeTransformation;
    }

    public Column getRefColumnBeforeTransformation() {
        return refColumnBeforeTransformation;
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
        ArrayList<ForeignKey> out = null;
        if(target.equals(TransformationTarget.All)){
            out = new ArrayList();
            out.addAll(cascadeFkMap.get(TransformationTarget.ForeignKeyTable));
            out.addAll(cascadeFkMap.get(TransformationTarget.ReferencedTable));
        }else{
            out = cascadeFkMap.get(target);
        }
        return out;
    }
    /*
    public void setCascadeFk(ArrayList<ForeignKey> cascadeFk) {
        this.cascadeFk = cascadeFk;
    }
    */
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
        
        makeCascadeTransformation();           
 
        for(SQLQuery query : listQuery){
            query.sqlQueryDo();
        }
        addFkQuery.sqlQueryDo();
        
        tableDico.get(fk.getForeingKeyTable()).addForeignKey(fk);
        
    }    
    public void unDoTransformation() throws SQLException{
        addFkQuery.sqlQueryUndo();
        
        for (int i=(this.listQuery.size()-1);i>=0;i--){
            cascadTransformation.getQueries().add(0,(SQLAlterTableQuery)listQuery.get(i));
            //cascadTransformation.addQuery((SQLAlterTableQuery)listQuery.get(i));
        }
        
        try{
            undoCascadeTransformation();
        }catch(SQLException e){
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, "SQLException during undoing Cascade transformation", e);
        }
        
    }

    public void analyse(){
        
        try {
            fkColumnBeforeTransformation = loadTable(fk.getForeingKeyTable()).getTablecolumn()
                    .stream()
                    .filter(c->c.getColumnName().equals(fk.getForeingKeyColumn()))
                    .findFirst()
                    .get();
            
            refColumnBeforeTransformation = loadTable(fk.getReferencedTableName()).getTablecolumn()
                    .stream()
                    .filter(c->c.getColumnName().equals(fk.getReferencedColumn()))
                    .findFirst()
                    .get();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        encodageAnalyse();
        analyseValues();
        analyseCascade();
        //déplacer dans le cascade : transforamtion du type de la fk ou ref column
        addFkQuery = sqlFactory.createSQLAlterAddForeignKeyQuery(tableName, fk);
    }
    
    private void encodageAnalyse(){
        try {
            Table fkTable = loadTable(fk.getForeingKeyTable());
            Table refTable = loadTable(fk.getReferencedTableName());
            
            String encodagefk = fkTable.getTablecolumn()
                    .stream()
                    .filter(s->s.getColumnName().equals(fk.getForeingKeyColumn()))
                    .findFirst()
                    .get()
                    .getCharset();
            
            String encodageRef = refTable.getTablecolumn()
                    .stream()
                    .filter(s->s.getColumnName().equals(fk.getReferencedColumn()))
                    .findFirst()
                    .get()
                    .getCharset();
            
            System.out.println("ref charset : "+encodageRef);
            System.out.println("fk charset "+encodagefk);
            if(encodageRef ==null && encodagefk==null){
                 System.out.println("case1");
                this.encodageMatching=true;
            }else if ((encodageRef !=null && encodagefk==null) || (encodageRef ==null && encodagefk!=null)){
                System.out.println("case2");
                this.encodageMatching=false;
            } else{
                System.out.println("case3 : "+encodageRef.toUpperCase().equals(encodagefk.toUpperCase()));
                this.encodageMatching = encodageRef.toUpperCase().equals(encodagefk.toUpperCase());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void makeCascadeTransformation() throws SQLException{
        
        //remove existing fk for the modification
        ArrayList<SQLQuery> remvfk = new ArrayList();
        getCascadeFk().forEach(fk->remvfk.add(sqlFactory.createSQLAlterDropForeignKeyQuery(fk.getForeingKeyTable(), fk)));
        for(SQLQuery query : remvfk){
                query.sqlQueryDo();
        }
        // change the type
        
        SQLTransactionQuery transaction = sqlFactory.creatTransactionQuery();
        
        if (target.equals(TransformationTarget.ForeignKeyTable)){
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)));
            //modifi the BD intern representation
            Column col = tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get();
            setColumnWithTypAndCharset(col,newType);
        }else if(target.equals(TransformationTarget.ReferencedTable)){
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)));
            //modifi the BD intern representation
            Column col = tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get();
            setColumnWithTypAndCharset(col,newType);
        }else if (target.equals(TransformationTarget.All)){
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)));
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)));
             //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get(),newType);
            setColumnWithTypAndCharset(tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get(),newType);
        }
        
        
        for(ForeignKey fk : this.getCascadeFk()){
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)));
            //new ajout pas certain que se soit bon
            transaction.addQuery(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)));
            //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get(),newType);
            setColumnWithTypAndCharset(tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get(),newType);
        
        }
        this.cascadTransformation = transaction;
        transaction.sqlQueryDo();
        //reconstruct the fk
        for(SQLQuery query : remvfk){
                query.sqlQueryUndo();
        }
        
    }
    
    private void undoCascadeTransformation() throws SQLException{
        //remove existing fk for the modification
        ArrayList<SQLQuery> remvfv = new ArrayList();
        getCascadeFk().forEach(fk->remvfv.add(sqlFactory.createSQLAlterDropForeignKeyQuery(fk.getForeingKeyTable(), fk)));
        for(SQLQuery query : remvfv){
                query.sqlQueryDo();
        }
        
        this.cascadTransformation.sqlQueryUndo();
    
        //reconstruct the fk
        for(SQLQuery query : remvfv){
                query.sqlQueryUndo();
        }
        
        
    }
    
    private void analyseValues(){
         String s = String.format(
                "SELECT %s FROM %s WHERE %s IS NOT NULL AND CONVERT(%s,char) NOT IN (SELECT CONVERT(%s,char) FROM %s);",
                fk.getForeingKeyColumn(),
                fk.getForeingKeyTable(),
                fk.getForeingKeyColumn(),
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
    
    private void analyseCascade(){       
                loadCascadFk(fk.getForeingKeyTable(), fk.getForeingKeyColumn(),TransformationTarget.ForeignKeyTable);
                loadCascadFk(fk.getReferencedTableName(), fk.getReferencedColumn(),TransformationTarget.ReferencedTable);
    }

    
    //ajoute a la liste cascadeFk toutes les foreign key pointant sur la colonne de la table donnée
    private void loadCascadFk(String tablename, String columnName,TransformationTarget target){
            
            try {
            
            if(!tableDico.containsKey(tablename)){tableDico.put(tablename, loadTable(tablename));}
            SQLSelectQuery select2 = new SQLSelectQuery(
            new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
            sqlFactory.getConn(),
            new String[]{"TABLE_NAME,COLUMN_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
            "(REFERENCED_TABLE_NAME IS NOT NULL AND REFERENCED_TABLE_NAME = '"+tablename+"' AND REFERENCED_COLUMN_NAME = '"+columnName+"') OR ( REFERENCED_TABLE_NAME IS NOT NULL AND TABLE_NAME='"+tablename+"' AND COLUMN_NAME='"+columnName+"' )"
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
            //System.out.println("clef = "+clef);
            //System.out.println("!cascadeFk.contains(clef) ->"+!cascadeFk.contains(clef));
            //cascadeFk.forEach(System.out::println);
            if(!cascadeFkMap.get(target).contains(clef)){
            //System.out.println("add :" + clef);
            cascadeFkMap.get(target).add(clef);
            loadCascadFk(result.getString("TABLE_NAME"),result.getString("COLUMN_NAME"),target);
            loadCascadFk(result.getString("REFERENCED_TABLE_NAME"),result.getString("REFERENCED_COLUMN_NAME"),target);  
            }
            }
            } catch (SQLException ex) {
            Logger.getLogger(DBTransformation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            loadCascadeFKFromDicoTable(tablename, columnName, target);      
    }
    
    private void loadCascadeFKFromDicoTable(String tablename, String columnName,TransformationTarget target){
        
        ArrayList<ForeignKey> allFK = new ArrayList();
        this.tableDico.forEach((k,v)->allFK.addAll(v.getForeignKeys()));
         ArrayList<ForeignKey> fkFiltered = allFK.stream()
                 .filter((fk->((fk.getForeingKeyTable().equals(tablename)&&fk.getForeingKeyColumn().equals(columnName))||(fk.getReferencedTableName().equals(tablename)&&fk.getReferencedColumn().equals(columnName)))))
                 .collect(Collectors.toCollection(ArrayList::new));
         for(ForeignKey clef : fkFiltered){
             if(!cascadeFkMap.get(target).contains(clef)){
                cascadeFkMap.get(target).add(clef);
                loadCascadFk(clef.getForeingKeyTable(),clef.getForeingKeyColumn(),target);
                loadCascadFk(clef.getReferencedTableName(),clef.getReferencedColumn(),target);  
            }
         }
    }
    
    public String getTransformationScript()throws SQLException{
        StringBuilder out = new StringBuilder();         
        out.append(getMakeCascadeTransformationString());                  
        for(SQLQuery query : listQuery){
            out.append(((StringQueryGetter)query).getStringSQLQueryDo());
        }
        out.append(addFkQuery.getStringSQLQueryDo());
        //modify intern representation ef Table
        tableDico.get(fk.getForeingKeyTable()).addForeignKey(fk);
        return out.toString();
    }
    
    public String getMakeCascadeTransformationString() throws SQLException{
        StringBuilder out = new StringBuilder();
        //remove existing fk for the modification
        ArrayList<SQLQuery> remvfk = new ArrayList();
        getCascadeFk().forEach(fk->remvfk.add(sqlFactory.createSQLAlterDropForeignKeyQuery(fk.getForeingKeyTable(), fk)));
        for(SQLQuery query : remvfk){
                out.append(((StringQueryGetter)query).getStringSQLQueryDo());
        }
        // change the type
       
        if (target.equals(TransformationTarget.ForeignKeyTable)){
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)).getStringSQLQueryDo());
            //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get(),newType);
        }else if(target.equals(TransformationTarget.ReferencedTable)){
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)).getStringSQLQueryDo());
            //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get(),newType);            
        }else if (target.equals(TransformationTarget.All)){
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)).getStringSQLQueryDo());
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)).getStringSQLQueryDo());
            //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get(),newType);
            setColumnWithTypAndCharset(tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get(),newType);

        }
                
        for(ForeignKey fk : this.getCascadeFk()){
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), new Column(fk.getForeingKeyColumn(), newType)).getStringSQLQueryDo());
            //new ajout pas certain que se soit bon
            out.append(sqlFactory.createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), new Column(fk.getReferencedColumn(), newType)).getStringSQLQueryDo());
            //modifi the BD intern representation
            setColumnWithTypAndCharset(tableDico.get(fk.getForeingKeyTable()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getForeingKeyColumn())).findFirst().get(),newType);
            setColumnWithTypAndCharset(tableDico.get(fk.getReferencedTableName()).getTablecolumn().stream().filter(c->c.getColumnName().equals(fk.getReferencedColumn())).findFirst().get(),newType);

        }
        //this.cascadTransformation = transaction;
        //transaction.sqlQueryDo();
        //reconstruct the fk
        //for(SQLQuery query : remvfk){
        //        out.append(((StringQueryGetter)query).getStringSQLQueryUndo());
        //        
        //}
        ArrayList<SQLQuery> readd = new ArrayList();
        getCascadeFk().forEach(fk->readd.add(sqlFactory.createSQLAlterAddForeignKeyQuery(fk.getForeingKeyTable(), fk)));
        for(SQLQuery query : readd){
                out.append(((StringQueryGetter)query).getStringSQLQueryDo());
        }
        
        return out.toString();
    }
    
    private Table loadTable(String tableName) throws SQLException{
        Table out = null;
        if (tableDico.containsKey(tableName)){
            //System.out.println("not reload :" + tableName);
            out = tableDico.get(tableName);
        }else{
                //System.out.println("reload :" + tableName);
                out = sqlFactory.loadTable(tableName);
                tableDico.put(tableName,out);
        }
        return out;
    }
    
    private static String[] CHARSET_TYPE={"CHAR","VARCHAR","TEXT"};
    private static void setColumnWithTypAndCharset(Column col,String type){
        if (type.toUpperCase().contains("CHARACTER SET")){
            String [] st = type.toUpperCase().split("CHARACTER SET");
            col.setColumnType(st[0]);
            col.setCharset(st[1]);
        } else if(!isIn(type.split("\\(|\\|,)")[0],CHARSET_TYPE)){          
            col.setColumnType(type);
            col.setCharset(null);
        }else{
            col.setColumnType(type);
        }
    }
    
    private static boolean isIn(String s , String[] table){
            for(String e : table){
                if(s.toUpperCase().equals(e.toUpperCase())){return true;}
            }
        return false;
    };
}
