package Main;

import ContextAnalyser.ContextAnalyser;
import ContextAnalyser.TransformationType;
import EasySQL.ForeignKey;
import EasySQL.SQLQuery;
import Transformation.DBTransformation;
import Transformation.EmptyTransformation;
import Transformation.ImpossibleTransformation;
import Transformation.Transformation;
import Transformation.TransformationTarget;
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
public class Main {
    private String dbhost;
    private String dbport;
    private String dbpw;
    private String dblogin; 
    private String fkfile;
    private File fileFkFile;
    private String dbName;
    private ArrayList<ForeignKey> fkArray;
    private ArrayList<Transformation> transformations;
    private HashMap<Transformation,Action> actionChoice;
    
    public Main(){
        fkArray = new ArrayList();
        transformations = new ArrayList<Transformation>();
        actionChoice = new HashMap<Transformation, Action>();
    }
    
    public Main(String[] userParameter){
        this.argsParseur(userParameter);
        fkArray = new ArrayList();
        transformations = new ArrayList<Transformation>();
        actionChoice = new HashMap<Transformation, Action>();
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
        printSmilet();
        System.out.println("");
        
        desableInfoLog();
        //args = "-dbhost localhost -dbname mydb -dbport 3306 -dblogin root -dbpw root -fkfile ./possible_matchesTest.txt".split(" ");
        args = "-dbhost localhost -dbname mydb -dbport 3306 -dblogin root -dbpw root -fkfile ./possible_matchesTest.txt".split(" ");
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
        System.out.println("3. Undo run" +((this.transformations.isEmpty())?"(Need to run first)":""));
        System.out.println("4. Print Analyse ");
        System.out.println("5. exit");
        System.out.println();
        
        int option = optionSelection(1,5);
        switch(option){
            case 1:
                argsMenu();
                break;
            case 2:
                if(this.fkArray.size()==0){
                    System.err.println("Impossible to proceed no foreign key found on file");
                    this.mainMenu();
                }else{
                    this.contextAnalyserMenu();
                }
                break;
            case 3:
                if(this.fkArray.isEmpty()){
                    System.err.println("Need to run first : opperation aborted");
                    this.mainMenu();
                }else{
                    for(int i=this.transformations.size()-1;i>=0;i--){
                        Transformation t=transformations.get(i);
                        if (t instanceof DBTransformation && actionChoice.get(t).equals(Action.Transform)){
                            try {
                                drawLine(25);
                                System.out.println("");
                                System.out.println("undoing fk : "+((DBTransformation) t).getFk());
                                ((DBTransformation) t).unDoTransformation();
                                System.out.println("[OK] transformation undo");
                                System.out.println("");
                            } catch (SQLException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                break;
            case 4:
                if(this.fkArray.size()==0){
                    System.err.println("Impossible to proceed no foreign key found on file");
                    this.mainMenu();
                }else{
                    printAnalyse();
                }
                break;
            case 5:
                System.out.println("Bye Bye :) ");
                System.exit(0);
                break;
                
        }
        

    }
    
    public void printAnalyse(){
        drawLine(100);
        System.out.println("Print Analyse");
        System.out.println();
        
        ContextAnalyser contextAnalyser = null;
        try{
            contextAnalyser = new ContextAnalyser(dbhost,dbName, dbport, dblogin, dbpw, fkArray);
            int i = 0;
            while(contextAnalyser.hasNext()){
                drawLine(25);
                System.out.println(this.fkArray.get(i));
                Transformation transfo = contextAnalyser.next();
                transformations.add(transfo);
                if (transfo instanceof DBTransformation){
                    PrintDBTransformationMenu((DBTransformation)transfo);
                }else if (transfo instanceof ImpossibleTransformation){
                    System.out.println(((ImpossibleTransformation) transfo).getMessage());
                    
                }else if (transfo instanceof EmptyTransformation){
                    System.out.println(((EmptyTransformation) transfo).getMessage());    
                }
                i++;
            }
            drawLine(25);
            System.out.println("");
            System.out.println("All foreign keys done :) ");
            contextAnalyser.getDicoTable().forEach((k,v)->System.out.println(v));
            this.mainMenu();
            
        }catch(EasySQL.Exception.DBConnexionErrorException e){
            System.err.println("DB connexion error some parameter can be wrong");
            this.mainMenu();
        }
        
        
    }
    
    public void contextAnalyserMenu(){
        drawLine(100);
        System.out.println("CONTEXT ANALYSER MENU");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        ContextAnalyser contextAnalyser = null;
        try{
            contextAnalyser = new ContextAnalyser(dbhost,dbName, dbport, dblogin, dbpw, fkArray);
            while(contextAnalyser.hasNext()){
                Transformation transfo = contextAnalyser.next();
                transformations.add(transfo);
                drawLine(25);
                if (transfo instanceof DBTransformation){
                    DBTransformationMenu((DBTransformation)transfo);
                }else if (transfo instanceof ImpossibleTransformation){
                    System.out.println(((ImpossibleTransformation) transfo).getMessage());
                    
                }else if (transfo instanceof EmptyTransformation){
                    System.out.println(((EmptyTransformation) transfo).getMessage());    
                }

                System.out.println("");
                System.out.println("Press a key to start with another fk");
                scanner.nextLine();
            }
            System.out.println("");
            System.out.println("All foreign keys done :) ");
            this.mainMenu();
            
        }catch(EasySQL.Exception.DBConnexionErrorException e){
            System.err.println("DB connexion error some parameter can be wrong");
            this.mainMenu();
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
    
    private void PrintDBTransformationMenu(DBTransformation dbtransfo) {
        boolean ok = true;
        boolean needCascadeTransfo=false;
        dbtransfo.analyse();     
        boolean isMBT= dbtransfo.getTransforamtiontype().equals(TransformationType.MBT);            
        
        System.out.println("Transformation type : " +dbtransfo.getTransforamtiontype().name());
        if(isMBT){
            System.out.println("    Just adding the foreignkey");
        }
        else if(dbtransfo.getTarget().equals(TransformationTarget.ForeignKeyTable)){
            System.out.println("    Transformation of " + dbtransfo.getFk().getForeingKeyTable()+"."+dbtransfo.getFk().getForeingKeyColumn() +" type to " + dbtransfo.getNewType());
        }else if(dbtransfo.getTarget().equals(TransformationTarget.ReferencedTable)){
            System.out.println("    Transformation of " + dbtransfo.getFk().getReferencedTableName()+"."+dbtransfo.getFk().getReferencedColumn()+" type to " + dbtransfo.getNewType());
        }
                    
        ok = dbtransfo.isEncodageMatching(); 
        System.out.println((dbtransfo.isEncodageMatching()?"[OK] Encodage matching":"[KO] Encodage mismatching"));
                    
        if(dbtransfo.getUnmatchingValue().size()==0){
            System.out.println("[OK] ALL table values matching");
        }else{
            System.out.println("[KO] Some table values unmatching :");
            ok=false;
            dbtransfo.getUnmatchingValue().forEach(s->System.out.println("    " + s));
        }
                    
        if(dbtransfo.getCascadeFk().size()==0 ){
            System.out.println("[OK] No Cascade Transformation");
            needCascadeTransfo=false;
        }else{
            needCascadeTransfo=true;
            System.out.println("[Warning] Existing Cascade Transformation on : ");
            dbtransfo.getCascadeFk().forEach(s->System.out.println("    "+s.getConstraintName()+" : "+s.getForeingKeyTable()+"."+s.getForeingKeyColumn() +" -> "+s.getReferencedTableName()+"."+s.getReferencedColumn()) );
        }
        
        if(ok){
            try {
                dbtransfo.getTransformationScript();
                System.out.println(System.lineSeparator()+"Transformation simulation done");
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void DBTransformationMenu(DBTransformation dbtransfo) {
        boolean ok = true;
        boolean needCascadeTransfo=false;
        dbtransfo.analyse();
        
        boolean isMBT= dbtransfo.getTransforamtiontype().equals(TransformationType.MBT) || dbtransfo.getTransforamtiontype().equals(TransformationType.MVMT) ;            
        
        System.out.println("Transformation type : " +dbtransfo.getTransforamtiontype().name());
        if(isMBT){
            System.out.println("    Juste adding the foreignkey");
        }
        else if(dbtransfo.getTarget().equals(TransformationTarget.ForeignKeyTable)){
            System.out.println("    Transformation of " + dbtransfo.getFk().getForeingKeyTable()+"."+dbtransfo.getFk().getForeingKeyColumn() +" type to " + dbtransfo.getNewType());
        }else if(dbtransfo.getTarget().equals(TransformationTarget.ReferencedTable)){
            System.out.println("    Transformation of " + dbtransfo.getFk().getReferencedTableName()+"."+dbtransfo.getFk().getReferencedColumn()+" type to " + dbtransfo.getNewType());
        }
                    
        ok = dbtransfo.isEncodageMatching(); 
        System.out.println((dbtransfo.isEncodageMatching()?"[OK] Encodage matching":"[KO] Encodage mismatching"));
                    
        if(dbtransfo.getUnmatchingValue().size()==0){
            System.out.println("[OK] ALL table values matching");
        }else{
            System.out.println("[KO] Some table values unmatching :");
            ok=false;
            dbtransfo.getUnmatchingValue().forEach(s->System.out.println("    " + s));
        }
                    
        if(dbtransfo.getCascadeFk().size()==0 ){
            System.out.println("[OK] No Cascade Transformation");
            needCascadeTransfo=false;
        }else{
            needCascadeTransfo=true;
            System.out.println("[Warning] Existing Cascade Transformation on : ");
            dbtransfo.getCascadeFk().forEach(s->System.out.println("    "+s.getConstraintName()+" : "+s.getForeingKeyTable()+"."+s.getForeingKeyColumn() +" -> "+s.getReferencedTableName()+"."+s.getReferencedColumn()) );
        }
        System.out.println("");    
        System.out.println("Option :");
        System.out.println("1. Run the modification" +
                ((ok)?"":" (Impossible to process check \"KO\" result)") +
                ((ok && needCascadeTransfo)?" (with cascade modification)":"")
        );
        System.out.println("2. Abort transformation");
        System.out.println("3. Add trigger to simulate foreign key");
        System.out.println("");
        
        boolean optionOk = false;
                
        while(!optionOk){
            int option = optionSelection(1, 3);
            Scanner sc = new Scanner(System.in);
            switch(option){
                case 1:
                    if(ok){
                        try {
                            optionOk=true;
                            dbtransfo.transfrom();
                            actionChoice.put(dbtransfo, Action.Transform);
                            System.out.println("[OK] Transformation done ");
                        } catch (SQLException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{
                        System.err.println("Impossible to process check \"KO\" result");
                    }
                    break;
                case 2:
                    actionChoice.put(dbtransfo, Action.Abort);
                    System.out.println("Operation aborted");
                    optionOk=true;
                    break;

                case 3 :
                    actionChoice.put(dbtransfo, Action.AddFK);
                    System.out.println("Not implemented yet");
                    optionOk=true;
                    break;
            }
        }
        
    }
    
    private static void printSmilet(){
        System.out.println(
"                               oooo$$$$$$$$$$$$oooo\n" +
"                           oo$$$$$$$$$$$$$$$$$$$$$$$$o\n" +
"                        oo$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$o         o$   $$ o$\n" +
"        o $ oo        o$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$o       $$ $$ $$o$\n" +
"     oo $ $ \"$      o$$$$$$$$$    $$$$$$$$$$$$$    $$$$$$$$$o       $$$o$$o$\n" +
"     \"$$$$$$o$     o$$$$$$$$$      $$$$$$$$$$$      $$$$$$$$$$o    $$$$$$$$\n" +
"       $$$$$$$    $$$$$$$$$$$      $$$$$$$$$$$      $$$$$$$$$$$$$$$$$$$$$$$\n" +
"       $$$$$$$$$$$$$$$$$$$$$$$    $$$$$$$$$$$$$    $$$$$$$$$$$$$$  \"\"\"$$$\n" +
"        \"$$$\"\"\"\"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     \"$$$\n" +
"         $$$   o$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     \"$$$o\n" +
"        o$$\"   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$       $$$o\n" +
"        $$$    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\" \"$$$$$$ooooo$$$$o\n" +
"       o$$$oooo$$$$$  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$   o$$$$$$$$$$$$$$$$$\n" +
"       $$$$$$$$\"$$$$   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     $$$$\"\"\"\"\"\"\"\"\n" +
"      \"\"\"\"       $$$$    \"$$$$$$$$$$$$$$$$$$$$$$$$$$$$\"      o$$$\n" +
"                 \"$$$o     \"\"\"$$$$$$$$$$$$$$$$$$\"$$\"         $$$\n" +
"                   $$$o          \"$$\"\"$$$$$$\"\"\"\"           o$$$\n" +
"                    $$$$o                                o$$$\"\n" +
"                     \"$$$$o      o$$$$$$o\"$$$$o        o$$$$\n" +
"                       \"$$$$$oo     \"\"$$$$o$$$$$o   o$$$$\"\"\n" +
"                          \"\"$$$$$oooo  \"$$$o$$$$$$$$$\"\"\"\n" +
"                             \"\"$$$$$$$oo $$$$$$$$$$\n" +
"                                     \"\"\"\"$$$$$$$$$$$\n" +
"                                         $$$$$$$$$$$$\n" +
"                                          $$$$$$$$$$\"\n" +
"                                           \"$$$\"\"  "
        );
    }
    
}
