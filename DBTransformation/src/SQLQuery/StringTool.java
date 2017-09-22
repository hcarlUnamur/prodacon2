/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

/**
 *
 * @author carl_
 */
public class StringTool {
    
    public static String ArrayToString(String[] arr){
        StringBuilder out= new StringBuilder();
        for(String s : arr){
            out=out.append(", "+s);
        }
        return out.substring(1, out.length());
    }
    
    public static String ArrayToStringInsert(String[] arr) {
        StringBuilder out= new StringBuilder();
        for(String s : arr){
            out=out.append(", "+"\"" + s + "\"");
        }
        return out.substring(1, out.length());
    }
    
}
