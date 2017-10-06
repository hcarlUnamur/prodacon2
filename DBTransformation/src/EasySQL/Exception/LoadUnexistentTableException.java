package EasySQL.Exception;

import java.sql.SQLException;

public class LoadUnexistentTableException extends SQLException{

    public LoadUnexistentTableException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public LoadUnexistentTableException(String reason, String SQLState) {
        super(reason, SQLState);
    }

    public LoadUnexistentTableException(String reason) {
        super(reason);
    }

    public LoadUnexistentTableException() {
    }

    public LoadUnexistentTableException(Throwable cause) {
        super(cause);
    }

    public LoadUnexistentTableException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public LoadUnexistentTableException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public LoadUnexistentTableException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }

    
    
}
