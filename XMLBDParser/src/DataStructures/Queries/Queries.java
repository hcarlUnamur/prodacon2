package DataStructures.Queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author carl
 */
public class Queries implements Serializable {
    
    private String module;
    private int part;
    private String url;
    private String hash;
    private String date;
    private List<Query> queries; 
    private boolean TranslatedQueries;
    
    public Queries() {
        queries = new ArrayList();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public boolean isTranslatedQueries() {
        return TranslatedQueries;
    }

    public void setTranslatedQueries(boolean TranslatedQueries) {
        this.TranslatedQueries = TranslatedQueries;
    }
   
}
