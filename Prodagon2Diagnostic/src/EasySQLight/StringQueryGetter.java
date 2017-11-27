/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQLight;

import java.sql.SQLException;

/**
 *
 * @author carl_
 */
public interface StringQueryGetter {
    
    public String getStringSQLQueryDo() throws SQLException;
    public String getStringSQLQueryUndo() throws SQLException;
    
}
