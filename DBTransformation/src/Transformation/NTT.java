/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

import EasySQL.ForeignKey;
import EasySQL.SQLQueryFactory;

/**
 *
 * @author carl_
 */
public class NTT extends TypeMismatching {

    public NTT(SQLQueryFactory sqlFactory, ForeignKey fk) {
        super(sqlFactory, fk);
    }

    
    public NTT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
    }

    
}
