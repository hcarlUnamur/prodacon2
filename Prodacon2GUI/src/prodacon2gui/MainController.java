/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prodacon2gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import EasySQL.ForeignKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
/**
 *
 * @author carl_
 */
public class MainController implements Initializable {
//Global Data

//Menu Properties 
    @FXML private TextField dbhostName;   
    @FXML private TextField dbName; 
    @FXML private TextField dbPort;   
    @FXML private TextField dbLogin;   
    @FXML private TextField dbPassWord;

//Menu Foreign keys
    private ObservableList<ForeignKey> fkList = FXCollections.observableArrayList();
    @FXML private BorderPane fkTabMenu;
    @FXML private TableView fkTable;
    @FXML private TableColumn<ForeignKey, String> colRT;
    @FXML private TableColumn<ForeignKey, String> colRC;
    @FXML private TableColumn<ForeignKey, String> colFKT;
    @FXML private TableColumn<ForeignKey, String> colFKC;
    @FXML private TableColumn<ForeignKey, String> colCN;
    @FXML private Button buttonLoad;
    @FXML private TextField filePath; 
    
//Run Transformation Menu
    private ObservableList<String> unmatchingValueObservableList = FXCollections.observableArrayList();
    private ObservableList<ForeignKey> cascadeTransformationObservableList = FXCollections.observableArrayList();
    @FXML private Label transfomrmationType;
    @FXML private Label transfomationSubtype;
    @FXML private Label mainTarget;
    @FXML private Label newType;
    @FXML private Label encodageMatching;
    @FXML private TableView unmatchingValue;
    @FXML private TableColumn<String,String> unmatchingValueColumn;
    @FXML private TableView cascadeTable;
    @FXML private TableColumn<ForeignKey,String> cascadeTableColumn;
    @FXML private TableColumn<ForeignKey,String> cascadeColumnColumn;
    @FXML private HBox analyseButtonBox;
    @FXML private Button addTriggerButton;
    @FXML private Button abordeButton;
    @FXML private Button ExeButton;
     private Button startButton = new Button("Start");
            
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
        fkTable.setItems(fkList);
        colCN.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getConstraintName()));
        colFKC.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getForeingKeyColumn()));
        colFKT.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getForeingKeyTable()));
        colRC.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getReferencedColumn()));
        colRT.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getReferencedTableName()));
        
    //Run Transformation        
        analyseButtonBox.getChildren().clear();
        startButton.setMinWidth(100);
        analyseButtonBox.getChildren().add(startButton);
        /*
        addTriggerButton.setVisible(false);
        abordeButton.setVisible(false);
        ExeButton.setVisible(false);
        */
        cascadeTable.setItems(cascadeTransformationObservableList);
        unmatchingValue.setItems(unmatchingValueObservableList);
        cascadeTableColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getForeingKeyTable()));
        cascadeColumnColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getForeingKeyColumn()));
        unmatchingValueColumn.setCellValueFactory(s->new SimpleStringProperty(s.getValue().toString()));
    }    
    
    @FXML
    private void buttonRestoreOnClick(){
    
    }
    
    
    @FXML
    private void buttonLoadOnClick(){
        BufferedReader reader = null;
        File fileFKFile = new File(this.filePath.getText());
                
        try{
            reader = new BufferedReader((new FileReader(fileFKFile)));
            String text = null;
            int i=0;
            while ((text = reader.readLine()) != null) {               
                String[] tab = text.split(":|,");
                ForeignKey fk = new ForeignKey(
                        tab[2],
                        tab[3],
                        tab[1],
                        tab[0],
                        ("fk_"+tab[0].substring(0,((tab[0].length()<=10)?tab[0].length():10))+"_"+tab[2].substring(0,((tab[2].length()<=10)?tab[2].length():10))+"_"+i));
                this.fkList.add(fk);
                i++;
            }
        }catch(IOException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("ERROR");
            
            alert.setHeaderText("Error : " + (fileFKFile.exists()?"During loading":"file not found"));
            alert.showAndWait();
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {}

    }
    
    @FXML
    private void RemoveAllButtonOnClick(){
        fkList.clear();
    }
    
    @FXML
    private void ResetDefaultButtonOnClick(){
        Properties prop = new Properties();
	InputStream input = null;
	try {
		input = new FileInputStream("./Prodacon2DB.properties");

		// load a properties file
		prop.load(input);

		// get the property value
		this.dbLogin.setText(prop.getProperty("DB_Login"));
                this.dbName.setText(prop.getProperty("DB_Name"));
                this.dbPassWord.setText(prop.getProperty("DB_Password"));
                this.dbPort.setText(prop.getProperty("DB_Port"));
                this.dbhostName.setText(prop.getProperty("DB_Host"));
	}catch (IOException ex){
            Alert("IOException " +ex);
	} finally {
            if (input != null) {
		try {
			input.close();
		} catch (IOException e) {
                    Alert("IOExeption during closing file");
		}
            }
	}
    }
    
    @FXML
    private void SaveDefaultButtonOnClick(){
        System.out.println("SDBOC begin");
        Properties prop = new Properties();
	OutputStream output = null;
	try {
		output = new FileOutputStream("./Prodacon2DB.properties");


		// Set the property value
		prop.setProperty("DB_Login",this.dbLogin.getText());
                prop.setProperty("DB_Name",this.dbName.getText());
                prop.setProperty("DB_Password",this.dbPassWord.getText());
                prop.setProperty("DB_Port",this.dbPort.getText());
                prop.setProperty("DB_Host",this.dbhostName.getText());
                
                // save properties to project root folder
                prop.store(output, null);
	}catch (IOException ex){
            Alert("IOException " +ex);
	} finally {
            if (output != null) {
		try {
			output.close();
		} catch (IOException e) {
                    Alert("IOExeption during closing file");
		}
            }
	}
        System.out.println("SDBOC end");
    }
    
    private void Alert(String message){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("ERROR");      
        alert.setHeaderText("Error : " +message);
        alert.showAndWait();
    }
    
    private void Alert(String message,String message2){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("ERROR");      
        alert.setHeaderText("Error : " +message);
        alert.setContentText(message2);
        alert.showAndWait();
    }
    
    
    @FXML
    private void addTriggerButtonOnClick(){
    
    }
    
    @FXML
    private void abordButtonOnClick(){
    
    }
    
    @FXML
    private void executeTransformationButtonOnClick(){
    
    }
}
