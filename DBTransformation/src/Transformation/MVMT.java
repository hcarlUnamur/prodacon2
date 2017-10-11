package Transformation;

import EasySQL.ForeignKey;
import EasySQL.SQLQueryFactory;
import EasySQL.Table;
import java.util.function.Function;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author carl_
 */
public class MVMT extends TypeMatching {
    
    private Function transformationFunction;
    
    @Deprecated
    public MVMT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        System.err.println("Warning the MVMT (String,String,String,String,String,ForeignKey) is not recomended");
    }

    public MVMT(SQLQueryFactory sqlFactory, ForeignKey fk) {
        super(sqlFactory, fk);
    }

    
    
    public MVMT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk, Function<Object,String> transformationFunction) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        this.transformationFunction = transformationFunction;
        try {
            ResultSet oldData = (getSQLFactory().createSQLSelectQuery(getTableName(), new String[]{"*"}, "1=1")).sqlQueryDo();
            ResultSetMetaData metaData = oldData.getMetaData();
            while(oldData.next()){
                String transformedData = transformationFunction.apply(oldData.getString(getFk().getForeingKeyColumn()));
                addQuery(
                            getSQLFactory().createSQLUpdateQuery(
                            getTableName(),
                            new String[][]{{getFk().getForeingKeyColumn(),transformedData}},
                            resultSetToWhereString(oldData))
                        );
            }
            addQuery(getSQLFactory().createSQLAlterAddForeignKeyQuery(tableName, fk));
        } catch (SQLException ex) {
            Logger.getLogger(MVMT.class.getName()).log(Level.SEVERE, " Erro during the database loading ", ex);
        }
    }
    
    private String resultSetToWhereString(ResultSet set) throws SQLException{
        StringBuilder out = new StringBuilder();
        ResultSetMetaData metaData = set.getMetaData();
        for(int i=0;i<metaData.getColumnCount();i++){
            out.append(" AND " + metaData.getColumnName(i+1) +" = '"+ set.getString(metaData.getColumnName(i+1))+"' ");
        }
        out.delete(0, 4);
        return out.toString();
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
