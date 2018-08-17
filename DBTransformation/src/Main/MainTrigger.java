package Main;

import EasySQL.ForeignKey;
import EasySQL.SQLCreateTriggerQuery;
import EasySQL.SQLQueryFactory;
import EasySQL.SQLQueryFree;
import EasySQL.SQLQueryType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author carl_
 */
public class MainTrigger {
    private String dbhost;
    private String dbport;
    private String dbpw;
    private String dblogin; 
    private String fkfile;
    private File fileFkFile;
    private String dbName;
    private ArrayList<ForeignKey> fkArray;
    
    public MainTrigger(){
        fkArray = new ArrayList();
    }
    
    public MainTrigger(String[] userParameter){
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
    
    private static void desableInfoLog(){
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.SEVERE);
        }
    }
    
    public static void main(String[] args) {
        drawLine(100);
        System.out.println("");
        System.out.println("");
        
        desableInfoLog();
        args = "-dbhost localhost -dbname mydb -dbport 3306 -dblogin root -dbpw root -fkfile ./possible_matchesTest.txt".split(" ");
        MainTrigger main = new MainTrigger(args);
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
        System.out.println("2. Add Trigger Log Table");
        System.out.println("3. Add Trigger Loggers " + ((this.fkArray.size()==0)?"( Impossible action no foreign key found on file)":""));
        System.out.println("4. exit");
        System.out.println();
        
        int option = optionSelection(1,4);
        switch(option){
            case 1:
                argsMenu();
                break;
             
            case 2:
                this.CreateLogTable();
                this.mainMenu();
                break;
                
            case 3:
                if(this.fkArray.size()==0){
                    System.err.println("Impossible to proceed no foreign key found on file");
                    this.mainMenu();
                }else{
                    this.WriteTrigger();
                    this.mainMenu();
                }
                break;
            case 4:
                System.out.println("Bye Bye :) ");
                System.exit(0);
                break;
                
        }
        

    }
    
    public void CreateLogTable(){
        try{
            SQLQueryFactory sqlF = new SQLQueryFactory(this.dbhost, this.dbName, this.dbport, this.dblogin, this.dbpw);
            SQLQueryFree free = sqlF.createSQLCreateFreeQuery(SQLQueryType.Updater, "create table IF NOT EXISTS tableTriggerLog (\n" +
                                                                                    "    foreignKeyName varchar(100) NOT NULL,\n" +
                                                                                    "    foreignKeyTable varchar(100) NOT NULL,\n" +
                                                                                    "    foreignKeyColumn varchar(100) NOT NULL,\n" +
                                                                                    "    referencedTable varchar(100) NOT NULL,\n" +
                                                                                    "    referencedColumn varchar(100) NOT NULL,\n" +
                                                                                    "    problemAction varchar(40) NOT NULL,\n" +
                                                                                    "    dateAndTime timeStamp NOT NULL DEFAULT now()\n" +
                                                                                    ");");
            free.sqlQueryDo();
        }catch(Exception ex){
            System.err.println("Problem during the adding of the logging table");
            System.err.println(ex);
        }
    }
    
    public void WriteTrigger(){
        try{
        SQLQueryFactory sqlF = new SQLQueryFactory(this.dbhost, this.dbName, this.dbport, this.dblogin, this.dbpw);
        for(ForeignKey fk : fkArray){
            SQLCreateTriggerQuery tri = sqlF.createSQLcreateTriggerQuery(fk, "tableTriggerLog");
            tri.sqlQueryDo();
            System.out.println("trigger logger of " + fk.getConstraintName() + " added");
        }
        }catch(SQLException sqlEx){
            System.err.println("Problem during the adding of the trigger loggers");
            System.err.println(sqlEx);
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
        System.out.println((fileFkFile.exists() ? "" : "    Warning : File don't exist"));
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
                this.fkArray=new ArrayList();
                System.out.print("new fk file value : ");
                this.fkfile = sc.nextLine();
                this.fileFkFile=new File(this.fkfile);        
                try {
                    parseFKFile();
                    System.out.println("Loading and parsing file [Success]");
                } catch (Exception ex) {
                    System.err.println("Error impossible to load and/or parse [Fails] : " + this.fkfile);
                }
                this.argsMenu();
                break;
            case 8 :
                this.fkArray.forEach(System.out::println);
                System.out.println("");
                System.out.print("Press a key to come back to ARGUMENTS MENU ");
                sc.nextLine();
                this.argsMenu();
                break;
        }
        
    }
    
    public int optionSelection(int min, int max){
        Scanner sc = new Scanner(System.in);
        int out =0;
        try{
            System.out.print("Select an option : ");
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
                this.fkArray.add(new ForeignKey(tab[2], tab[3], tab[1], tab[0],("fk_Constraint_"+tab[0]+"_"+tab[2]+"_"+i) ));
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

