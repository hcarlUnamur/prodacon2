/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlbdparser;

import DataStructures.Queries.Query;
import DataStructures.Queries.Call;
import DataStructures.Queries.Queries;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author carl_
 */
public class XMLQueriesHandler extends DefaultHandler {

    private Queries queries;
    private Query currentQuery;
    private Call currentcall;
    private int counter=1;
    
    private boolean bQueries = false;
    private boolean bCall = false;
    private boolean bQuery = false;
    private boolean bPath = false;

    public XMLQueriesHandler() {
    }

    public Queries getQueries() {
        return queries;
    }
       
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("Queries") || qName.equalsIgnoreCase("TranslatedQueries") ){
            queries = new Queries();
            queries.setDate(attributes.getValue("date"));
            queries.setHash(attributes.getValue("hash"));
            queries.setModule(attributes.getValue("module"));
            queries.setPart(Integer.valueOf(attributes.getValue("part")));
            queries.setUrl(attributes.getValue("url"));
        }else if (qName.equalsIgnoreCase("Query")) {
            bQuery = true;
            //System.out.println("<Query>");
            currentQuery = new Query();
            currentQuery.setId(Integer.valueOf(attributes.getValue("id")));
            currentQuery.setContainsNull(Boolean.valueOf(attributes.getValue("containsNull")));
            currentQuery.setType(attributes.getValue("type"));
            currentQuery.setQuery(attributes.getValue("query"));
            currentQuery.setIsNull(Boolean.valueOf(attributes.getValue("isNull")));
        }else if(qName.equalsIgnoreCase("Call")){
            bCall = true;
            //System.out.println("<Call>");
            currentcall = new Call();
            currentcall.setBeginCol(Integer.valueOf(attributes.getValue("beginCol")));
            currentcall.setBeginLine(Integer.valueOf(attributes.getValue("beginLine")));
            currentcall.setEndCol(Integer.valueOf(attributes.getValue("endCol")));
            currentcall.setEndLine(Integer.valueOf(attributes.getValue("endLine")));           
        }else if(qName.equalsIgnoreCase("Path")){
            //System.out.println("<Path>");
            bPath = true;
        }else if(qName.equalsIgnoreCase("OrigQuery")){
            currentQuery.setOrigQuery(attributes.getValue("origQuery"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Query")) {
            //System.out.println("</Query>");
            //System.out.println("Query loaded : " + counter++);
            queries.getQueries().add(currentQuery);
            currentQuery=null;
        }else if (qName.equalsIgnoreCase("Call")){
            //System.out.println("</Call>");
            currentQuery.getCallgraph().add(currentcall);
            currentcall=null;
        }else if(qName.equalsIgnoreCase("Path")){
            //System.out.println("</Path>");
        } 
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(bCall){      
            bCall = false;
        }else if(bQuery){ 
            bQuery=false;
        }else if(bPath){
            currentcall.setPath(new String(ch, start, length));
            bPath= false;
        }
    }
}
