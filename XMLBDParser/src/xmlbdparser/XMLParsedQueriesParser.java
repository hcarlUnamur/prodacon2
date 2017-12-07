package xmlbdparser;

import DataStructures.ParsedQueries.ParsedQueries;
import DataStructures.Queries.Queries;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author carl
 */
public class XMLParsedQueriesParser {
    
    public static ParsedQueries  ParseFile(File file) throws Exception{   
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        xmlbdparser.XMLParsedQueriesHandler parser = new XMLParsedQueriesHandler();
        saxParser.parse(file, parser);
        return parser.getParsedQueries();
    }
    
    public static ParsedQueries ParseFile(String filePath) throws Exception{   
        return ParseFile(new File(filePath));
    }
    
}
