package prodagon2diagnostic;

import Analyse.Analyse;
import Diagnostic.Diagnostic;
import EasySQLight.ForeignKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

/**
 *
 * @author carl
 */
public class FXMLDocumentController implements Initializable {

//Class Variables -----------------------------------------------------------------------------------------------------------------------
    
    private Diagnostic currentDiagnostic;
    private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue();
    private ImageView gear=new ImageView("file:img/Gear.gif");
    private Button buttonSave = new Button("Save output on file");
    
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
        buttonSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                onClickFileChoise();
            }
        });
    }
    
// Button Actions -----------------------------------------------------------------------------------------------------------------------
    @FXML
    private void OnClickStart(){
        swithToWorkingIcon();
        boolean error=false;
        if (fieldsAreEmpty()){
            Alert("Some fields Are Empties", "");
            swithToStartButton();
        }else{
            // try to load FK file
            tAreaOutput.setText("");
            List<ForeignKey> listfk =null;
            try {
                
                listfk = loadFkFromFile(tFieldFKFile.getText());
            } catch (Exception ex) {
                Alert("Error during reading the \"Foreign Keys\" file (It may doesn't exist)"+System.lineSeparator() +ex, "");
                swithToStartButton();
                error=true;
            }
            
            //try tu connect to the DB and proceed the analyse 
            if(!error){
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
                            swithToStartButtonAndSaveButton();
                    }
                    );
                    t.start();
                }catch(RuntimeException ex){
                    Alert("Error during the DB connexion (some data can be wrong)"+System.lineSeparator() +ex, "");
                    swithToStartButton();
                }
            }
        }
    }
    
    @FXML
    private void onClickFileChoise(){
        PrintWriter writer = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.setInitialFileName("Proadacon2DiagnosticOutput.json");
            File file = fileChooser.showSaveDialog(null);
            writer = new PrintWriter(file, "UTF-8");
            writer.println(tAreaOutput.getText());
            writer.close();
            Alert(Alert.AlertType.CONFIRMATION, "Writing Succes", "");
        } catch (Exception ex) {
            Alert("Error during file Writing", "");
        } finally {
            writer.close();
        }
    }
    
    public void swithToWorkingIcon(){
        Platform.runLater( ()->{
            gear.setFitHeight(hBoxButton.getHeight());
            gear.setFitWidth(hBoxButton.getHeight());
            hBoxButton.getChildren().clear();
            hBoxButton.getChildren().add(gear);
        });
    }
    
    public void swithToStartButton(){
        Platform.runLater( ()->{
            buttonStart.setText("Restart Diagnostic");
            hBoxButton.getChildren().clear();
            hBoxButton.getChildren().add(buttonStart);
        });
    }
    
    public void swithToStartButtonAndSaveButton(){
        Platform.runLater( ()->{
            hBoxButton.getChildren().clear();
            buttonStart.setText("Restart Diagnostic");
            hBoxButton.getChildren().add(buttonStart);
            hBoxButton.getChildren().add(buttonSave);
        });
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
                || fieldsIsEmpty(tFieldDBName)
                || fieldsIsEmpty(tFieldFKFile)
                || fieldsIsEmpty(tFieldUserName)
                || fieldsIsEmpty(tFieldhost);
        return out;
    }
    
    private void Alert(String message,String message2){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR");      
        alert.setHeaderText("Error : " +message);
        alert.setContentText(message2);
        alert.showAndWait();
    }
    
    private void Alert(Alert.AlertType type,String message,String message2){
        Alert alert = new Alert(type);
        alert.setTitle(type.name());      
        alert.setHeaderText(message);
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
