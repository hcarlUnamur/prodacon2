/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasySQL;

import java.util.ArrayList;

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
    
    public static String UpdateSetVal(String[][] values){
        String cond = "";
            for(int i=0; i<values.length; i++){
                if(values[i][1]==null || values[i][1].equals("DEFAULT")){
                    cond = cond + values[i][0] + "=" + "" + values[i][1] + "";
                }else{
                    cond = cond + values[i][0] + "=" + "'" + values[i][1] + "'";
                }
                if(i < values.length - 1){
                    cond = cond + " , ";
                }
            }        
        return cond;
    }
    
    public static String WhereToStringVal(String[][] values){
        String cond = "";
            for(int i=0; i<values.length; i++){
                cond = cond + values[i][0] + "=" + "'" + values[i][1] + "'";
                if(i < values.length - 1){
                    cond = cond + " && ";
                }
            }        
        return cond;
    }
    
    public static ArrayList<String[]> InsertVal(String[][] values){
        String[] col = null;
        String[] val = null;
            for(int i=0; i<values.length; i++){
                col[i] = values[i][0];
                val[i] = values[i][1];
            }    
            
        ArrayList<String[]> ls = new ArrayList<>();
        ls.add(col); ls.add(val);
        return ls;
    }
    
}
