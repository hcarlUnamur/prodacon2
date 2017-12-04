/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thibaud
 */
public class SQLCreateTriggerQuery extends SQLManipulationQuery implements StringQueryGetter {
    
    private static String QUERYFORMATINSERT = "CREATE TRIGGER %s " +
                                              "BEFORE INSERT ON %s " +
                                              "FOR EACH ROW " +
                                              "BEGIN " +
                                                "IF not (new.%s IN (SELECT %s from %s)) " +
                                              "THEN " +
                                                "INSERT INTO %s (foreignKeyName, foreignKeyTable, foreignKeyColumn, referencedTable, referencedColumn, problemAction) values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"insert\"); " +
                                              "END IF; " +
                                              "END;";
    
    private static String QUERYFORMATDELETE = "CREATE TRIGGER %s " +
                                              "BEFORE delete ON %s " +
                                              "FOR EACH ROW " +
                                              "BEGIN " +
                                                 "IF (old.%s IN (SELECT %s from %s)) " +
                                              "THEN " +
                                                 "INSERT INTO %s (foreignKeyName, foreignKeyTable, foreignKeyColumn, referencedTable, referencedColumn, problemAction) values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"delete\"); " +
                                              "END IF; " +
                                              "END;";
    
    private static String QUERYFORMATUPDATEFK = "CREATE TRIGGER %s " +
                                                "BEFORE UPDATE ON %s " +
                                                "FOR EACH ROW " +
                                                "BEGIN " +
                                                   "IF((old.%s <> new.%s) AND not (new.%s IN (SELECT %s from %s))) " +
                                                "THEN " +
                                                   "INSERT INTO %s (foreignKeyName, foreignKeyTable, foreignKeyColumn, referencedTable, referencedColumn, problemAction) values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"updateForeignkeyTable\"); " +
                                                "END IF; " +
                                                "END;";
    
    private static String QUERYFORMATUPDATEREF = "CREATE TRIGGER %s " +
                                                 "BEFORE UPDATE ON %s " +
                                                 "FOR EACH ROW " +
                                                 "BEGIN " +
                                                    "IF((old.%s <> new.%s) AND (old.%s IN (SELECT %s from %s))) \n" +
                                                 "THEN " +
                                                    "INSERT INTO %s (foreignKeyName, foreignKeyTable, foreignKeyColumn, referencedTable, referencedColumn, problemAction) values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"updateReferencedTable\"); " +
                                                 "END IF; " +
                                                 "END;";
    
    private String foreignKeyTable;  
    private String foreignKeyColumn;
    private String referencedTable;
    private String referencedColumn;
    private String foreignKeyName;
    private String logTable;
       
    
    public SQLCreateTriggerQuery(Connection con, ForeignKey fk, String logTable){
        super(new String[]{fk.getForeingKeyTable()}, con);
        foreignKeyTable = fk.getForeingKeyTable();
        foreignKeyColumn = fk.getForeingKeyColumn();
        referencedTable = fk.getReferencedTableName();
        referencedColumn = fk.getReferencedColumn();
        foreignKeyName = fk.getConstraintName();
        this.logTable = logTable;
    }
    
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        
        
        Statement stmt = this.getCon().createStatement();

        String queryInsert = String.format(QUERYFORMATINSERT,"triggerInsert" + this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.referencedColumn,this.referencedTable,this.logTable,this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.referencedTable,this.referencedColumn);
        String queryDelete = String.format(QUERYFORMATDELETE,"triggerDelete" + this.foreignKeyName,this.referencedTable,this.referencedColumn,this.foreignKeyColumn,this.foreignKeyTable,this.logTable,this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.referencedTable,this.referencedColumn);
        String queryUpdateFK = String.format(QUERYFORMATUPDATEFK,"triggerUpdateFK" + this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.foreignKeyColumn,this.foreignKeyColumn,this.referencedColumn,this.referencedTable,this.logTable,this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.referencedTable,this.referencedColumn);
        String queryUpdateREF = String.format(QUERYFORMATUPDATEREF, "triggerUpdateREF" + this.foreignKeyName,this.referencedTable,this.referencedColumn,this.referencedColumn,this.referencedColumn,this.foreignKeyColumn,this.foreignKeyTable,this.logTable,this.foreignKeyName,this.foreignKeyTable,this.foreignKeyColumn,this.referencedTable,this.referencedColumn);
        
        
        Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.INFO, queryInsert);
        stmt.executeUpdate(queryInsert);
        Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.INFO, queryDelete);
        stmt.executeUpdate(queryDelete);
        Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.INFO, queryUpdateFK);
        stmt.executeUpdate(queryUpdateFK);
        Logger.getLogger(SQLAlterTableQuery.class.getName()).log(Level.INFO, queryUpdateREF);
        stmt.executeUpdate(queryUpdateREF);
        
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    @Override
    public Object sqlQueryUndo() throws SQLException {
        
        return null;
    }

    @Override
    public String getStringSQLQueryDo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStringSQLQueryUndo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
