package DataStructures.Queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author carl
 */
public class Query implements Serializable {
    
    private int id;
    private String type;
    private String query;
    private boolean isNull;
    private boolean containsNull;
    private List<Call> callgraph;
    private String origQuery;

    public Query() {
        callgraph = new ArrayList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isIsNull() {
        return isNull;
    }

    public void setIsNull(boolean isNull) {
        this.isNull = isNull;
    }

    public boolean isContainsNull() {
        return containsNull;
    }

    public void setContainsNull(boolean containsNull) {
        this.containsNull = containsNull;
    }

    public List<Call> getCallgraph() {
        return callgraph;
    }

    public void setCallgraph(List<Call> callgraph) {
        this.callgraph = callgraph;
    }

    public String getOrigQuery() {
        return origQuery;
    }

    public void setOrigQuery(String OrigQuery) {
        this.origQuery = OrigQuery;
    }

    @Override
    public String toString() {
        return "Query{" + "id=" + id + ", type=" + type + ", query=" + query + ", isNull=" + isNull + ", containsNull=" + containsNull + ", callgraph=" + callgraph + ", OrigQuery=" + origQuery + '}';
    }
    
    
    
    
    
}
