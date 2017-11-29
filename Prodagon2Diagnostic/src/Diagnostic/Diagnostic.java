package Diagnostic;

import Analyse.Analyse;
import Analyse.TransformationTarget;
import EasySQL.Exception.LoadUnexistentTableException;
import EasySQLight.Column;
import EasySQLight.ForeignKey;
import EasySQLight.SQLQueryFactory;
import EasySQLight.Table;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * Rework of Prodacon2.ContextAnalyser for being lighter and without side effect (no DB update, only select)
 * @author carl
 */
public class Diagnostic implements Iterator<Analyse> {
    
// Class attributs-------------------------------------------------------------------------------------------------------------------------
    private SQLQueryFactory factory;
    private List<ForeignKey> fks;
    private int iteratorIndex;
    private HashMap<String,Table> dicoTable;
// Class constants-------------------------------------------------------------------------------------------------------------------------    
    private static final String[] INT_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT"};
    private static final String[] NUMERIC_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static final String[] ALPHA_NUMERIC_TYPES = {"ENUM","CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT"};
    private static final String[] TIME_TYPES = {"TIME","YEAR","DATE","TIMESTAMP","DATETIME"};
    private static final String[] TIME_TYPES_TRANSFORMABLE = {"TIMESTAMP","DATETIME"};
    private static final String[] ONE_PARAMETER_TYPE={"YEAR","CHAR","VARCHAR"}; 
    private static final String[] TWO_PARAMETER_TYPE={"FLOAT","DOUBLE","DECIMAL"};
// Class Constructors-------------------------------------------------------------------------------------------------------------------------
    /**
     * @param dataBaseHostName the DataBase host name where we will make the transformation.  
     * @param dbName the data base name where we will make the transformation.
     * @param dataBasePortNumber the port of data base where we will make the transformation.
     * @param dataBaseLogin the login of the data base where we will make the transformation.
     * @param dataBasePassword the password of the login of the data base where we will make the transformation.
     * @param fks A list of foreign keys that we want to generate the transformation.
     */
    public Diagnostic(String dataBaseHostName,String dbName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, List<ForeignKey> fks){
        this.factory= new EasySQLight.SQLQueryFactory(dataBaseHostName,dbName,dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fks = fks;
        this.iteratorIndex=0;
        this.dicoTable = new HashMap();
    }

// Iterator overloaded methods-------------------------------------------------------------------------------------------------------------------------    
    
    @Override
    public boolean hasNext() {
        return iteratorIndex<this.fks.size();
    }

    @Override
    public Analyse next() {
        Analyse out = null;
        iteratorIndex++;
        out = makeDiagnostic(fks.get(iteratorIndex-1));
        return out;
    }

// Diagnostic heart code-------------------------------------------------------------------------------------------------------------------------
    /**
     * 
     * 
     * @param fk Foreign key that we want to generate the analyse
     * @return A completed Analyse object (work as a Factory)
     */
    public Analyse makeDiagnostic(ForeignKey fk){
        Table usedTable = null;
        Table referencedTable = null;
        try{
                usedTable = loadTable(fk.getForeingKeyTable());
                referencedTable = loadTable(fk.getReferencedTableName());
        }catch(SQLException e){
               return Analyse.impossibleToFindTable(fk);
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
                        return Analyse.impossibleToFindTable(fk,"Impossible to find : "+fk.getForeingKeyTable()+"."+fk.getForeingKeyColumn());
                    else 
                        return Analyse.impossibleToFindTable(fk,"Impossible to find : "+fk.getReferencedTableName()+"."+fk.getReferencedColumn());
                }
                //System.out.println("--------------------------------"+fkAlreadyExist(usedTable, fk));

                if (fkColumn.getColumnType().equals(referencedColumn.getColumnType())){
                    return perfectTypeMatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }else{
                    return typeMismatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }        
    }
    
    private Analyse perfectTypeMatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, fkColumn.getColumnType(), TransformationType.MBT);        
    }
    
    private Analyse typeMismatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        //same Type But Different lengths
        if (isTheSameType(fkColumn, referencedColumn)){
            return sameTypeButDifferentlength(usedTable, referencedTable,fk, fkColumn, referencedColumn);
        }
        //super type transformation
        //int type 
        else if ((isIn(getTypeName(fkColumn), INT_TYPES) && isIn(getTypeName(referencedColumn), INT_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), INT_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), INT_TYPES);
            if(fktypeindex>reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.NTT);
            }
            else if(fktypeindex<reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.NTT);
            }       
        }
        else if((isIn(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES) && isIn(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES);
            if(fktypeindex>reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.ANTT);
            }
            else if(fktypeindex<reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.ANTT);
            } 
        }
        else if((isIn(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE) && isIn(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE);
            if(fktypeindex>reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.TTT);
            }
            else if(fktypeindex<reftypeindex){
                return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.TTT);
            } 
        }else{
            return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.DTT);
        }
        return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.DTT);
    }
    
    private Analyse sameTypeButDifferentlength(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn){
            if (isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE)){
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                    return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);
                }else{
                    return new Analyse(factory, dicoTable, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT);
                }
            }
            else if (isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE)){
                if(getTypelength2(fkColumn)!=getTypelength2(referencedColumn)){
                    return new Analyse(     factory,
                                            dicoTable, 
                                            fk,
                                            TransformationTarget.ForeignKeyTable,
                                            getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")",
                                            TransformationType.LMTT
                                      );
                }else if(getTypelength1(fkColumn)!=getTypelength1(referencedColumn)){
                    if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                        return new Analyse(     factory,
                                                dicoTable, 
                                                fk, TransformationTarget.ForeignKeyTable,
                                                getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")",
                                                TransformationType.LMTT
                                           );
                    }else{
                        return new Analyse(     factory,
                                                dicoTable, 
                                                fk, TransformationTarget.ReferencedTable,
                                                getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")",
                                                TransformationType.LMTT
                                            );                      
                    }
                } 
            }           
            else if(!isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE) && !isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE) ){
                // on fait le même chose que one parameter transfo
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){

                    return new Analyse(factory, dicoTable, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);        
                }else{
                    return new Analyse(factory, dicoTable, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT); 
                }
            }
            // juste pour afoir un return de fin noralement ne tombe jamais dedans 
            return Analyse.impossibleToFindTable(fk,"Type mismatching and no transformation found [between :" + fkColumn.getColumnType() +" -> "+ referencedColumn.getColumnType()+"]");
    }
    
// tool methods-------------------------------------------------------------------------------------------------------------------------    
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
}
    
    
    

