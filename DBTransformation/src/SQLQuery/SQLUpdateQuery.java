package SQLQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.MessageUtils;

public class SQLUpdateQuery extends SQLManipulationQuery{
    
    private static String QUERYFORMAT = "UPDATE %s SET %s WHERE %s;"; 
    private String[][] setValues;
    private String condValues;
    private ResultSet datasave ;

    public SQLUpdateQuery(String[] table, Connection con) {
        super(table, con);
    }
    
    private void makeDataSave(){
        SQLSelectQuery select = new SQLSelectQuery(getTable()[0], getCon(), new String[]{"*"}, condValues);
            try {
                datasave = select.sqlQueryDo();
            } catch (SQLException ex) {
                Logger.getLogger(SQLUpdateQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public SQLUpdateQuery(String table, Connection con, String[][] setValues, String condValues){
        super(new String[]{table}, con);
            this.setValues = setValues;
            this.condValues = condValues;   
    }
    
    @Override
    public Object sqlQueryDo() throws SQLException {
        makeDataSave();
        Statement stmt = this.getCon().createStatement();
        String s = getTable()[0];
        String setChange = StringTool.UpdateSetVal(setValues);
        String cond = condValues;
        String query = String.format(QUERYFORMAT,s,setChange, cond);
        System.out.println(query);
        stmt.executeUpdate(query);
        try{
         if(stmt!=null)
            stmt.close();
        }catch(SQLException se2){}
        return null;
    }

    //attention l'implémentation actuelle ne prens pas tout els cas par exemple un update qui istence tout les éléments de 1 peut de pas être accepté si il y a une contrainte d'unisité 
    @Override
    public Object sqlQueryUndo() throws SQLException {
        StringBuilder where = new StringBuilder();
        for(int i = 0;i<setValues.length;i++){
                where.append(" AND  " +setValues[i][0]+ "='" + setValues[i][1]+"'");
            }
            where.delete(0, 4);
        
        while (datasave.next()){
            ResultSetMetaData meta = datasave.getMetaData();
            String[][] modif = new String[meta.getColumnCount()][2];
            String[] modifColName = new String[meta.getColumnCount()];
            String[] modifColVal = new String[meta.getColumnCount()];
            int length = meta.getColumnCount();
                    for(int i=0;i<meta.getColumnCount();i++){
                modif[i][0] = meta.getColumnName(i+1);
                modifColName[i] = meta.getColumnName(i+1);
                modif[i][1] = datasave.getString(i+1);
                modifColVal[i] = datasave.getString(i+1);
            }
            //sans doute mieux mais doit être bien fait (i)
            //String whereUndo = undoWhereConstructor(where.toString(),modif);
            //System.out.println("oooooooooooooooooo [set] "+whereUndo);
            //System.out.println("oooooooooooooooooo"+whereUndo);
            //SQLUpdateQuery update = new SQLUpdateQuery(getTable()[0], getCon(), modif, whereUndo);
            //update.sqlQueryDo();
            System.out.println("######################################################");
            SQLInsertQuery insertQuery = new SQLInsertQuery(getTable()[0], getCon(),modifColName, modifColVal);
            insertQuery.sqlQueryDo();
        }
        System.out.println("+######################################################+");
        SQLDeleteQuery deleteQuery = new SQLDeleteQuery(getTable()[0],getCon(), where.toString());
        deleteQuery.sqlQueryDo();
        return null;
    }
    
    private String undoWhereConstructor(String firstWhereDo, String[][] modification){
        String[] modif = StringTool.UpdateSetVal(modification).split(",");
        String[] split = firstWhereDo.split("AND");
        StringBuffer out = new StringBuffer();
        
        for (String fw : split){
            for(String m : modif ){
                if((fw.split("=")[0]).equals(m.split("=")[0])){
                    out.append(" AND "+m);
                }else{
                    out.append(" AND "+fw);
                }
            }
        }
        out.delete(0, 4);
        return out.toString();
    }
}
