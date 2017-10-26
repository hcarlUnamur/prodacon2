/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prodacon2gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import EasySQL.ForeignKey;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
/**
 *
 * @author carl_
 */
public class MainController implements Initializable {

//Menu Properties
    @FXML
    private Label label;    
    @FXML
    private ObservableList<ForeignKey>  kflist;   
    @FXML
    private TextField dbhostName;   
    @FXML
    private TextField dbName; 
    @FXML
    private TextField dbPort;   
    @FXML
    private TextField dbLogin;   
    @FXML
    private TextField dbPassWord;
    @FXML
    private Button buttonRestore;  
    @FXML
    private Button buttonUpdate ;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
