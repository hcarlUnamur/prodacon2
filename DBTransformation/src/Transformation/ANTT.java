/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

import EasySQL.ForeignKey;
import EasySQL.SQLQueryFactory;
import EasySQL.Table;
import java.util.ArrayList;

/**
 *
 * @author carl_
 */
public class ANTT extends TypeMismatching {

    public ANTT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
    }

    public ANTT(SQLQueryFactory sqlFactory, ForeignKey fk) {
        super(sqlFactory, fk);
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
