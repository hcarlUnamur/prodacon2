package Main;

import DataStructures.ParsedQueries.ParsedQueries;
import DataStructures.Queries.Queries;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import xmlbdparser.XMLParsedQueriesParser;
import xmlbdparser.XMLQueriesParser;
import java.io.DataInputStream;

/**
 *
 * @author carl_
 */
public class Main {
    
     public static void main(String[] args) throws Exception{
        main3();
     }
 
    //test parser
    public static void main1(){
        try {
        String path ="./XMLFile/TranslatedHibernateQueries/697.xml";
        Queries q = XMLQueriesParser.ParseFile(path);
        q.getQueries().forEach(System.out::println);
      } catch (Exception e) {
         e.printStackTrace();
      }
    
    }
    
    //Serializable parsed data
    public static void main2() throws Exception{
        String[] inputPaths ={
            "C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\JDBCQueries\\697.xml",
            "C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\HibernateQueries\\697.xml",
            "C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\ParsedHibernateQueries\\697.accesses.xml",
            "C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\ParsedJDBCQueries\\697.accesses.xml",
            "C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\TranslatedHibernateQueries\\697.xml"
        };
        
        String[] outputPaths ={
            "./Serializable/JDBCQueries.queries",
            "./Serializable/HibernateQueries.queries",
            "./Serializable/ParsedHibernateQueries.pqueries",
            "./Serializable/ParsedJDBCQueries.pqueries",
            "./Serializable/TranslatedHibernateQueries.queries"
        };
        
        
        
        for(int i=0;i<outputPaths.length;i++){
            if (outputPaths[i].endsWith(".queries")){
                System.out.println("Begin file parsing : " + inputPaths[i] );
                Queries q = XMLQueriesParser.ParseFile(inputPaths[i]);
                System.out.println("Begin file writing : " + outputPaths[i] );

                
                ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(new FileOutputStream(outputPaths[i])));
                out.writeObject(q);
                out.close();       
                System.out.println("END file writing");
            } else if(outputPaths[i].endsWith(".pqueries")){
                System.out.println("Begin file parsing : " + inputPaths[i] );
                ParsedQueries q = XMLParsedQueriesParser.ParseFile(inputPaths[i]);
                System.out.println("Begin file writing : " + outputPaths[i] );

                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputPaths[i]));
                out.writeObject(q);
                out.close();       
                System.out.println("END file writing");
            }
        }
    }
    // test reading Serializable file
    public static void main3() throws Exception{
        /*
        FileInputStream fileIn = new FileInputStream("C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\Serializable\\HibernateQueries.queries");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Queries q = (Queries) in.readObject();
        in.close();
        fileIn.close();
        */
        LocalDateTime t1 = LocalDateTime.now();
        ParsedQueries q =ParsedQueries.loadFromFile("./Serializable/ParsedJDBCQueries.pqueries");
        LocalDateTime t2 = LocalDateTime.now();
        
        System.out.println("loading duration = " + Duration.between(t1, t2).getSeconds());
        
        t1 = LocalDateTime.now();
        ParsedQueries q2 =ParsedQueries.loadFromXMLFile("C:\\Users\\carl_\\Documents\\GitHub\\prodacon2\\XMLBDParser\\XMLFile\\ParsedJDBCQueries\\697.accesses.xml");
        t2 = LocalDateTime.now();
        
        System.out.println("parsing duration = " + Duration.between(t1, t2).getSeconds());
        
        //q.getQueries().forEach(System.out::println);
    }
    
}
