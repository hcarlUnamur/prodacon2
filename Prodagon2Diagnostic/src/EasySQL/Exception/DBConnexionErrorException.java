/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL.Exception;

/**
 *
 * @author carl_
 */
public class DBConnexionErrorException extends RuntimeException{

    public DBConnexionErrorException() {
    }

    public DBConnexionErrorException(String message) {
        super(message);
    }

    public DBConnexionErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBConnexionErrorException(Throwable cause) {
        super(cause);
    }

    public DBConnexionErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
