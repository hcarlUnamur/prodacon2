package Main;

import DataStructures.ParsedQueries.ParsedQueries;
import DataStructures.Queries.Queries;
import xmlbdparser.XMLParsedQueriesParser;
import xmlbdparser.XMLQueriesParser;

/**
 *
 * @author carl_
 */
public class Main {
 
    public static void main2(String[] args){
        try {
        String path ="./XMLFile/TranslatedHibernateQueries/697.xml";
        Queries q = XMLQueriesParser.ParseFile(path);
        q.getQueries().forEach(System.out::println);
      } catch (Exception e) {
         e.printStackTrace();
      }
    
    }
    
    public static void main(String[] args){
        try {
        String path ="C:\\Users\\carl_\\Documents\\NetbeanProject\\XMLBDParser\\XMLFile\\ParsedHibernateQueries\\697.accesses.xml";
        ParsedQueries q = XMLParsedQueriesParser.ParseFile(path);
        q.getQueries().forEach(System.out::println);
      } catch (Exception e) {
         e.printStackTrace();
      }
    
    }
}
