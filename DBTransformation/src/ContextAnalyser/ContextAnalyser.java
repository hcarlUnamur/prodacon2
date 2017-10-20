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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carl_
 */
public class ContextAnalyser implements Iterator<Transformation> {
    
    private static String[] INT_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT"};
    private static String[] NUMERIC_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static String[] ALPHA_NUMERIC_TYPES = {"ENUM","CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT"};
    private static String[] TIME_TYPES = {"TIME","YEAR","DATE","TIMESTAMP","DATETIME"};
    private static String[] TIME_TYPES_TRANSFORMABLE = {"TIMESTAMP","DATETIME"};
    private static String[] ONE_PARAMETER_TYPE={"YEAR","CHAR","VARCHAR"}; 
    private static String[] TWO_PARAMETER_TYPE={"FLOAT","DOUBLE","DECIMAL"};
    
    private ArrayList<ForeignKey> fks;
    private EasySQL.SQLQueryFactory factory;
    private HashMap<String,Table> tableLoaded;
    private int iteratorIndex;
    
    public ContextAnalyser(String dataBaseHostName,String dbName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ArrayList<ForeignKey> fks) {

        this.factory= new EasySQL.SQLQueryFactory(dataBaseHostName,dbName,dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fks = fks;
        this.tableLoaded = new HashMap<String,Table>();
        this.iteratorIndex=0;
    }
       
    // create de transformation map , try to find a transformation strategy for all foreignKeys;
    public Transformation analyse(ForeignKey fk) throws LoadUnexistentTableException{
        Table usedTable = null;
        Table referencedTable = null;
        try{
                usedTable = factory.loadTable(fk.getForeingKeyTable());
                referencedTable = factory.loadTable(fk.getReferencedTableName());
        }catch(SQLException e){
               throw new LoadUnexistentTableException("It's impossible to loade the foreign key table. they can don't exist");
        }
        
        
                Column fkColumn = usedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getForeingKeyColumn()))
                                            .findFirst()
                                            .get();
                
                Column referencedColumn = referencedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getReferencedColumn()))
                                            .findFirst()
                                            .get();
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
        return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, fkColumn.getColumnType(),TransformationType.MBT);        
    }
    
    private Transformation typeMismatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        //System.out.println("*****************Type mismatching ");
        //signed and unsigned type
        if(isUnsigned(fkColumn)!=isUnsigned(referencedColumn)){
            //System.out.println("***************** signed and unsigned type");
            return new ImpossibleTransformation("Type mismatching : signed and unsigned type");
            
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
                return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.NTT);        
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to " + referencedColumn.getColumnType());
                return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.NTT);
            }       
        }
        else if((isIn(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES) && isIn(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES);
            if(fktypeindex>reftypeindex){
                //System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+fkColumn.getColumnType()  );
                 return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.ANTT);
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to "+ referencedColumn.getColumnType());
                return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.ANTT);
            } 
        }
        else if((isIn(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE) && isIn(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE);
            if(fktypeindex>reftypeindex){
                //System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+fkColumn.getColumnType()  );
                return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.TTT);
            }
            else if(fktypeindex<reftypeindex){
                //System.out.println("***************** Transformation :"+ fk.getForeingKeyTable() +" "+fkColumn.getColumnType() +" to "+ referencedColumn.getColumnType());
                return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.TTT);
            } 
        }else{
            return new ImpossibleTransformation("Type mismatching and no transformation found");
        }
        return new ImpossibleTransformation("Type mismatching and no transformation found");
    }
    
    private Transformation sameTypeButDifferentlength(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn){
            //System.out.println("*****************Same type but different length ");

            if (isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE)){
                //System.out.println("*****************Case 1 ");
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                    //System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn.getColumnType() + " to " +referencedColumn.getColumnType() );
                    // @Do : ajouter action
                    return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);        
                    
                }else{
                    //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +fkColumn.getColumnType() );
                    // @Do : ajouter action 
                    return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT); 
                }
            }
            
            else if (isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE)){
                //System.out.println("*****************Case 2 ");
                //System.out.println(fkColumn.getColumnType()+""+referencedColumn.getColumnType());
                if(getTypelength2(fkColumn)!=getTypelength2(referencedColumn)){
                    //System.out.println("*****************Transforamtion impossible mismatching decimal length : " + fkColumn.getColumnType() + " -/-> " + referencedColumn.getColumnType());
                    return new ImpossibleTransformation("Transformation impossible mismatching decimal length : " + fkColumn.getColumnType() + " -/-> " + referencedColumn.getColumnType());
                }else if(getTypelength1(fkColumn)!=getTypelength1(referencedColumn)){
                        if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                        //System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn + " to " +getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")" );
                        // @Do : ajouter action
                        return new DBTransformation(   factory,
                                                                        tableLoaded, 
                                                                        fk, TransformationTarget.ForeignKeyTable,
                                                                        getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")",
                                                                        TransformationType.LMTT
                                                    ); 
                    }else{
                        //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")" );
                        // @Do : ajouter action
                        return  new DBTransformation(   factory,
                                                                        tableLoaded, 
                                                                        fk, TransformationTarget.ReferencedTable,
                                                                        getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")",
                                                                        TransformationType.LMTT
                                                                    );
                        
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
                    return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ForeignKeyTable, referencedColumn.getColumnType(),TransformationType.LMTT);        
                    
                }else{
                    //System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +fkColumn.getColumnType() );
                    // @Do : ajouter action 
                    return new DBTransformation(factory, tableLoaded, fk, TransformationTarget.ReferencedTable, fkColumn.getColumnType(),TransformationType.LMTT); 
                }
            }
            // juste pour afoir un return de fin noralement ne tombe jamais dedans 
            return new ImpossibleTransformation("Type mismatching and no transformation found");
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
        return col.getColumnType().contains("unsigned");
    }

    @Override
    public boolean hasNext() {
        return iteratorIndex<this.fks.size();
    }

    @Override
    public Transformation next() {
        Transformation out = null;
        iteratorIndex++;
        try {
            out = analyse(fks.get(iteratorIndex-1));
        } catch (LoadUnexistentTableException ex) {
            Logger.getLogger(ContextAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    private boolean fkAlreadyExist (Table table, ForeignKey fk){
        boolean out = false;
        
        for (ForeignKey tableFk : table.getForeignKeys()){
            if(tableFk.equals(fk)){return true;}
        }
        return out;
    }

}
