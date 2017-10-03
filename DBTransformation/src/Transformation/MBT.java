/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transformation;

import EasySQL.ForeignKey;

/**
 *
 * @author carl_
 */
public class MBT extends TypeMatching {

    public MBT(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, String tableName, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, tableName, fk);
        this.addQuery(getSQLFactory().creatSQLAlterAddForeignKeyQuery(this.getTableName(), fk));
    }
 
}
