package DataStructures.ParsedQueries;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author carl
 */
public class Query {
    private int id;
    private boolean error;
    private List<Table> tables;
    private List<Column> columns;

    public Query() {
        tables= new ArrayList();
        columns =new ArrayList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> Columns) {
        this.columns = Columns;
    }

    @Override
    public String toString() {
        return "Query{" + "id=" + id + ", error=" + error + ", tables=" + tables + ", columns=" + columns + '}';
    }
        
    
    
}
