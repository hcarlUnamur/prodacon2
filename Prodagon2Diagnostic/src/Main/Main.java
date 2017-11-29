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
 
    public static void main(String[] args){
        try {
            List<ForeignKey> listfk = loadFkFromFile("./possible_matchesTest.txt");
            Diagnostic diag = new Diagnostic("localhost", "mydb", "3306", "root", "root",listfk);
            while (diag.hasNext()){
                Analyse analyse = diag.next();
                analyse.analyse();
                System.out.println(analyse.getJson());
                //System.out.println(analyse.getJson().replace(",", ","+System.lineSeparator()).replace("{", "{"+System.lineSeparator()));
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
