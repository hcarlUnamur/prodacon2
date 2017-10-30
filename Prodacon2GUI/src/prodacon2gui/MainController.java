/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prodacon2gui;

import ContextAnalyser.ContextAnalyser;
import ContextAnalyser.TransformationType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import EasySQL.ForeignKey;
import Main.Action;
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
import java.util.ArrayList;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import Transformation.*;
import java.sql.SQLException;
import java.util.HashMap;
import EasySQL.Exception.LoadUnexistentTableException;
/**
 *
 * @author carl_
 */
public class MainController implements Initializable {
//Global Data
    private ContextAnalyser contextAnalyser;
    private ArrayList<Transformation> transformations = new ArrayList();
    private HashMap<Transformation,Action> actionChoice = new HashMap();
    private DBTransformation currentDbTransformation;
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
    private Button nextbutton = new Button("Next");
    private Button undoButton = new Button("undo All");
    @FXML private TableView fkInfo;
    @FXML private TableColumn<ForeignKey,String> fkInfoCol1;
    @FXML private TableView transInfo;
    @FXML private TableColumn<DBTransformation,String> transCol1;
    @FXML private TableColumn<DBTransformation,String> transCol2;
    private ObservableList<ForeignKey> fkInfoObservableList = FXCollections.observableArrayList();
    private ObservableList<Transformation> transInfoObservableList = FXCollections.observableArrayList();
     
            
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
        ResetDefaultButtonOnClick();
        fkTable.setItems(fkList);
        colCN.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getConstraintName()));
        colFKC.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getForeingKeyColumn()));
        colFKT.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getForeingKeyTable()));
        colRC.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getReferencedColumn()));
        colRT.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getReferencedTableName()));
        
    //Run Transformation        
        fkInfo.setItems(fkInfoObservableList);
        transInfo.setItems(transInfoObservableList);
        fkInfoCol1.setCellValueFactory(fk-> new SimpleStringProperty(fk.getValue().getConstraintName()) );
        transCol1.setCellValueFactory(trans ->new SimpleStringProperty(trans.getValue().getTransforamtiontype().name()));
        transCol2.setCellValueFactory(trans ->  new SimpleStringProperty(this.actionChoice.get(trans.getValue()).name()));
        analyseButtonBox.getChildren().clear();
        startButton.setMinWidth(100);
        analyseButtonBox.getChildren().add(startButton);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startButtonOnclickAction();
            }
        });
        
        nextbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nextButtonOnclickAction();
            }
        });
        
        undoButton.setMinWidth(100);
        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UndoButtonOnclickAction();
            }
        });
        
        cascadeTable.setItems(cascadeTransformationObservableList);
        unmatchingValue.setItems(unmatchingValueObservableList);
        cascadeTableColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getForeingKeyTable()+"."+cellData.getValue().getForeingKeyColumn()));
        cascadeColumnColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getReferencedTableName()+"."+cellData.getValue().getReferencedColumn()));
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
        fkInfoObservableList.remove(0);
        Alert("Sorry","Net yet implemented");
    }
    
    @FXML
    private void abordButtonOnClick(){
        fkInfoObservableList.remove(0);
        tryNextTransformation();
    }
    
    @FXML
    private void executeTransformationButtonOnClick(){
        try {
            this.currentDbTransformation.transfrom();
            actionChoice.put(this.currentDbTransformation, Action.Transform);
            this.transInfoObservableList.add(0,this.currentDbTransformation);
            fkInfoObservableList.remove(0);
        } catch (SQLException ex) {
            Alert("Error during transformation",ex.getMessage());
        }
        tryNextTransformation();
    }
    
    private void startButtonOnclickAction() {
        try{
            contextAnalyser = new ContextAnalyser(
                    this.dbhostName.getText(),
                    this.dbName.getText(),
                    this.dbPort.getText(),
                    this.dbLogin.getText(),
                    this.dbPassWord.getText(),
                    new ArrayList(this.fkList)
            );

            //clear
            transformations.clear();
            actionChoice.clear();
            //currentDbTransformation=null;
            transInfoObservableList.clear();
            //end clear
            
            showAnalysebutton();
            fkList.forEach(fk -> fkInfoObservableList.add(fk));
            tryNextTransformation();
        }catch(EasySQL.Exception.DBConnexionErrorException e){
            Alert("DB connexion error","Some properties parameter can be wrong");
        }
        
        
    }

    private void showNextbutton(){
        this.analyseButtonBox.getChildren().clear();
        this.analyseButtonBox.getChildren().add(this.nextbutton);
    }
    
    private void showAnalysebutton(){
        this.analyseButtonBox.getChildren().clear();
        this.analyseButtonBox.getChildren().add(this.addTriggerButton);
        this.analyseButtonBox.getChildren().add(this.abordeButton);
        this.analyseButtonBox.getChildren().add(this.ExeButton);    
    }
    
    private void DBTransformationAction(DBTransformation dbtransfo) {
            this.currentDbTransformation = dbtransfo;
            boolean ok = true;
            boolean needCascadeTransfo=false;
            dbtransfo.analyse();

            boolean isMBT= dbtransfo.getTransforamtiontype().equals(TransformationType.MBT) || dbtransfo.getTransforamtiontype().equals(TransformationType.MVMT) ;            
            this.transfomrmationType.setText("Transformation");
            this.transfomationSubtype.setText(dbtransfo.getTransforamtiontype().name());
            if(isMBT){
                //System.out.println("    Juste adding the foreignkey");
                this.mainTarget.setText("No main target we juste have to add the foreignkey constraint");
            }
            else if(dbtransfo.getTarget().equals(TransformationTarget.ForeignKeyTable)){
                this.mainTarget.setText(dbtransfo.getFk().getForeingKeyTable()+"."+dbtransfo.getFk().getForeingKeyColumn());
                this.newType.setText(dbtransfo.getNewType());
            }else if(dbtransfo.getTarget().equals(TransformationTarget.ReferencedTable)){
                this.mainTarget.setText(dbtransfo.getFk().getReferencedTableName()+"."+dbtransfo.getFk().getReferencedColumn());
                this.newType.setText(dbtransfo.getNewType());
            }

            ok = dbtransfo.isEncodageMatching(); 
            this.encodageMatching.setText((dbtransfo.isEncodageMatching()?"[OK] Encodage matching":"[KO] Encodage mismatching"));

            if(dbtransfo.getUnmatchingValue().size()==0){
                //System.out.println("[OK] ALL table values matching");
            }else{
                //System.out.println("[KO] Some table values unmatching :");
                ok=false;
                dbtransfo.getUnmatchingValue().forEach(s->this.unmatchingValueObservableList.add(s));
            }

            if(dbtransfo.getCascadeFk().size()==0 ){
                //System.out.println("[OK] No Cascade Transformation");
                needCascadeTransfo=false;
            }else if (!(dbtransfo.getTransforamtiontype().equals(TransformationType.MBT) || dbtransfo.getTransforamtiontype().equals(TransformationType.MVMT))) {
                needCascadeTransfo=true;
                //System.out.println("[Warning] Existing Cascade Transformation on : ");
                dbtransfo.getCascadeFk().forEach(s->this.cascadeTransformationObservableList.add(s));
            }

            if(!ok){this.ExeButton.setDisable(true);}
            else{this.ExeButton.setDisable(false);}
    }
    
    private void tryNextTransformation(){
            cleanAnalyseView();
            Transformation transfo = null;
            if(contextAnalyser.hasNext()){
            try{    
                transfo = contextAnalyser.next();
                transformations.add(transfo);
                if (transfo instanceof DBTransformation){
                    showAnalysebutton();
                    DBTransformationAction((DBTransformation)transfo);
                }else if (transfo instanceof ImpossibleTransformation){
                    this.transfomrmationType.setText("[ImpossibleTransformation] " +((ImpossibleTransformation) transfo).getMessage());
                    actionChoice.put(transfo, Action.Abort);
                    showNextbutton();
                }else if (transfo instanceof EmptyTransformation){
                    this.transfomrmationType.setText("[EmptyTransformation] " +((EmptyTransformation) transfo).getMessage());
                    actionChoice.put(transfo, Action.Abort);
                    showNextbutton();
                }
            }catch(RuntimeException e){
                Alert("Error Load Unexistent Table Exception");
                tryNextTransformation();
            }
        }else{
            cleanAnalyseView();
            analyseButtonBox.getChildren().clear();
            startButton.setText("Restart");           
            analyseButtonBox.getChildren().add(startButton);
            analyseButtonBox.getChildren().add(undoButton);
                    
        }
    }

    private void cleanAnalyseView(){
        this.transfomrmationType.setText("");
        this.transfomationSubtype.setText("");
        this.mainTarget.setText("");
        this.newType.setText("");
        this.encodageMatching.setText("");
        this.unmatchingValueObservableList.clear();
        this.cascadeTransformationObservableList.clear();
    }
    
    private void nextButtonOnclickAction(){
        fkInfoObservableList.remove(0);
        tryNextTransformation();
    }
    
    
    private void UndoButtonOnclickAction() {
        for(int i=this.transformations.size()-1;i>=0;i--){
                    //System.out.println("");
                    Transformation t=transformations.get(i);
                    if(t instanceof DBTransformation ){
                        System.out.println(((DBTransformation) t).getFk().getConstraintName());
                        System.out.println(actionChoice.get(t));
                    }
                    if (t != null && t instanceof DBTransformation && actionChoice.get(t).equals(Action.Transform)){
                        try {
                           ((DBTransformation) t).unDoTransformation();
                          } catch (SQLException ex) {
                              Alert("Error during undo",ex.getMessage());
                           }
                        }
                    }
        Alert("Undo done");
    }
}
