package Main;

import ContextAnalyser.ContextAnalyser;
import EasySQL.ForeignKey;
import EasySQL.SQLQuery;
import Transformation.DBTransformation;
import Transformation.EmptyTransformation;
import Transformation.ImpossibleTransformation;
import Transformation.Transformation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author carl_
 */
public class Main {
    private String dbhost;
    private String dbport;
    private String dbpw;
    private String dblogin; 
    private String fkfile;
    private File fileFkFile;
    private String dbName;
    private ArrayList<ForeignKey> fkArray;
    
    /**
     * @param args the command line arguments
     */
    public Main(){
        fkArray = new ArrayList();
    }
    
    public Main(String[] userParameter){
        this.argsParseur(userParameter);
        fkArray = new ArrayList();
        if (this.fileFkFile!=null){
            try {
                parseFKFile();
            } catch (Exception ex) {
                System.err.println("error impossible to load and/or parse : " + this.fkfile);
            }
        }
    }
    
    public static void main(String[] args) {
        args = "-dbhost localhost -dbname mydb -dbport 3306 -dblogin carl -dbpw root -fkfile ./possible_matches.txt".split(" ");
        Main main = new Main(args);
        main.mainMenu();
    }
    
    public static void drawLine(int length){
        for(int i=0;i<length;i++){
            System.out.print("-");
        }
        System.out.println("");
    }
    
    public void mainMenu(){
        drawLine(100);
        System.out.println("MAIN MENU");
        System.out.println();
        
        System.out.println("Option: ");
        System.out.println("1. Arguments Menu");
        System.out.println("2. Run context Analyser " + ((this.fkArray.size()==0)?"( Impossible action no foreign key found on file)":""));
        System.out.println("3. exit");
        System.out.println();
        
        int option = optionSelection(1,3);
        switch(option){
            case 1:
                argsMenu();
                break;
            case 2:
                if(this.fkArray.size()==0){
                    System.err.println("Impossible to proceed no foreign key found on file");
                    this.mainMenu();
                }
                System.out.println("Not implemented yet");
                break;
            case 3:
                System.out.println("Bye Bye :) ");
                System.exit(0);
                break;
                
        }
        

    }
    
    public void contextAnalyserMenu(){
        drawLine(100);
        System.out.println("CONTEXT ANALYSER MENU");
        System.out.println();
        
        ContextAnalyser contextAnalyser = new ContextAnalyser(dbhost,dbName, dbport, dblogin, dbpw, fkArray);
        while(contextAnalyser.hasNext()){
            Transformation transfo = contextAnalyser.next();
            if (transfo instanceof DBTransformation){
                DBTransformation dbtransfo = (DBTransformation)transfo;
                dbtransfo.analyse();
                //dbtransfo.
            }else if (transfo instanceof ImpossibleTransformation){
                System.out.println(((ImpossibleTransformation) transfo).getMessage());
            }else if (transfo instanceof EmptyTransformation){
                System.out.println(((EmptyTransformation) transfo).getMessage());    
            }
        }
    }
    
    public  void argsMenu(){
        drawLine(100);
        System.out.println("ARGUMENTS MENU");
        System.out.println();
        
        System.out.println("dbhost = " + this.dbhost);
        System.out.println("dbName = " + this.dbName);
        System.out.println("dblogin = " + this.dblogin);
        System.out.println("dbport = " + this.dbport);
        System.out.println("dbpassword = " + this.dbpw);
        System.out.println("foreign key file path" +" = " + this.fkfile +((fileFkFile!=null)?" (contains :"+this.fkArray.size()+" foreign keys)":""));
        System.out.println((fileFkFile.exists() ? "" : "    Warding : File don't exist"));
        System.out.println();
        
        System.out.println("Option: ");
        System.out.println("1. Main Menu");
        System.out.println("2. Set dbhost");
        System.out.println("3. Set dbname");
        System.out.println("4. Set dblogin");
        System.out.println("5. Set dbport");
        System.out.println("6. Set dbpassword");
        System.out.println("7. Set foreign key file path");
        System.out.println("8. Print foreign keys");
        System.out.println("");
        
        int option = optionSelection(1, 8);
        Scanner sc = new Scanner(System.in);
        switch(option){
            case 1 : 
                this.mainMenu();
                break;
            case 2 : 
                System.out.print("new dbhost value : ");
                this.dbhost = sc.nextLine();
                this.argsMenu();
                break;
            case 3 : 
                System.out.print("new dbName value : ");     
                this.dbName = sc.nextLine();
                this.argsMenu();
                break;
            case 4 : 
                System.out.print("new dblogin value : ");     
                this.dblogin = sc.nextLine();
                this.argsMenu();
                break;
            case 5 : 
                System.out.print("new dbport value : ");
                this.dbport = sc.nextLine();
                this.argsMenu();
                break;
             case 6 : 
                System.out.print("new dbpassword value : ");
                this.dbpw = sc.nextLine();
                this.argsMenu();
                break;
            case 7 : 
                System.out.print("new fk file value : ");
                this.fkfile = sc.nextLine();
                this.fileFkFile=new File(this.fkfile);        
                try {
                    parseFKFile();
                    System.out.println("Loading and parsing file [Success]");
                } catch (Exception ex) {
                    System.err.println("Error impossible to load and/or parse [Fails] : " + this.fkfile);
                    this.fkArray = new ArrayList();
                }
                this.argsMenu();
                break;
            case 8 :
                this.fkArray.forEach(System.out::println);
                System.out.println("");
                System.out.print("Press a key to come back to ARGUMENTS MENU ");
                this.fkfile = sc.nextLine();
                this.argsMenu();
                break;
        }
        
    }
    
    public int optionSelection(int min, int max){
        Scanner sc = new Scanner(System.in);
        int out =0;
        try{
            System.out.print("Select a option : ");
            String str = sc.nextLine();
            out =Integer.parseInt(str);
            if (out>max || out<min)throw new NumberFormatException();
        }catch(NumberFormatException e){
            System.err.println(String.format("Invalide input (must be number between %d and %d)",min,max));
            return optionSelection(min,max);
        }
        return out;
    }
    
    public void parseFKFile() throws Exception{
        BufferedReader reader = null;
        
            reader = new BufferedReader((new FileReader(this.fileFkFile)));
            String text = null;
            int i=0;
            while ((text = reader.readLine()) != null) {               
                String[] tab = text.split(":|,");
                this.fkArray.add(new ForeignKey(tab[2], tab[3], tab[0], tab[1],"fk_Constraint"+i));
                i++;
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {}
}

   
    
    public void argsParseur(String[] args){
        HashMap<String,String> out = new HashMap();
        for(int i=0;i<args.length;i++){
            switch(args[i]){
                case "-dbhost":
                    this.dbhost = args[i+1];
                    i++;
                    break;
                case "-dbport":
                    this.dbport = args[i+1];
                    i++;
                    break;
                case "-dblogin":
                    this.dblogin = args[i+1];
                    i++;
                    break;
                case "-dbpw":
                    this.dbpw = args[i+1];
                    i++;
                    break;
                 case "-dbname":
                    this.dbName = args[i+1];
                    i++;
                    break;
                case "-fkfile":
                    this.fkfile = args[i+1];
                    i++;
                    this.fileFkFile = new File(this.fkfile);
                    break;
                
            }
        }
    }
    
}
