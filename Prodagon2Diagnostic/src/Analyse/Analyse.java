package Analyse;

import Diagnostic.TransformationType;
import EasySQLight.ForeignKey;
import EasySQLight.SQLQueryFactory;
import EasySQLight.SQLQueryType;
import EasySQLight.SQLSelectQuery;
import EasySQLight.Table;
import Tools.JsonBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
Exception in thread "Thread-3" java.lang.NullPointerException
	at Analyse.Analyse.loadTable(Analyse.java:448)
	at Analyse.Analyse.AlreadExistCheck(Analyse.java:276)
	at Analyse.Analyse.analyse(Analyse.java:206)
	at prodagon2diagnostic.FXMLDocumentController.lambda$OnClickStart$1(FXMLDocumentController.java:116)
	at java.lang.Thread.run(Thread.java:748)
*/

/**
 * @author carl
 */
public class Analyse {

// Attributs declaration -------------------------------------------------------------------------------------------------------------------------------------    
    
    // Message that can descrive the analyse 
    private String message="";
    // SQL factory that the object will use to communicate with the databases (to creat select queries)
    private EasySQLight.SQLQueryFactory sqlFactory;
    // Intern Representation of the database. Used to load once the table information and make intern manipulation 
    private HashMap<String,Table> dicoTable;
    // Foreign key that we want to analyse if it is possible to add
    private ForeignKey fk;
    // Target that we need to modify to proceed the foreign key adding
    private TransformationTarget target;
    // New type that we will that we need to the target to proceed the foreign key adding
    private String newType;
    // Transfomation type that we need to perform to proceed the foreign key adding 
    private TransformationType transforamtiontype;
    // Boolean set to true if the fk alread exist on the database 
    private boolean fkAlreadyExist;
    // Boolean that set to true if it is impossible to adding the foreign key (set the message variable to more inforamtion) set false in other case
    private boolean impossibleAdding=false;
    // Boolean set true if the charset is combatible between the foreign key and the reference table.
    private boolean encodageMatching;
    // Boolean set to true if the reference and the foreignkey column are not the same unsigned/signed value
    private boolean unmatchingUnsigned;
    // Contains all values from the foreign key table that don't exist on the reference table 
    private List<String> unmatchingValue = new ArrayList();
    // Map of foreign key that can have a impact on the db transformation ( may required cascade tranforamtion)
    private HashMap<TransformationTarget,ArrayList<ForeignKey>> cascadeFkMap = new HashMap();
    
// Constructor -------------------------------------------------------------------------------------------------------------------------------------    
    
    private Analyse() {}
    
    public Analyse(SQLQueryFactory sqlFactory, HashMap<String, Table> dicoTable, ForeignKey fk, TransformationTarget target, String newType, TransformationType transforamtiontype) {
        this.sqlFactory = sqlFactory;
        this.dicoTable = dicoTable;
        this.fk = fk;
        this.target = target;
        this.newType = newType;
        this.transforamtiontype = transforamtiontype;
        this.cascadeFkMap.put(TransformationTarget.ForeignKeyTable, new ArrayList());
        this.cascadeFkMap.put(TransformationTarget.ReferencedTable, new ArrayList());
    }
    
    public static Analyse impossibleToFindTable(ForeignKey fk){
        Analyse out = new Analyse();
        out.fk = fk;
        out.impossibleAdding=true;
        out.message="Impossible to find the Reference/ForeignKey table and/or column. It can be not exist";
        return out;
    }
    
    public static Analyse impossibleToFindTable(ForeignKey fk, String message){
        Analyse out = new Analyse();
        out.fk = fk;
        out.impossibleAdding=true;
        out.message=message;
        return out;
    }

// Getter & Setter -------------------------------------------------------------------------------------------------------------------------------------

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SQLQueryFactory getSqlFactory() {
        return sqlFactory;
    }

    public void setSqlFactory(SQLQueryFactory sqlFactory) {
        this.sqlFactory = sqlFactory;
    }

    public HashMap<String, Table> getDicoTable() {
        return dicoTable;
    }

    public void setDicoTable(HashMap<String, Table> dicoTable) {
        this.dicoTable = dicoTable;
    }

    public ForeignKey getFk() {
        return fk;
    }

    public void setFk(ForeignKey fk) {
        this.fk = fk;
    }

    public TransformationTarget getTarget() {
        return target;
    }

    public void setTarget(TransformationTarget target) {
        this.target = target;
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

    public boolean isFkAlreadyExist() {
        return fkAlreadyExist;
    }

    public void setFkAlreadyExist(boolean fkAlreadyExist) {
        this.fkAlreadyExist = fkAlreadyExist;
    }

    public boolean isImpossibleAdding() {
        return impossibleAdding;
    }

    public void setImpossibleAdding(boolean impossibleAdding) {
        this.impossibleAdding = impossibleAdding;
    }

    public boolean isEncodageMatching() {
        return encodageMatching;
    }

    public void setEncodageMatching(boolean encodageMatching) {
        this.encodageMatching = encodageMatching;
    }

    public boolean isUnmatchingUnsigned() {
        return unmatchingUnsigned;
    }

    public void setUnmatchingUnsigned(boolean unmatchingUnsigned) {
        this.unmatchingUnsigned = unmatchingUnsigned;
    }

    public List<String> getUnmatchingValue() {
        return unmatchingValue;
    }

    public void setUnmatchingValue(List<String> unmatchingValue) {
        this.unmatchingValue = unmatchingValue;
    }

    public HashMap<TransformationTarget, ArrayList<ForeignKey>> getCascadeFkMap() {
        return cascadeFkMap;
    }

    public void setCascadeFkMap(HashMap<TransformationTarget, ArrayList<ForeignKey>> cascadeFkMap) {
        this.cascadeFkMap = cascadeFkMap;
    }

    public ArrayList<ForeignKey> getAllFK() {
        return allFK;
    }

    public void setAllFK(ArrayList<ForeignKey> allFK) {
        this.allFK = allFK;
    }
    
    
// Code -------------------------------------------------------------------------------------------------------------------------------------    
    
    /**
     * Check on the database or/and on the intern representation (dicoTable) if the foreign key adding is possible
     */
    public void analyse(){
        fkTableExistCheck();
        
        if(!impossibleAdding){
            AlreadExistCheck();
        }
        
        if(!impossibleAdding){
            encodageAnalyse();
            unsignedCompatibilityCheck();
            analyseValues();
            analyseCascade();
        }        
    }
    /**
     * Check if the foreign key and tha reference table and column exist.
     * Set this.impossibleAdding to true if we can't load the foreign key or the reference table (and set this.message) 
     */
    private void fkTableExistCheck(){
        //check if we can load the forign key table and column from the database or the inter representation
        try {
            loadTable(fk.getForeingKeyTable()).getTablecolumn().stream()
                    .filter(col-> col.getColumnName().toUpperCase().equals(fk.getForeingKeyColumn().toUpperCase()))
                    .findFirst()
                    .get();
        } catch (Exception ex) {
            this.impossibleAdding=true;
            this.message="Impossible to find the Foreign key table and/or column. It can be not exist";
        }
        //check if we can load the reference table and column from the database or the inter representation
        try {
            loadTable(fk.getReferencedTableName()).getTablecolumn().stream()
                    .filter(col-> col.getColumnName().toUpperCase().equals(fk.getReferencedColumn().toUpperCase()))
                    .findFirst()
                    .get();
        } catch (Exception ex) {
            this.impossibleAdding=true;
            this.message="Impossible to find the Reference table and/or column. It can be not exist";
        }
     }
    
    
    /**
     *  Check if the reference and the foreignkey column are not the same unsigned/signed value
     *  Set this.unmatchingUnsigned (and if turn false set this.message)
     */
    private void unsignedCompatibilityCheck(){
        try {
            boolean fkunsigned=false;
            boolean refunsigned=false;
            
            fkunsigned= loadTable(fk.getForeingKeyTable()).getTablecolumn().stream()
                    .filter(col-> col.getColumnName().toUpperCase().equals(fk.getForeingKeyColumn().toUpperCase()))
                    .map(col -> col.isUnsigned())
                    .findFirst()
                    .get();
            
            refunsigned= loadTable(fk.getReferencedTableName()).getTablecolumn().stream()
                    .filter(col-> col.getColumnName().toUpperCase().equals(fk.getReferencedColumn().toUpperCase()))
                    .map(col -> col.isUnsigned())
                    .findFirst()
                    .get();
            
            this.unmatchingUnsigned = !(fkunsigned==refunsigned);
            if (this.unmatchingUnsigned){
                message+="Differents signed/unsigned values between the foreign key column and the reference column";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Check if the foreign key alread exist on the databases 
     */
    private void AlreadExistCheck(){
         try {
            Table fkTable = loadTable(fk.getForeingKeyTable());
            for(ForeignKey clef : fkTable.getForeignKeys()){
                if(sameSementicKey(fk, clef)){
                    this.impossibleAdding=true;
                    this.fkAlreadyExist=true;
                    this.message="The Fk is already on the database";
                }
            }            
        } catch (SQLException ex) {
            Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    /**
     * Check if the charset between foreign key and reference matches
     * Set this.encodageMatching
     */
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
            if(encodageRef ==null && encodagefk==null){
                this.encodageMatching=true;
            }else if ((encodageRef !=null && encodagefk==null) || (encodageRef ==null && encodagefk!=null)){
                this.encodageMatching=false;
            } else{
                this.encodageMatching = encodageRef.toUpperCase().equals(encodagefk.toUpperCase());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Check if there are some unmatching values between the foreign key and the reference tables,
     * if there are som value add it to this.unmatchingValue
     * if this.transforamtiontype =="MBT" and there are unmatching value turn this.transforamtiontype to TransformationType.MVMT 
     */
    private void analyseValues(){
            String s = String.format(
                    "SELECT %s FROM %s WHERE %s IS NOT NULL AND %s NOT IN (SELECT %s FROM %s);",
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
            }catch (SQLException ex){
                Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
            }

        
        if (this.transforamtiontype.equals(TransformationType.MBT) && !unmatchingValue.isEmpty()){
            this.transforamtiontype=TransformationType.MVMT;
        }
    }
    /**
     * Check if there ares some Cascade transformation that we need the performe to adding the foreign key 
     */
    private void analyseCascade(){
        loadCascadFk(fk.getForeingKeyTable(), fk.getForeingKeyColumn(),TransformationTarget.ForeignKeyTable);
        loadCascadFk(fk.getReferencedTableName(), fk.getReferencedColumn(),TransformationTarget.ReferencedTable);
    }
    
    //ajoute a la liste cascadeFk toutes les foreign key pointant sur la colonne de la table donnée
    private void loadCascadFk(String tablename, String columnName,TransformationTarget target){
            try { 
                SQLSelectQuery select2 = new SQLSelectQuery(
                new String[]{"INFORMATION_SCHEMA.KEY_COLUMN_USAGE"},
                sqlFactory.getConn(),
                new String[]{"TABLE_NAME,COLUMN_NAME","COLUMN_NAME","CONSTRAINT_NAME","REFERENCED_TABLE_NAME","REFERENCED_COLUMN_NAME"},
                "(REFERENCED_TABLE_NAME IS NOT NULL AND REFERENCED_TABLE_NAME = '"+tablename+"' AND REFERENCED_COLUMN_NAME = '"+columnName+"') OR ( REFERENCED_TABLE_NAME IS NOT NULL AND TABLE_NAME='"+tablename+"' AND COLUMN_NAME='"+columnName+"' )"
                );
                ResultSet result =select2.sqlQueryDo();
                while(result.next()){            
                    ForeignKey clef =   new ForeignKey( 
                        result.getString("REFERENCED_TABLE_NAME"),
                        result.getString("REFERENCED_COLUMN_NAME"),
                        result.getString("COLUMN_NAME"),
                        result.getString("TABLE_NAME"),
                        result.getString("CONSTRAINT_NAME")
                    );
                    if(!cascadeFkMap.get(target).contains(clef)){
                    cascadeFkMap.get(target).add(clef);
                    loadCascadFk(result.getString("TABLE_NAME"),result.getString("COLUMN_NAME"),target);
                    loadCascadFk(result.getString("REFERENCED_TABLE_NAME"),result.getString("REFERENCED_COLUMN_NAME"),target);  
                    }
                }
            } catch (SQLException ex) {
            Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadCascadeFKFromDicoTable(tablename, columnName, target);      
    }
    
    private ArrayList<ForeignKey> allFK = new ArrayList();
    private void loadCascadeFKFromDicoTable(String tablename, String columnName,TransformationTarget target){       
        if(allFK.isEmpty()){
            this.dicoTable.forEach((k,v)->allFK.addAll(v.getForeignKeys()));
        }
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
    
    
    /**
     * @param objectName  name of Json creat
     * @return String in Json format to Diagnostic the feasibility of foreign key adding 
     */
    public String getJson(String objectName){
        HashMap<String,Object> map = new HashMap();
        map.put("foreignKey",this.fk.toJson());
        map.put("message", this.message);
        if(this.impossibleAdding){
            map.put("impossibleAdding",new Boolean(this.impossibleAdding));
        }else if(this.fkAlreadyExist){
            map.put("fkAlreadyExist",new Boolean(this.fkAlreadyExist));
        }else{
            map.put("impossibleAdding",new Boolean(this.impossibleAdding));
            map.put("fkAlreadyExist",new Boolean(this.fkAlreadyExist));
            map.put("encodageMatching",new Boolean(this.encodageMatching));
            map.put("unmatchingUnsigned",new Boolean(this.unmatchingUnsigned));          
            map.put("foreignKeyCascade",(this.cascadeFkMap.get(TransformationTarget.ForeignKeyTable)).stream().map(fk->fk.toJson()).collect(Collectors.toCollection(ArrayList::new)));
            map.put("ReferenceCascade",(this.cascadeFkMap.get(TransformationTarget.ReferencedTable)).stream().map(fk->fk.toJson()).collect(Collectors.toCollection(ArrayList::new)));
            map.put("advisedNewType",this.newType);
            map.put("advisedTarget",this.target.name());
            map.put("transformationType",this.transforamtiontype.name());
            map.put("unmatchingValuesNumber",new Integer(this.unmatchingValue.size()));
        }
        return JsonBuilder.mapToJson(objectName, map);
    }
    /**
     * @return String in Json format to Diagnostic the feasibility of foreign key adding 
     * the name of this json is the foreign key constraint name
     */
    public String getJson(){
        return getJson(null);
    }
    
    /**
     * Return a Table Object that is which that be stored on de intern representation (tableDico attribut)
     * if it don't be already stored creat it from the DB metadata, store it and retrun it; 
     * @param tableName Name of the Table that we have to load
     * @return A Table Object laready initialised
     * @throws SQLException if there are some probleme with de database
     */
    private Table loadTable(String tableName) throws SQLException{
        Table out = null;
        if (dicoTable.containsKey(tableName)){
            out = dicoTable.get(tableName);
        }else{
            out = sqlFactory.loadTable(tableName);
            dicoTable.put(tableName,out);
        }
        return out;
    }

    @Override
    public String toString() {
        return super.toString(); 
    }
    /**
     * check if the two koreign key avec the same Table an column as Foreignkey/Reference
     * 
     * @param fk1 foreign key to compare
     * @param fk2 foreign key to compare
     * @return return true if the two koreign key avec the same Table/column for the Foreignkey/Reference
     */
    public boolean sameSementicKey(ForeignKey fk1, ForeignKey fk2){
        boolean samefkTable = fk1.getForeingKeyTable().toUpperCase().equals(fk2.getForeingKeyTable().toUpperCase());
        boolean samefkColumn = fk1.getForeingKeyColumn().toUpperCase().equals(fk2.getForeingKeyColumn().toUpperCase());
        boolean samerefTable = fk1.getReferencedTableName().toUpperCase().equals(fk2.getReferencedTableName().toUpperCase()); 
        boolean samerefColumn = fk1.getReferencedColumn().toUpperCase().equals(fk2.getReferencedColumn().toUpperCase());
        
        return samefkColumn && samefkTable && samerefColumn && samerefTable;
    }
    
}
