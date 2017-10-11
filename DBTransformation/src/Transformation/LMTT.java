/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

import EasySQL.Column;
import EasySQL.ForeignKey;
import EasySQL.SQLQueryFactory;
import EasySQL.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author carl_
 */
public class LMTT extends TypeMismatching {

    @Deprecated
    public LMTT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        System.err.println("Warning the LMTT (String,String,String,String,String,ForeignKey) is not recomended");
    }

    public LMTT(SQLQueryFactory sqlFactory, ForeignKey fk) {
        super(sqlFactory, fk);
    }
   
    public LMTT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk, HashMap<String, Table> tableMap, String superType) throws SQLException{
        
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        
        Table tF = tableMap.get(fk.getForeingKeyTable());
        Table tR = tableMap.get(fk.getReferencedTableName());
        
        Column col1 = new Column(fk.getForeingKeyColumn(), superType); 
        Column col2 = new Column(fk.getReferencedColumn(), superType);
        
        ArrayList<Column> lColF = new ArrayList<>();
        lColF = tF.getTablecolumn();
        
        for(Column c : lColF){
            if(c.getColumnName().equals(fk.getForeingKeyColumn())){
                if(!c.getColumnType().equals(superType)){                    
                    ChangeHashMap(tF, new Column(col1.getColumnName(), superType), tableMap, fk);
                    addQuery(getSQLFactory().createSQLAlterModifyColumnTypeQuery(fk.getForeingKeyTable(), col1));                    
                }
            }
        }
        
        ArrayList<Column> lColR = new ArrayList<>();
        lColR = tR.getTablecolumn();
        for(Column c : lColR){
            if(c.getColumnName().equals(fk.getReferencedColumn())){
                if(!c.getColumnType().equals(superType)){
                    ChangeHashMap(tR, new Column(col2.getColumnName(), superType), tableMap, fk);
                    addQuery(getSQLFactory().createSQLAlterModifyColumnTypeQuery(fk.getReferencedTableName(), col2));                    
                }
            }
        }
        
        addQuery(getSQLFactory().createSQLAlterAddForeignKeyQuery(fk.getForeingKeyTable(), fk.getConstraintName(), new Column(fk.getForeingKeyColumn(), superType), fk.getReferencedTableName(), fk.getReferencedColumn()));
    }
    
    private void ChangeHashMap(Table t, Column c, HashMap<String, Table> hm, ForeignKey fk){
        ArrayList<Column> lCol = new ArrayList<>();
        lCol = t.getTablecolumn();
        ArrayList<Column> lColFinal = new ArrayList<>();
        for (Column lc : lCol){
            if(lc.getColumnName().equals(c.getColumnName())){
                lColFinal.add(c);
            }else{
                lColFinal.add(lc);
            }
        }
        t.setTablecolumn(lColFinal);
        ArrayList<ForeignKey> lfk = new ArrayList<>();
        lfk = t.getForeignKeys();
        lfk.add(fk);
        t.setForeignKeys(lfk);
        hm.put(t.getName(), t);
    }
    
    
    
    @Override
    public void analyseValues() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void analyseCascade(ArrayList<Table> tables) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
