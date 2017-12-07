package xmlbdparser;

import DataStructures.ParsedQueries.Column;
import DataStructures.ParsedQueries.ParsedQueries;
import DataStructures.ParsedQueries.Query;
import DataStructures.ParsedQueries.Table;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author carl
 */
public class XMLParsedQueriesHandler extends DefaultHandler {

    private ParsedQueries parsedQueries;
    private Query currentQuery;

    public ParsedQueries getParsedQueries() {
        return parsedQueries;
    }
   

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
       if (qName.equalsIgnoreCase("ParsedQueries")){
            parsedQueries = new ParsedQueries();
            parsedQueries.setDate(attributes.getValue("date"));
            parsedQueries.setHash(attributes.getValue("hash"));
            parsedQueries.setModule(attributes.getValue("module"));
            parsedQueries.setPart(Integer.valueOf(attributes.getValue("part")));
            parsedQueries.setUrl(attributes.getValue("url"));
        }else if (qName.equalsIgnoreCase("Query")) {
            currentQuery = new Query();
            currentQuery.setId(Integer.valueOf(attributes.getValue("id")));
            currentQuery.setError((attributes.getValue("error").equals("1")?true:false));
        }else if (qName.equalsIgnoreCase("Table")) {
            Table t = new Table();
            t.setId(attributes.getValue("id"));
            t.setExplicite((attributes.getValue("explicit").equals("1")?true:false));
            currentQuery.getTables().add(t);
        }else if (qName.equalsIgnoreCase("Column")) {
            Column c = new Column();
            c.setId(attributes.getValue("id"));
            c.setExplicite((attributes.getValue("explicit").equals("1")?true:false));
            currentQuery.getColumns().add(c);
        }  
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Query")) {
            parsedQueries.getQueries().add(currentQuery);
            currentQuery=null;
        }
    }

    
}
