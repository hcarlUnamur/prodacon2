/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures.ParsedQueries;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * @author carl
 */
public class ParsedQueries implements Serializable {
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
    
    public static ParsedQueries loadFromFile(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException{
        ParsedQueries q = null;
            
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            FileInputStream fileIn = new FileInputStream(raf.getFD());
            BufferedInputStream buf = new BufferedInputStream(fileIn);
            DataInputStream dis = new DataInputStream(buf);
            ObjectInputStream in = new ObjectInputStream(dis);

            
            /*
              RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
              FileInputStream fileIn = new FileInputStream(raf.getFD());
              FileChannel fc = fileIn.getChannel();
              int size = (int)fc.size();
              ByteBuffer bBuff = ByteBuffer.allocate(size);
              fc.read(bBuff);
              bBuff.flip();
              ByteArrayInputStream ina = new ByteArrayInputStream(bBuff.array());
              ObjectInputStream in = new ObjectInputStream(ina);
            */
            q = (ParsedQueries) in.readObject();
            in.close();
            fileIn.close();
        
        return q;
    }
    
    public static ParsedQueries loadFromXMLFile(String filePath) throws Exception{      
        return xmlbdparser.XMLParsedQueriesParser.ParseFile(filePath);
    }
    
}
