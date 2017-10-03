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
public class TypeMismatching extends DBTransformation {

    public TypeMismatching(String dataBaseHostName, String dataBasePortNumber, String dataBaseLogin, String dataBasePassword, ForeignKey fk) {
        super(dataBaseHostName, dataBasePortNumber, dataBaseLogin, dataBasePassword, fk);
    }

    @Override
    public void transfrom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unDoTransformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
