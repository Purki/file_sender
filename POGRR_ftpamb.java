/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pogrr_ftpamb;

import java.io.File;
import java.util.Scanner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author purkart
 */
public class POGRR_ftpamb extends Application {
    public static Button chooser;
    public static String ftpUsername = "";
    public static String ftpPort = "";
    public static String ftpPassword = "";
    public static String ftpPathToFiles = "";
    public static String ftpAddress = "";
            
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        chooser  = new Button();
        chooser.setText("Vyber cestu k souborům");
        chooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(stage);
                FXMLDocumentController.pathToFiles = selectedDirectory.getAbsolutePath();
                FXMLDocumentController.setHere.setText(selectedDirectory.getAbsolutePath());
            }
        });
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void loadSettings(){
        try
        {
            File iniFile = new File("settings.ini");
            if(iniFile.exists())
            {
                Scanner sc = new Scanner(iniFile);
                ftpAddress = sc.nextLine();
                ftpUsername = sc.nextLine();
                ftpPort = sc.nextLine();
                ftpPassword = sc.nextLine();
                ftpPathToFiles = sc.nextLine();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INI Soubor");
                alert.setHeaderText("První spuštění");
                alert.setContentText("prosím proveďtě nastavení aplikace");
                alert.showAndWait();
            }
        }
        catch(Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba!!!");
            alert.setHeaderText("Chyba načítání");
            alert.setContentText("Chyba při načítání nastavení! Kontaktujte správce...");
            alert.showAndWait();
        }
    }
    
}
