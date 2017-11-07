/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prodacon2gui;

import ContextAnalyser.ContextAnalyser;
import ContextAnalyser.TransformationType;
import EasySQL.Column;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
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
    private ObservableList<String> cascadeTransformationObservableList = FXCollections.observableArrayList();
    @FXML private Label transfomrmationType;
    @FXML private Label transfomationSubtype;
    @FXML private Label fkInfoLable;
    @FXML private Label referenceInfoLable;
    @FXML private Label mainTarget;
    @FXML private Label newType;
    @FXML private Label encodageMatching;
    @FXML private TableView unmatchingValue;
    @FXML private TableColumn<String,String> unmatchingValueColumn;
    @FXML private TableView cascadeTable;
    @FXML private TableColumn<String,String> cascadeTableColumn;
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
//men fast Analyse
    @FXML private TextArea fastAnalyseTextArea;
    @FXML private Button fastAnalyseButton;
    @FXML private ChoiceBox choiceBoxTarget;
    private ObservableList choiceBoxTargetList= FXCollections.observableArrayList();
    @FXML private Label labelTransfoTableInfo;
    @FXML private HBox hBoxnewType;
    @FXML private ChoiceBox choiceBoxNexType;
    private ObservableList choiceBoxNexTypeList= FXCollections.observableArrayList();;
    @FXML private TextField textFieldNewTypeLength1;
    private TextField textFieldNewTypeLength2 = new TextField();
    @FXML private Label labelInfo;
    private static String[] INT_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT"};
    private static String[] NUMERIC_TYPES = {"TINYINT","SMALLINT","INT","MEDIUMINT","BIGINT","FLOAT","DOUBLE","DECIMAL"};
    private static String[] DECIMAL_NUMERIC_TYPES = {"FLOAT","DOUBLE","DECIMAL"};
    private static String[] ALPHA_NUMERIC_TYPES = {"ENUM","CHAR","VARCHAR","BLOB","TEXT","TINYBLOB","TINYTEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT"};
    private static String[] TIME_TYPES = {"TIME","YEAR","DATE","TIMESTAMP","DATETIME"};
    private static String[] TIME_TYPES_TRANSFORMABLE = {"TIMESTAMP","DATETIME"};
    private static String[] ONE_PARAMETER_TYPE={"YEAR","CHAR","VARCHAR"}; 
    private static String[] TWO_PARAMETER_TYPE={"FLOAT","DOUBLE","DECIMAL"};
    
    
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
        cascadeTableColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue()));
        unmatchingValueColumn.setCellValueFactory(s->new SimpleStringProperty(s.getValue().toString()));
        
        choiceBoxNexType.setItems(choiceBoxNexTypeList);
        choiceBoxNexTypeList.addAll(INT_TYPES);
        choiceBoxNexTypeList.add(new Separator());
        choiceBoxNexTypeList.addAll(DECIMAL_NUMERIC_TYPES);
        choiceBoxNexTypeList.add(new Separator());
        choiceBoxNexTypeList.addAll(ALPHA_NUMERIC_TYPES);
        choiceBoxNexTypeList.add(new Separator());
        choiceBoxNexTypeList.addAll(TIME_TYPES);
        choiceBoxNexType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(isIn((String)newValue, TWO_PARAMETER_TYPE)){
                    hboxNewType2parameterTransforamtion();
                }else{
                    hboxNewType1parameterTransforamtion();
                }
            }
        });
        
        
        choiceBoxTarget.setItems(choiceBoxTargetList);
        choiceBoxTargetList.addAll(TransformationTarget.values());
        choiceBoxTarget.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TransformationTarget newtarget =(TransformationTarget)newValue;
                currentDbTransformation.setTarget(newtarget);
                cascadeTransformationObservableList.clear();
                currentDbTransformation.getCascadeFk().forEach(
                        s->{
                            StringBuilder sb = new StringBuilder();
                            sb.append(s.getForeingKeyTable());
                            sb.append(".");
                            sb.append(s.getForeingKeyColumn());
                            if (!cascadeTransformationObservableList.contains(sb.toString())){
                               cascadeTransformationObservableList.add(sb.toString());
                            }
                            
                            sb = new StringBuilder();
                            sb.append(s.getReferencedTableName());
                            sb.append(".");
                            sb.append(s.getReferencedColumn());
                            if (!cascadeTransformationObservableList.contains(sb.toString())){
                               cascadeTransformationObservableList.add(sb.toString());
                            }
                            
                        }
                );
                if(newtarget.equals(TransformationTarget.ForeignKeyTable)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getForeingKeyTable()+"."+currentDbTransformation.getFk().getForeingKeyColumn());
                }else if(newtarget.equals(TransformationTarget.ReferencedTable)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getReferencedTableName()+"."+currentDbTransformation.getFk().getReferencedColumn());
                }else if(newtarget.equals(TransformationTarget.All)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getForeingKeyTable()+"."+currentDbTransformation.getFk().getForeingKeyColumn()+" & "+currentDbTransformation.getFk().getReferencedTableName()+"."+currentDbTransformation.getFk().getReferencedColumn());
                }
            }
        });
    }    
    
    @FXML
    private void buttonRestoreOnClick(){
        ObservableList l = hBoxnewType.getChildren();
        l.clear();
        l.add(choiceBoxNexType);
        l.add(new Label(" ( "));
        l.add(textFieldNewTypeLength1);
        l.add(new Label(" ) "));
    }
    
    private void hboxNewType2parameterTransforamtion(){
        ObservableList l = hBoxnewType.getChildren();
        l.clear();
        l.add(choiceBoxNexType);
        l.add(new Label(" ( "));
        l.add(textFieldNewTypeLength1);
        l.add(new Label(" , "));
        textFieldNewTypeLength2.setPrefWidth(50);
        l.add(textFieldNewTypeLength2);
        l.add(new Label(" ) "));
        
    }
    
    private void hboxNewType1parameterTransforamtion(){
        ObservableList l = hBoxnewType.getChildren();
        l.clear();
        l.add(choiceBoxNexType);
        l.add(new Label(" ( "));
        l.add(textFieldNewTypeLength1);
        l.add(new Label(" ) "));
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
        //System.out.println("SDBOC begin");
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
        //System.out.println("SDBOC end");
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
    
    private void Alert(AlertType type,String message,String message2){
        Alert alert = new Alert(type);
        alert.setTitle(type.name());      
        alert.setHeaderText(message);
        alert.setContentText(message2);
        alert.showAndWait();
    }
    
    
    @FXML
    private void addTriggerButtonOnClick(){
        actionChoice.put(currentDbTransformation, Action.AddFK);
        fkInfoObservableList.remove(0);
        Alert("Sorry","Net yet implemented");
    }
    
    @FXML
    private void abordButtonOnClick(){
        actionChoice.put(currentDbTransformation, Action.Abort);
        this.transInfoObservableList.add(0,this.currentDbTransformation);
        fkInfoObservableList.remove(0);
        tryNextTransformation();
    }
    
    @FXML
    private void executeTransformationButtonOnClick(){
        try {
            if(isIn((String)choiceBoxNexType.getValue(),TWO_PARAMETER_TYPE)){
                //Test if input is int               
                //System.out.println("textFieldNewTypeLength1.getText()=\""+textFieldNewTypeLength1.getText()+"\"");
                //System.out.println("textFieldNewTypeLength2.getText()=\""+textFieldNewTypeLength2.getText()+"\"");
                if(textFieldNewTypeLength1.getText()==null || textFieldNewTypeLength2.getText()==null){throw new NumberFormatException();}
                if(textFieldNewTypeLength1.getText().replace(" ", "").isEmpty()||textFieldNewTypeLength2.getText().replace(" ", "").isEmpty()){ throw new NumberFormatException();}
                Integer.parseInt(textFieldNewTypeLength1.getText());
                Integer.parseInt(textFieldNewTypeLength2.getText());
                currentDbTransformation.setNewType((String)choiceBoxNexType.getValue()+"("+textFieldNewTypeLength1.getText()+","+textFieldNewTypeLength2.getText()+")");
            }else{
                if(textFieldNewTypeLength1.getText()==null){throw new NumberFormatException();}
                if(textFieldNewTypeLength1.getText().replace(" ", "").isEmpty()){ throw new NumberFormatException();}
                Integer.parseInt(textFieldNewTypeLength1.getText());
                currentDbTransformation.setNewType((String)choiceBoxNexType.getValue()+"("+textFieldNewTypeLength1.getText()+")");
            }
            
            this.currentDbTransformation.transfrom();
            actionChoice.put(this.currentDbTransformation, Action.Transform);
            //System.out.println("add : " +this.currentDbTransformation.getFk().getConstraintName() +" on action choice " );
            this.transInfoObservableList.add(0,this.currentDbTransformation);
            fkInfoObservableList.remove(0);
            tryNextTransformation();
        }catch(NumberFormatException e){
            labelInfo.setTextFill(Color.RED);
            labelInfo.setText("transformation new type size parametter is not string ");
        } catch (SQLException ex) {
            Alert("Error during transformation",ex.getMessage());
        }
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
            
            ForeignKey f = dbtransfo.getFk();
            Column col =null;
            col = dbtransfo.getFkColumnBeforeTransformation();
            this.fkInfoLable.setText(f.getForeingKeyTable()+"."+col.getColumnName() +" : " +col.getColumnType() );
            
            col = dbtransfo.getRefColumnBeforeTransformation();
            this.referenceInfoLable.setText(f.getReferencedTableName()+"."+col.getColumnName() +" : " +col.getColumnType() );
            
            if(isMBT){
                //System.out.println("    Juste adding the foreignkey");
                this.mainTarget.setText("No main target we juste have to add the foreignkey constraint");
            }
            else if(dbtransfo.getTarget().equals(TransformationTarget.ForeignKeyTable)){
                this.mainTarget.setText(dbtransfo.getFk().getForeingKeyTable()+"."+dbtransfo.getFk().getForeingKeyColumn());               
            }else if(dbtransfo.getTarget().equals(TransformationTarget.ReferencedTable)){
                this.mainTarget.setText(dbtransfo.getFk().getReferencedTableName()+"."+dbtransfo.getFk().getReferencedColumn());               
            }
            
            this.newType.setText(dbtransfo.getNewType());
            //System.out.println("target : " + dbtransfo.getTarget());
            this.choiceBoxTarget.setValue(dbtransfo.getTarget());//attenton au effet de bord qui modifie aussi l'état de crrent transformation'
            TransformationTarget newtarget = dbtransfo.getTarget();
            if(newtarget.equals(TransformationTarget.ForeignKeyTable)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getForeingKeyTable()+"."+currentDbTransformation.getFk().getForeingKeyColumn());
            }else if(newtarget.equals(TransformationTarget.ReferencedTable)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getReferencedTableName()+"."+currentDbTransformation.getFk().getReferencedColumn());
            }else if(newtarget.equals(TransformationTarget.All)){
                    labelTransfoTableInfo.setText(currentDbTransformation.getFk().getForeingKeyTable()+"."+currentDbTransformation.getFk().getForeingKeyColumn()+" & "+currentDbTransformation.getFk().getReferencedTableName()+"."+currentDbTransformation.getFk().getReferencedColumn());
            }

            //this.labelTransfoTableInfo.setText(this.mainTarget.getText());

            this.choiceBoxNexType.setValue(typeParser(dbtransfo.getNewType())[0]);
            this.textFieldNewTypeLength1.setText(typeParser(dbtransfo.getNewType())[1]);
            this.textFieldNewTypeLength2.setText(typeParser(dbtransfo.getNewType())[2]);
            
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
                dbtransfo.getCascadeFk().forEach(
                        s->{
                            StringBuilder sb = new StringBuilder();
                            sb.append(s.getForeingKeyTable());
                            sb.append(".");
                            sb.append(s.getForeingKeyColumn());
                            if (!this.cascadeTransformationObservableList.contains(sb.toString())){
                               this.cascadeTransformationObservableList.add(sb.toString());
                            }
                            
                            sb = new StringBuilder();
                            sb.append(s.getReferencedTableName());
                            sb.append(".");
                            sb.append(s.getReferencedColumn());
                            if (!this.cascadeTransformationObservableList.contains(sb.toString())){
                               this.cascadeTransformationObservableList.add(sb.toString());
                            }
                            
                        }
                );
            }

            if(!ok){this.ExeButton.setDisable(true);}
            else{this.ExeButton.setDisable(false);}
    }
    
    private static String[] typeParser(String s){
        String[] out = new String[3];
        int i=0;
        for(String p : s.split("\\(|\\)|,")){
            out[i]=p.toUpperCase();
            i++;
        }
        return out;
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
                    currentDbTransformation=(DBTransformation)transfo;
                    DBTransformationAction((DBTransformation)transfo);
                }else if (transfo instanceof ImpossibleTransformation){
                    cleanAnalyseView();
                    this.transfomrmationType.setText("[ImpossibleTransformation] " +((ImpossibleTransformation) transfo).getMessage());
                    actionChoice.put(transfo, Action.Abort);
                    showNextbutton();
                }else if (transfo instanceof EmptyTransformation){
                    cleanAnalyseView();
                    this.transfomrmationType.setText("[EmptyTransformation] " +((EmptyTransformation) transfo).getMessage());
                    actionChoice.put(transfo, Action.Abort);
                    showNextbutton();
                }
            }catch(RuntimeException e){
                Alert("Error Load Unexistent Table Exception");
                actionChoice.put(currentDbTransformation, Action.Abort);
                fkInfoObservableList.remove(0);
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
        this.fkInfoLable.setText("");
        this.referenceInfoLable.setText("");
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
                    if (t != null && t instanceof DBTransformation && actionChoice.get(t).equals(Action.Transform)){
                        try {
                           ((DBTransformation) t).unDoTransformation();
                          } catch (SQLException ex) {
                              Alert("Error during undo",ex.getMessage());
                           }
                        }
                    }
        analyseButtonBox.getChildren().clear();
        startButton.setText("Start");
        analyseButtonBox.getChildren().add(startButton);
        Alert(AlertType.INFORMATION,"Undo done","");
    }
    
    private void drawLine(int length, TextArea textArea){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<length;i++){s.append("_");}
        addLine(textArea, s.toString());
    }
    
    
    private  void addLine(TextArea area,String line){
        fastAQueu.add(line+System.lineSeparator());
        signalUpdate();
        //area.setText(area.getText()+System.lineSeparator()+line);
        //Platform.runLater(()->area.setText(area.getText()+System.lineSeparator()+line));
    }
    
    private Service<Void> fastAnalyseService = new Service(){
        @Override
          protected Task<Void> createTask() {
              return new Task() {
                  @Override
                  protected Object call() throws Exception {
                      fastAnalyse();
                      return null;
                  }
              };
          }	
    };
    
    private ConcurrentLinkedQueue<String> fastAQueu = new ConcurrentLinkedQueue();
    private ReentrantLock lock = new ReentrantLock();
    private void signalUpdate(){
        Platform.runLater(new Runnable() {                  
            @Override
            public void run() {
               lock.lock();
               fastAnalyseTextArea.appendText(fastAQueu.poll());
               lock.unlock();
            }
        });
    }
    
    @FXML
    private void fastAnalyseButtonOnClick(){
        fastAnalyseTextArea.clear();
        //fastAnalyse();
        if (!fastAnalyseService.isRunning()){
            //System.out.println(fastAnalyseService);
            fastAnalyseService.restart();
        }
        System.out.println("end");
    }
    
    private void updatelatter(String message,TextArea area){
        Platform.runLater(new Runnable() {                  
            @Override
            public void run() {
                addLine(area, message);
            }
        });
    }
    /*@FXML
    private void fastAnalyse(){
        fastAnalyseTextArea.clear();
        drawLine(100,this.fastAnalyseTextArea);
        addLine(fastAnalyseTextArea,"Fast Analyse");
        addLine(fastAnalyseTextArea,"");
        
        ContextAnalyser contextAnalyser = null;
        try{
            contextAnalyser = new ContextAnalyser(
                    this.dbhostName.getText(),
                    this.dbName.getText(),
                    this.dbPort.getText(),
                    this.dbLogin.getText(),
                    this.dbPassWord.getText(),
                    new ArrayList(this.fkList)
            );
            int i = 0;
            while(contextAnalyser.hasNext()){
                Transformation transfo=null;
                try{
                    drawLine(25,fastAnalyseTextArea);
                    addLine(fastAnalyseTextArea,this.fkList.get(i).toString());
                    transfo = contextAnalyser.next();
                    transformations.add(transfo);
                    if (transfo instanceof DBTransformation){
                        PrintDBTransformationMenu((DBTransformation)transfo);
                    }else if (transfo instanceof ImpossibleTransformation){
                        addLine(fastAnalyseTextArea,((ImpossibleTransformation) transfo).getMessage());

                    }else if (transfo instanceof EmptyTransformation){
                        addLine(fastAnalyseTextArea,((EmptyTransformation) transfo).getMessage());    
                    }
                    i++;
                }catch(RuntimeException e){
                    addLine(fastAnalyseTextArea,"Error Load Unexistent Table Exception ");                 
                }
                
            }
            drawLine(25,fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"");
            addLine(fastAnalyseTextArea,"All foreign keys done :) "+System.lineSeparator());
            drawLine(100,this.fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"Table state :");
            drawLine(100,this.fastAnalyseTextArea);
            contextAnalyser.getDicoTable().forEach((k,v)->addLine(fastAnalyseTextArea,v.toString()));
            drawLine(100,this.fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"");
        }catch(EasySQL.Exception.DBConnexionErrorException e){
            Alert("DB connexion error","Some properties parameter can be wrong");
        }
        
    }*/
    
    @FXML
    private void fastAnalyse(){
        drawLine(100,this.fastAnalyseTextArea);
        addLine(fastAnalyseTextArea,"Fast Analyse");
        addLine(fastAnalyseTextArea,"");
        
        ContextAnalyser contextAnalyser = null;
        try{
            contextAnalyser = new ContextAnalyser(
                    this.dbhostName.getText(),
                    this.dbName.getText(),
                    this.dbPort.getText(),
                    this.dbLogin.getText(),
                    this.dbPassWord.getText(),
                    new ArrayList(this.fkList)
            );
            int i = 0;
            while(contextAnalyser.hasNext()){
                Transformation transfo=null;
                try{
                    drawLine(25,fastAnalyseTextArea);
                    addLine(fastAnalyseTextArea,this.fkList.get(i).toString());
                    transfo = contextAnalyser.next();
                    transformations.add(transfo);
                    if (transfo instanceof DBTransformation){
                        PrintDBTransformationMenu((DBTransformation)transfo);
                    }else if (transfo instanceof ImpossibleTransformation){
                        addLine(fastAnalyseTextArea,((ImpossibleTransformation) transfo).getMessage());

                    }else if (transfo instanceof EmptyTransformation){
                        addLine(fastAnalyseTextArea,((EmptyTransformation) transfo).getMessage());    
                    }
                    i++;
                }catch(RuntimeException e){
                    addLine(fastAnalyseTextArea,"Error Load Unexistent Table Exception ");                 
                }
                
            }
            drawLine(25,fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"");
            addLine(fastAnalyseTextArea,"All foreign keys done :) "+System.lineSeparator());
            drawLine(100,this.fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"Table state :");
            drawLine(100,this.fastAnalyseTextArea);
            contextAnalyser.getDicoTable().forEach((k,v)->addLine(fastAnalyseTextArea,v.toString()));
            drawLine(100,this.fastAnalyseTextArea);
            addLine(fastAnalyseTextArea,"");
        }catch(EasySQL.Exception.DBConnexionErrorException e){
            Alert("DB connexion error","Some properties parameter can be wrong");
        }
        
    }

    
    private void PrintDBTransformationMenu(DBTransformation dbtransfo) {
        boolean ok = true;
        boolean needCascadeTransfo=false;
        dbtransfo.analyse();     
        boolean isMBT= dbtransfo.getTransforamtiontype().equals(TransformationType.MBT);            
        
        addLine(fastAnalyseTextArea,"Transformation type : " +dbtransfo.getTransforamtiontype().name());
        if(isMBT){
            addLine(fastAnalyseTextArea,"    Just adding the foreignkey");
        }
        else if(dbtransfo.getTarget().equals(TransformationTarget.ForeignKeyTable)){
            addLine(fastAnalyseTextArea,"    Transformation of " + dbtransfo.getFk().getForeingKeyTable()+"."+dbtransfo.getFk().getForeingKeyColumn() +" type to " + dbtransfo.getNewType());
        }else if(dbtransfo.getTarget().equals(TransformationTarget.ReferencedTable)){
            addLine(fastAnalyseTextArea,"    Transformation of " + dbtransfo.getFk().getReferencedTableName()+"."+dbtransfo.getFk().getReferencedColumn()+" type to " + dbtransfo.getNewType());
        }
                    
        ok = dbtransfo.isEncodageMatching(); 
        addLine(fastAnalyseTextArea,(dbtransfo.isEncodageMatching()?"[OK] Encodage matching":"[KO] Encodage mismatching"));
                    
        if(dbtransfo.getUnmatchingValue().size()==0){
            addLine(fastAnalyseTextArea,"[OK] ALL table values matching");
        }else{
            addLine(fastAnalyseTextArea,"[KO] Some table values unmatching :");
            ok=false;
            dbtransfo.getUnmatchingValue().forEach(s->addLine(fastAnalyseTextArea,"    " + s));
        }
                    
        if(dbtransfo.getCascadeFk().size()==0 ){
            addLine(fastAnalyseTextArea,"[OK] No Cascade Transformation");
            needCascadeTransfo=false;
        }else{
            needCascadeTransfo=true;
            addLine(fastAnalyseTextArea,"[Warning] Existing Cascade Transformation on : ");
            dbtransfo.getCascadeFk().forEach(s->addLine(fastAnalyseTextArea,"    "+s.getConstraintName()+" : "+s.getForeingKeyTable()+"."+s.getForeingKeyColumn() +" -> "+s.getReferencedTableName()+"."+s.getReferencedColumn()) );
        }
        
        if(ok){
            try {
                dbtransfo.getTransformationScript();
                addLine(fastAnalyseTextArea,System.lineSeparator()+"Transformation simulation done");
            } catch (SQLException ex) {
                System.err.println("error during fast analyse : " + ex.getMessage());
            }
        }
    }
    
    private static boolean isIn(String s , String[] table){
            for(String e : table){
                if(s.toUpperCase().equals(e.toUpperCase())){return true;}
            }
        return false;
    };

}
