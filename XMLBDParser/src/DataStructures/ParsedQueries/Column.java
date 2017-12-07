package DataStructures.ParsedQueries;

import java.io.Serializable;

/**
 *
 * @author carl
 */
public class Column implements Serializable {
    
    private String id;
    private boolean explicite;

    public Column() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isExplicite() {
        return explicite;
    }

    public void setExplicite(boolean explicite) {
        this.explicite = explicite;
    }

    @Override
    public String toString() {
        return "Column{" + "id=" + id + ", explicite=" + explicite + '}';
    }
    
    
    
}
