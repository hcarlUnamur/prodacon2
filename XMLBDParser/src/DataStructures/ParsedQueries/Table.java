package DataStructures.ParsedQueries;

/**
 *
 * @author carl
 */
public class Table {
    private String id;
    private boolean explicite;

    public Table() {
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
        return "Table{" + "id=" + id + ", explicite=" + explicite + '}';
    }
    
    
    
}
