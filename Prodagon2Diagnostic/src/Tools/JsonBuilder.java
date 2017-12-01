/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author carl_
 */
public class JsonBuilder {
    
    /**
     * @param objectName name of the json object
     * @param map that we want to get the json encodage
     * (value from the map need to be object with toString implemented methode or list of toStringable elements)
     * @return String that represente the json of the map object
     */
    static public String mapToJson(String objectName, Map<String,Object> map){
        StringBuilder out = new StringBuilder();
        
        if(objectName!=null){
            out.append("{ \""+objectName+"\" : {");
        }else{
            out.append("{");
        }
        map.forEach((k,v)->out.append(objectToJsonElement(k, v)));
        out.append("}");
        if(objectName!=null){
            out.append("}");
        }
        out.deleteCharAt(out.lastIndexOf(","));
        return out.toString();
    }
    static public String objectToJsonElement(String key, Object value){
        StringBuilder out = new StringBuilder();
        if(value instanceof List){
            List list = (List) value;
            out.append("\""+key+"\" : [ ");
            if(!list.isEmpty()){
                list.stream().forEach(v->out.append(v.toString()+", "));
                out.delete(out.length()-2, out.length());
            }
            out.append(" ], ");
        }else if((value.toString()).contains("{")){
            out.append("\""+key+"\" : "+value.toString()+",");
        }
        else{
            //System.out.println("******************"+key+" "+value);
            out.append("\""+key+"\" : \""+((value==null)?"null":value.toString())+"\",");
        }
        return out.toString();
    }
    
    
}
