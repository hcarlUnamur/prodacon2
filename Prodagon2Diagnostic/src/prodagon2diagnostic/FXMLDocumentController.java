package prodagon2diagnostic;

import Analyse.Analyse;
import Diagnostic.Diagnostic;
import EasySQLight.ForeignKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author carl
 */
public class FXMLDocumentController implements Initializable {

//Class Variables -----------------------------------------------------------------------------------------------------------------------
    
    private Diagnostic currentDiagnostic;
    private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue();
    
   
    
    @FXML
    private TextField tFieldhost;
    @FXML
    private TextField tFielPort;
    @FXML
    private TextField tFieldUserName;
    @FXML
    private PasswordField tFieldPassword;
    @FXML
    private TextField tFieldDBName;
    @FXML
    private TextField tFieldFKFile;
    @FXML
    private TextArea tAreaOutput;
    @FXML
    private Button buttonStart;
    @FXML
    private HBox hBoxButton;
    
    
//Class Methods -----------------------------------------------------------------------------------------------------------------------
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
// Button Actions -----------------------------------------------------------------------------------------------------------------------
    @FXML
    private void OnClickStart(){
        if (fieldsAreEmpty()){
            Alert("Some fields Are Empties", "");
        }else{
            // try to load FK file
            tAreaOutput.setText("");
            List<ForeignKey> listfk =null;
            try {
                listfk = loadFkFromFile(tFieldFKFile.getText());
            } catch (Exception ex) {
                Alert("Error during reading the \"Foreign Keys\" file (It may doesn't exist)"+System.lineSeparator() +ex, "");
            }
            
            //try tu connect to the DB and proceed the analyse 
            try{
                currentDiagnostic = new Diagnostic(
                        tFieldhost.getText(),
                        tFieldDBName.getText(),
                        tFielPort.getText(),
                        tFieldUserName.getText(),
                        tFieldPassword.getText(),
                        listfk
                );
                Thread t = new Thread( () ->{
                        addOutputAndNotify("{ \"proadcon2Diagnostic\" : [ ");
                        while (currentDiagnostic.hasNext()){
                            Analyse analyse = currentDiagnostic.next();
                            analyse.analyse();
                            addOutputAndNotify(analyse.getJson()+",");
                        }
                        deleteLastComa();
                        addOutputAndNotify("] }");
                }
                );
                t.start();
            }catch(RuntimeException ex){
                Alert("Error during the DB connexion (some data can be wrong)"+System.lineSeparator() +ex, "");
            }
        }
    }

//Class Tools -----------------------------------------------------------------------------------------------------------------------

    private void addOutputAndNotify(String message){
        try {
            sharedQueue.put(message);
            notifyOutput();
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void notifyOutput(){
        Platform.runLater( ()->{
            try {
                tAreaOutput.appendText(sharedQueue.take()+System.lineSeparator());
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        );
    }
    
    private void deleteLastComa(){
        Platform.runLater( ()->{
            String output = tAreaOutput.getText();
            int lastind = output.lastIndexOf(",");
            if (lastind!=-1){
                tAreaOutput.deleteText(lastind, lastind+1);
            }
        });
    }
    
    private boolean fieldsIsEmpty(TextField field){
        return field.getText().replace(" ","").length()==0;
    }
 
    private boolean fieldsAreEmpty(){
        boolean out= fieldsIsEmpty(tFielPort)
                && fieldsIsEmpty(tFieldDBName)
                && fieldsIsEmpty(tFieldFKFile)
                && fieldsIsEmpty(tFieldUserName)
                && fieldsIsEmpty(tFieldhost);
        return out;
    }
    
    private void Alert(String message,String message2){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR");      
        alert.setHeaderText("Error : " +message);
        alert.setContentText(message2);
        alert.showAndWait();
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
