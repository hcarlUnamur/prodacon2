package ContextAnalyser;

import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.Table;
import Transformation.DBTransformation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import EasySQL.Exception.LoadUnexistentTableException;
import Transformation.EmptyTransformation;
import Transformation.ImpossibleTransformation;
import Transformation.Transformation;
import Transformation.TransformationTarget;
import java.util.Iterator;

/**
 *
 * @author carl_
 */
public class ContextAnalyser implements Iterator<Transformation> {
    
    private static final String[] INT_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT"};
    private static final String[] NUMERIC_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static final String[] ALPHA_NUMERIC_TYPES = {"ENUM","CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT"};
    private static final String[] TIME_TYPES = {"TIME","YEAR","DATE","TIMESTAMP","DATETIME"};
    private static final String[] TIME_TYPES_TRANSFORMABLE = {"TIMESTAMP","DATETIME"};
    private static final String[] ONE_PARAMETER_TYPE={"YEAR","CHAR","VARCHAR"}; 
    private static final String[] TWO_PARAMETER_TYPE={"FLOAT","DOUBLE","DECIMAL"};
    
    private final ArrayList<ForeignKey> fks;
    private final EasySQL.SQLQueryFactory factory;
    private final HashMap<String,Table> tableLoaded;
    private final HashMap<String,Table> dicoTable;
    private int iteratorIndex;
    
/**
 * @param dataBaseHostName the DataBase host name where we will make the transformation.  
 * @param dbName the data base name where we will make the transformation.
 * @param dataBasePortNumber the port of data base where we will make the transformation.
 * @param dataBaseLogin the login of the data base where we will make the transformation.
 * @param dataBasePassword the password of the login of the data base where we will make the transformation.
 * @param fks A list of foreign keys that we want to generate the transformation.
 */
    
    public ContextAnalyser(String dataBaseHostName,String dbName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ArrayList<ForeignKey> fks) {
        this.factory= new EasySQL.SQLQueryFactory(dataBaseHostName,dbName,dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fks = fks;
        this.tableLoaded = new HashMap<String,Table>();
        this.iteratorIndex=0;
        this.dicoTable = new HashMap();
    }

    public HashMap<String, Table> getDicoTable() {
        return dicoTable;
    }
       

 /**
* Create a Transformation Object that was configured to make able the adding of foreign key if it is possible.
* @param fk foreign key that we want to add to the DB with the returned transformation
* @throws LoadUnexistentTableException if the tables and/or the columns from the fk parameter don't exist on the DB  
* @return
*  - If the fk is already on the DB, return an EmptyTransformation.</br>
*  - If it is possible, return a DBTransformation Object that was configured to make able the adding of key foreign</br>
*  - Finally, if the algorithm don't find a possible transformation it return a ImpossibleTransformation </br>  
* @see Transformation
* @see DBTransformation
* @see EmptyTransformation
* @see ImpossibleTransformation
*
*/
    
    public Transformation analyse(ForeignKey fk) throws LoadUnexistentTableException{
        Table usedTable = null;
        Table referencedTable = null;
        try{
                usedTable = loadTable(fk.getForeingKeyTable());
                referencedTable = loadTable(fk.getReferencedTableName());
        }catch(SQLException e){
               throw new LoadUnexistentTableException("It's impossible to loade the foreign key table. they can don't exist");
        }
                Column fkColumn =null;
                Column referencedColumn=null;
                
                try{
                int i =0;
                fkColumn = usedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getForeingKeyColumn()))
                                            .findFirst()
                                            .get();
                i++;
                referencedColumn = referencedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getReferencedColumn()))
                                            .findFirst()
                                            .get();
                
                }catch(java.util.NoSuchElementException e){
                    if (1==0)
                        throw new LoadUnexistentTableException("Impossible to find : "+fk.getForeingKeyTable()+"."+fk.getForeingKeyColumn());
                    else 
                        throw new LoadUnexistentTableException("Impossible to find : "+fk.getReferencedTableName()+"."+fk.getReferencedColumn());
                }
                //System.out.println("--------------------------------"+fkAlreadyExist(usedTable, fk));
                if(fkAlreadyExist(usedTable, fk)){
                    return new EmptyTransformation("the Foreign key already exist");
                }
                else if (fkColumn.getColumnType().equals(referencedColumn.getColumnType())){
                    return perfectTypeMatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }else{
                    return typeMismatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }
        
    }
    
    private Transformation perfectTypeMatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        //System.out.println("*****************Perfect Type Matching");
        // @Do : ajouter action
        DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, fkColumn.getColumnType(),TransformationType.MBT);        
        dbt.setTableDico(dicoTable);
        return dbt;
    }
    
    private Transformation typeMismatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        //System.out.println("*****************Type mismatching ");
        //signed and unsigned type
        if(isUnsigned(fkColumn)!=isUnsigned(referencedColumn)){
            //System.out.println("***************** signed and unsigned type");
            return new ImpossibleTransformation("Type mismatching : signed and unsigned type [between :" + fkColumn.getColumnType() +" -> "+ referencedColumn.getColumnType()+"]" );
            
        }
        //same Type But Different lengths
        else if (isTheSameType(fkColumn, referencedColumn)){
            return sameTypeButDifferentlength(usedTable, referencedTable,fk, fkColumn, referencedColumn);
        }
        //super type transformation
        //int type 
        else if ((isIn(getTypeName(fkColumn), INT_TYPES) && isIn(getTypeName(referencedColumn), INT_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), INT_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), INT_TYPES);
            if(fktypeindex>reftypeindex){
                //System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+fkColumn.getColumnType()  );
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.NTT);        
                dbt.setTableDico(dicoTable);
                return dbt;
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to " + referencedColumn.getColumnType());
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.NTT);
                dbt.setTableDico(dicoTable);
                return dbt;
            }       
        }
        else if((isIn(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES) && isIn(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES);
            if(fktypeindex>reftypeindex){
                //System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+fkColumn.getColumnType()  );
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.ANTT);
                dbt.setTableDico(dicoTable);
                return dbt;
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to "+ referencedColumn.getColumnType());
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.ANTT);
                dbt.setTableDico(dicoTable);
                return dbt;
            } 
        }
        else if((isIn(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE) && isIn(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE);
            if(fktypeindex>reftypeindex){
                //System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+fkColumn.getColumnType()  );
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.TTT);
                dbt.setTableDico(dicoTable);
                return dbt;
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to "+ referencedColumn.getColumnType());
                DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.TTT);
                dbt.setTableDico(dicoTable);
                return dbt;
            } 
        }else{
            DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.DTT);
            dbt.setTableDico(dicoTable);
            return dbt;
            //return new ImpossibleTransformation("Type mismatching and no transformation found [between :" + fkColumn.getColumnType() +" -> "+ referencedColumn.getColumnType()+"]");
        }
        DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.DTT);
        dbt.setTableDico(dicoTable);
        return dbt;
        //return new ImpossibleTransformation("Type mismatching and no transformation found [between :" + fkColumn.getColumnType() +" -> "+ referencedColumn.getColumnType()+"]");
    }
    
    private Transformation sameTypeButDifferentlength(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn){
            //System.out.println("*****************Same type but different length ");

            if (isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE)){
                //System.out.println("*****************Case 1 ");
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                    //System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn.getColumnType() + " to " +referencedColumn.getColumnType() );
                    // @Do : ajouter action
                    DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);        
                    dbt.setTableDico(dicoTable);
                    return dbt;
                }else{
                    //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +fkColumn.getColumnType() );
                    // @Do : ajouter action 
                    DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT); 
                    dbt.setTableDico(dicoTable);
                    return dbt;
                }
            }
            
            else if (isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE)){
                //System.out.println("*****************Case 2 ");
                //System.out.println(fkColumn.getColumnType()+""+referencedColumn.getColumnType());
                if(getTypelength2(fkColumn)!=getTypelength2(referencedColumn)){
                    //System.out.println("*****************Transforamtion impossible mismatching decimal length : " + fkColumn.getColumnType() + " -/-> " + referencedColumn.getColumnType());
                    //return new ImpossibleTransformation("Transformation impossible mismatching decimal length : " + fkColumn.getColumnType() + " -/-> " + referencedColumn.getColumnType());
                    DBTransformation dbt = new DBTransformation(   factory,
                                                                        tableLoaded, 
                                                                        fk, TransformationTarget.ForeignKeyTable,
                                                                        getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")",
                                                                        TransformationType.LMTT
                                                    );
                    dbt.setTableDico(dicoTable);
                    return dbt;
                }else if(getTypelength1(fkColumn)!=getTypelength1(referencedColumn)){
                        if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                        //System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn + " to " +getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")" );
                        // @Do : ajouter action
                        DBTransformation dbt = new DBTransformation(   factory,
                                                                        tableLoaded, 
                                                                        fk, TransformationTarget.ForeignKeyTable,
                                                                        getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")",
                                                                        TransformationType.LMTT
                                                    );
                        dbt.setTableDico(dicoTable);
                        return dbt;
                    }else{
                        //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")" );
                        // @Do : ajouter action
                        DBTransformation dbt =  new DBTransformation(   factory,
                                                                        tableLoaded, 
                                                                        fk, TransformationTarget.ReferencedTable,
                                                                        getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")",
                                                                        TransformationType.LMTT
                                                                    );
                        dbt.setTableDico(dicoTable);
                        return dbt;
                        
                    }
                } 
            }
            
            else if(!isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE) && !isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE) ){
                //System.out.println("*****************Case 3 ");
                //System.out.println("*****************Different type size but don't need changement (is a zero parameter type) ");
                // @Do : ajouter action
                // on fait le même chose que one parameter transfo
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                    //System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn.getColumnType() + " to " +referencedColumn.getColumnType() );
                    // @Do : ajouter action
                    DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);        
                    dbt.setTableDico(dicoTable);
                    return dbt;
                }else{
                    //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +fkColumn.getColumnType() );
                    // @Do : ajouter action 
                    DBTransformation dbt = new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT); 
                    dbt.setTableDico(dicoTable);
                    return dbt;
                }
            }
            // juste pour afoir un return de fin noralement ne tombe jamais dedans 
            return new ImpossibleTransformation("Type mismatching and no transformation found [between :" + fkColumn.getColumnType() +" -> "+ referencedColumn.getColumnType()+"]");
    }
    
    //look if it the column have the same type but can have differents length 
    private static boolean isTheSameType(Column col1,Column col2){
      return col1.getColumnType().split("\\(|\\)")[0].equals(col2.getColumnType().split("\\(|\\)")[0]);
    }
    
    private static String getTypeName(Column col){
        return col.getColumnType().split("\\(|\\)")[0];
    }
    
    private static int getTypelength1(Column col){
        return Integer.parseInt(col.getColumnType().split("\\(|\\)|,")[1]);
    }
    
    private static int getTypelength2(Column col){
        return Integer.parseInt(col.getColumnType().split("\\(|\\)|,")[2]);
    }
    
    private static boolean isIn(String s , String[] table){
            for(String e : table){
                if(s.toUpperCase().equals(e.toUpperCase())){return true;}
            }
        return false;
    };
    
    private static int getIndexOf(String element, String[] table){
        int i =0;
        while(i<table.length){
        if (element.toUpperCase().equals(table[i].toUpperCase())) return i;
        i++;
        }
        return -1;
    }
    
    private static boolean isUnsigned(Column col){
        return col.getColumnType().toUpperCase().contains("unsigned".toUpperCase());
    }
    
    

    @Override
    public boolean hasNext() {
        return iteratorIndex<this.fks.size();
    }

 /**
* Return the next Transformation Object of the iteration that was configured to make able the adding of foreign key if it is possible.
* @throws RuntimeException if the tables and/or the columns from the fk parameter don't exist on the DB  
* @return
*  - If the fk is already on the DB, return an EmptyTransformation.</br>
*  - If it is possible, return a DBTransformation Object that was configured to make able the adding of key foreign</br>
*  - Finally, if the algorithm don't find a possible transformation it return a ImpossibleTransformation </br>  
* @see Transformation
* @see DBTransformation
* @see EmptyTransformation
* @see ImpossibleTransformation
*
*/  
    @Override
    public Transformation next() {
        Transformation out = null;
        iteratorIndex++;
        try {
            out = analyse(fks.get(iteratorIndex-1));
        } catch (LoadUnexistentTableException ex) {
            throw new RuntimeException("Load Unexistent Table Exception");
        }
        return out;
    }
    /**
     * 
     * @param table 
     * @param fk 
     * @return TRUE if the foreign key is alread on the database. Otherwise, return FALSE
     */
    private boolean fkAlreadyExist (Table table, ForeignKey fk){
        boolean out = false;
        
        for (ForeignKey tableFk : table.getForeignKeys()){
            if(tableFk.equals(fk)){return true;}
        }
        return out;
    }
    
    /**
     * if the Table is already on the dicoTable attribut return the object stored
     * if not load creat the table with de DB metadata store it and finally return it 
     * @param tableName name of the table that will be return
     * @return a configured Table object  
     * @throws SQLException 
     */
    private Table loadTable(String tableName) throws SQLException{
        Table out = null;
        if (dicoTable.containsKey(tableName)){
            out = dicoTable.get(tableName);
        }else{
                out = factory.loadTable(tableName);
                dicoTable.put(tableName,out);
        }
        return out;
    }
}
