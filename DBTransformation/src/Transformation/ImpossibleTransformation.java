package Transformation;

/**
 *
 * Object instantiated when the algorithm don't fin a possible transformation
 * @author carl
 */
public class ImpossibleTransformation extends Transformation {

    private String message;

    public ImpossibleTransformation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    
}
