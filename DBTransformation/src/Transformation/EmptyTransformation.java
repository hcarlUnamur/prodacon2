package Transformation;

/**
 *
 * Object that represente that there are no need some transformation, the foreign key is already on the DB 
 * 
 * @author carl
 */
public class EmptyTransformation extends Transformation {

    private String message;

    public EmptyTransformation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
}
