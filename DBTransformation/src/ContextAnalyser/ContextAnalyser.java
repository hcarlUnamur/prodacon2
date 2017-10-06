package ContextAnalyser;

import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.Table;
import Transformation.DBTransformation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import EasySQL.Exception.LoadUnexistentTableException;
/**
 *
 * @author carl_
 */
public class ContextAnalyser {
    
    private static String[] NUMERIC_TYPES = {"INT","TINYINT","SMALLINT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static String[] ALPHA_NUMERIC_TYPES = {"CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT","ENUM"};
    private static String[] TIME_TYPES = {"DATE","DATETIME","TIMESTAMP","TIME","YEAR"};
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
                
                // peut être renvoyé une exception si une des 2 columns est null
                
                if (fkColumn.getColumnType().equals(referencedColumn.getColumnType())){
                    perfectTypeMatching(usedTable, referencedTable, fkColumn, referencedColumn);
                }else{
                    typeMismatching(usedTable, referencedTable, fkColumn, referencedColumn);
                }
        }
    }
    
    private void perfectTypeMatching(Table usedTable, Table referencedTable,Column fkColumn,Column referencedColumn ){
        System.out.println("*****************Perfect Type Matching");
        // @ToDo : ajouter action 
    }
    
    private void typeMismatching(Table usedTable, Table referencedTable,Column fkColumn,Column referencedColumn ){
        System.out.println("*****************Type mismatching ");
        
        if (isTheSameType(fkColumn, referencedColumn)){
            sameTypeButDifferentlength(usedTable, referencedTable, fkColumn, referencedColumn);
        }
    }
    
    private static void sameTypeButDifferentlength(Table usedTable, Table referencedTable,Column fkColumn,Column referencedColumn){
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
            
            if (isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE)){
                System.out.println("*****************Case 2 ");
                int maxlength1 = getTypelength1(referencedColumn)>getTypelength1(fkColumn) ? getTypelength1(referencedColumn) :getTypelength1(fkColumn);  
                int maxlength2 = getTypelength2(referencedColumn)>getTypelength2(fkColumn) ? getTypelength2(referencedColumn) :getTypelength2(fkColumn);
                System.out.println("*****************Transformation [fk table] : " + fkColumn.getColumnName() + " " + fkColumn.getColumnType() + " to " +getTypeName(referencedColumn)+"("+maxlength1+","+maxlength2+")" );
                System.out.println("*****************Transformation [ref table] : " + referencedColumn.getColumnName() + " " + referencedColumn.getColumnType() + " to " +getTypeName(referencedColumn)+"("+maxlength1+","+maxlength2+")" );
                // @ToDo : ajouter action  
            }
            
            if(!isIn(getTypeName(fkColumn), ONE_PARAMETER_TYPE) && !isIn(getTypeName(fkColumn), TWO_PARAMETER_TYPE) ){
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
    
}
