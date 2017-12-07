package DataStructures.Queries;

import DataStructures.ParsedQueries.ParsedQueries;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
   
    public static Queries loadFromFile(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException{
        Queries q = null;
             
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
            q = (Queries) in.readObject();
            in.close();
            fileIn.close();
        return q;
    }
    
     public static Queries loadFromXMLFile(String filePath) throws Exception{      
        return xmlbdparser.XMLQueriesParser.ParseFile(filePath);
    }
    
}
