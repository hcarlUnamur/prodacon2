/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlbdparser;

import DataStructures.Queries.Queries;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author carl_
 */
public class XMLQueriesParser {
    
    public static Queries ParseFile(File file) throws Exception{   
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        xmlbdparser.XMLQueriesHandler parser = new XMLQueriesHandler();
        saxParser.parse(file, parser);
        return parser.getQueries();
    }
    
    public static Queries ParseFile(String filePath) throws Exception{   
        return ParseFile(new File(filePath));
    }
    
}
