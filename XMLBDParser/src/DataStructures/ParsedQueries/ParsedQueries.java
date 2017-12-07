/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures.ParsedQueries;

import java.util.List;
import DataStructures.ParsedQueries.Query;
import java.util.ArrayList;
/**
 *
 * @author carl
 */
public class ParsedQueries {
    private String module;
    private int part;
    private String url;
    private String hash;
    private String date;
    private List<Query> queries;

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
    
    public ParsedQueries() {
        queries= new ArrayList();
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public List<Query> getQueries() {
        return queries;
    }

    @Override
    public String toString() {
        return "ParsedQueries{" + "module=" + module + ", part=" + part + ", url=" + url + ", hash=" + hash + ", date=" + date + ", queries=" + queries + '}';
    }
    
    
    
    
}
