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
public class IncompleteClassRuntimeException extends RuntimeException{

    public IncompleteClassRuntimeException() {
    }

    public IncompleteClassRuntimeException(String message) {
        super(message);
    }

    public IncompleteClassRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompleteClassRuntimeException(Throwable cause) {
        super(cause);
    }

    public IncompleteClassRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
