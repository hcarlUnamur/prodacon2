package ContextAnalyser;

import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.Table;
import Transformation.DBTransformation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import EasySQL.Exception.LoadUnexistentTableException;
/**
 *
 * @author carl_
 */
public class ContextAnalyser {
    
    private static String[] INT_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT"};
    private static String[] NUMERIC_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static String[] ALPHA_NUMERIC_TYPES = {"ENUM","CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT"};
    private static String[] TIME_TYPES = {"TIME","YEAR","DATE","TIMESTAMP","DATETIME"};
    private static String[] TIME_TYPES_TRANSFORMABLE = {"TIMESTAMP","DATETIME"};
    private static String[] ONE_PARAMETER_TYPE={"YEAR","CHAR","VARCHAR"}; 
    private static String[] TWO_PARAMETER_TYPE={"FLOAT","DOUBLE","DECIMAL"};
    
    private ArrayList<ForeignKey> fks;
    private HashMap<ForeignKey,DBTransformation> transformations; 
    private EasySQL.SQLQueryFactory factory;
    private HashMap<String,Table> tableLoaded;
    
    public ContextAnalyser(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ArrayList<ForeignKey> fks) throws LoadUnexistentTableException {

        this.factory= new EasySQL.SQLQueryFactory(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword);
        this.fks = fks;
        this.transformations = new HashMap();
        this.tableLoaded = new HashMap<String,Table>();
        //load concerned by the transformation tables
        for(ForeignKey fk : fks){
            try{
                tableLoaded.put(fk.getForeingKeyTable(),factory.loadTable(fk.getForeingKeyTable()));
                tableLoaded.put(fk.getReferencedTableName(),factory.loadTable(fk.getReferencedTableName()));
            }catch(SQLException e){throw new LoadUnexistentTableException("It's impossible to loade the foreign key table. they can don't exist");}
        }
    }
       
    // create de transformation map , try to find a transforamtion strategy for all foreignKeys;
    public void analyse(){
        for(ForeignKey fk : fks){
                Table usedTable = tableLoaded.get(fk.getForeingKeyTable());
                Table referencedTable = tableLoaded.get(fk.getReferencedTableName());
                
                Column fkColumn = usedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getForeingKeyColumn()))
                                            .findFirst()
                                            .get();
                
                Column referencedColumn = referencedTable.getTablecolumn().stream()
                                            .filter(c-> c.getColumnName().equals(fk.getReferencedColumn()))
                                            .findFirst()
                                            .get();
                
                
                if (fkColumn.getColumnType().equals(referencedColumn.getColumnType())){
                    perfectTypeMatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }else{
                    typeMismatching(usedTable, referencedTable,fk, fkColumn, referencedColumn);
                }
        }
    }
    
    private void perfectTypeMatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        System.out.println("*****************Perfect Type Matching");
        // @ToDo : ajouter action
        //transformations.put(fk, new MBT(factory,fk));
    }
    
    private void typeMismatching(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn ){
        System.out.println("*****************Type mismatching ");
        //signed and unsigned type
        System.out.println(getTypeName(fkColumn) + " isIn : "+ isIn(getTypeName(fkColumn), INT_TYPES) + " index : "+getIndexOf(getTypeName(fkColumn), INT_TYPES) );
        System.out.println(getTypeName(referencedColumn) + " isIn : "+ isIn(getTypeName(referencedColumn), INT_TYPES) + " index : "+getIndexOf(getTypeName(referencedColumn), INT_TYPES) );

        if(isUnsigned(fkColumn)!=isUnsigned(referencedColumn)){
            System.out.println("***************** signed and unsigned type");
            // @ToDo : ajouter action 
        }
        //same Type But Different lengths
        else if (isTheSameType(fkColumn, referencedColumn)){
            sameTypeButDifferentlength(usedTable, referencedTable,fk, fkColumn, referencedColumn);
        }
        //super type transformation
        //int type 
        else if ((isIn(getTypeName(fkColumn), INT_TYPES) && isIn(getTypeName(referencedColumn), INT_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), INT_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), INT_TYPES);
            if(fktypeindex>reftypeindex){
                System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+ fk.getConstraintName() +" "+fkColumn.getColumnType()  );
            }
            else if(fktypeindex<reftypeindex){
                System.out.println("***************** Transformation :"+ fk.getConstraintName() +" "+fkColumn.getColumnType() +" to " + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType());
            }       
        }
        else if((isIn(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES) && isIn(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), ALPHA_NUMERIC_TYPES);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), ALPHA_NUMERIC_TYPES);
            if(fktypeindex>reftypeindex){
                System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+ fk.getConstraintName() +" "+fkColumn.getColumnType()  );
            }
            else if(fktypeindex<reftypeindex){
                System.out.println("***************** Transformation :"+ fk.getConstraintName() +" "+fkColumn.getColumnType() +" to " + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType());
            } 
        }
        else if((isIn(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE) && isIn(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE))){
            int fktypeindex = getIndexOf(getTypeName(fkColumn), TIME_TYPES_TRANSFORMABLE);
            int reftypeindex = getIndexOf(getTypeName(referencedColumn), TIME_TYPES_TRANSFORMABLE);
            if(fktypeindex>reftypeindex){
                System.out.println("***************** Transformation :" + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType() +" to "+ fk.getConstraintName() +" "+fkColumn.getColumnType()  );
            }
            else if(fktypeindex<reftypeindex){
                System.out.println("***************** Transformation :"+ fk.getConstraintName() +" "+fkColumn.getColumnType() +" to " + referencedColumn.getColumnName() +" " + referencedColumn.getColumnType());
            } 
        }
    }
    
    private static void sameTypeButDifferentlength(Table usedTable, Table referencedTable,ForeignKey fk,Column fkColumn,Column referencedColumn){
            System.out.println("*****************Same type but different length ");

            if (isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE)){
                System.out.println("*****************Case 1 ");
                if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                    System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn.getColumnType() + " to " +referencedColumn.getColumnType() );
                    // @ToDo : ajouter action 
                }else{
                    System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +fkColumn.getColumnType() );
                    // @ToDo : ajouter action 
                }
            }
            
            else if (isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE)){
                System.out.println("*****************Case 2 ");
                if(getTypelength2(fkColumn)!=getTypelength2(referencedColumn)){
                    System.out.println("*****************Transforamtion impossible mismatching decimal length : " + fkColumn.getColumnType() + " -/-> " + referencedColumn.getColumnType());
                }else if(getTypelength1(fkColumn)!=getTypelength1(referencedColumn)){
                        if (getTypelength1(referencedColumn)>getTypelength1(fkColumn)){
                        System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn + " to " +getTypeName(referencedColumn)+"("+getTypelength1(referencedColumn)+","+getTypelength2(referencedColumn)+")" );
                        // @ToDo : ajouter action 
                    }else{
                        System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +getTypeName(fkColumn)+"("+getTypelength1(fkColumn)+","+getTypelength2(fkColumn)+")" );
                        // @ToDo : ajouter action 
                    }
                }

                // @ToDo : ajouter action  
            }
            
            else if(!isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE) && !isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE) ){
                System.out.println("*****************Case 3 ");
                System.out.println("*****************Different type size but don't need changement (is a zero parameter type) ");
                // @ToDo : ajouter action 
            }
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
}
