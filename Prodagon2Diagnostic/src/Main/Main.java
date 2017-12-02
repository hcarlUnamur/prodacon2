/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Analyse.Analyse;
import Diagnostic.Diagnostic;
import EasySQLight.ForeignKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carl
 */
public class Main {
 
    /**
     * 
     * commande example : java prodacon2Diagnostic "localhost" "3306" "DB_name" "Login" "Password" 
     * 
     */
    public static void main(String[] args){
       boolean error=false;
        for(String s : args){
            System.out.println(s);
        }
        
        try {
            List<ForeignKey> listfk = loadFkFromFile("./possible_matchesTest.txt");
             Diagnostic diag = null;
            if (args.length==0){
                try{
                    diag = new Diagnostic("localhost", "myb", "3306", "root", "root",listfk);
                }catch(Exception ex){
                    System.out.println("Error : We can creat a connexion with the database ( can be caused by wrong parameter Wrong parameters)");
                    error=true;
                }
            }else{
                try{
                    diag = new Diagnostic(args[0], args[2], args[1], args[3], args[4],listfk);
                }catch(Exception ex){
                    System.out.println("Error : We can creat a connexion with the database ( can be caused by wrong parameter Wrong parameters)");
                    error=true;
                }
            }
            if (!error){
                System.out.println("{ \"proadcon2Diagnostic\" : [ ");
                while (diag.hasNext()){
                    Analyse analyse = diag.next();
                    analyse.analyse();
                    System.out.println(analyse.getJson()+((diag.hasNext())?",":""));
                }

                System.out.println("] }");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<ForeignKey> loadFkFromFile(String filePath) throws Exception{
        BufferedReader reader = null;
        ArrayList<ForeignKey> out = new ArrayList();
        reader = new BufferedReader((new FileReader(new File(filePath))));
        String text = null;
        int i=0;
        while ((text = reader.readLine()) != null) {               
            String[] tab = text.split(":|,");
            out.add(new ForeignKey(tab[2], tab[3], tab[1], tab[0],("fk_Constraint_"+tab[0]+"_"+tab[2]+"_"+i) ));
            i++;
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {}
        return out;
    }

}
