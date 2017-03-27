/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pogrr_ftpamb;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author purkart
 */
public class FXMLDocumentController implements Initializable {
    public static String pathToFiles = "";
    public static TextField setHere;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        POGRR_ftpamb.loadSettings();
    }

    @FXML
    private void openSettings(){
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Nastavení FTP");
        dialog.setHeaderText("Zadejte prosím nastavení FTP");
        ButtonType addButtonType = new ButtonType("Nastav", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField adresaFTP = new TextField();
        adresaFTP.setPromptText("Adresa FTP");
        TextField usernameFTP = new TextField();
        usernameFTP.setPromptText("FTP Username");
        TextField passwordFTP = new TextField();
        passwordFTP.setPromptText("FTP Port");
        TextField portFTP = new TextField();
        portFTP.setPromptText("FTP Password");
        TextField pathToFiles = new TextField();
        pathToFiles.setPromptText("Cesta k souborům");
        grid.add(new Label("Adresa FTP:"), 0, 0);
        grid.add(adresaFTP, 1, 0);
        grid.add(new Label("FTP Username:"), 0, 1);
        grid.add(usernameFTP, 1, 1);
        grid.add(new Label("FTP Port:"), 0, 2);
        grid.add(passwordFTP, 1, 2);
        grid.add(new Label("FTP Password:"), 0, 3);
        grid.add(portFTP, 1, 3);
        grid.add(new Label("Cesta k souborům:"), 0, 4);
        grid.add(pathToFiles, 1, 4);
        grid.add(POGRR_ftpamb.chooser, 0, 5);
        adresaFTP.setText(POGRR_ftpamb.ftpAddress);
        usernameFTP.setText(POGRR_ftpamb.ftpUsername);
        passwordFTP.setText(POGRR_ftpamb.ftpPassword);
        portFTP.setText(POGRR_ftpamb.ftpPort);
        pathToFiles.setText(POGRR_ftpamb.ftpPathToFiles);
        setHere = pathToFiles;
        setHere.setPrefWidth(300);
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        adresaFTP.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> 
        {
            if (dialogButton == addButtonType) 
            {
                POGRR_ftpamb.ftpAddress = adresaFTP.getText();
                POGRR_ftpamb.ftpPort = portFTP.getText();
                POGRR_ftpamb.ftpPassword = passwordFTP.getText();
                POGRR_ftpamb.ftpPathToFiles = pathToFiles.getText();
                POGRR_ftpamb.ftpUsername = usernameFTP.getText();
                saveSettings();
                return true;
            }
            else
            {
                return false;
            }
        });
        Optional<Boolean> result = dialog.showAndWait();
    }
    
    private void saveSettings(){
        try
        {
            File iniFile = new File("settings.ini");
            if(!iniFile.exists())
            {
                iniFile.createNewFile();
            }
            PrintStream ps = new PrintStream(iniFile);
            ps.println(POGRR_ftpamb.ftpAddress);
            ps.println(POGRR_ftpamb.ftpUsername);
            ps.println(POGRR_ftpamb.ftpPassword);
            ps.println(POGRR_ftpamb.ftpPort);
            ps.println(POGRR_ftpamb.ftpPathToFiles);
        }
        catch(Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba při ukládání souboru");
            alert.setHeaderText("Nelze uložit nastavení");
            alert.setContentText("nelze uložit nastavení do INI souboru");
            alert.showAndWait();
        }
    }
    
    private void sendFile(String directory){
        String server = POGRR_ftpamb.ftpAddress;
        int port = Integer.parseInt(POGRR_ftpamb.ftpPort);
        String user = POGRR_ftpamb.ftpUsername;
        String pass = POGRR_ftpamb.ftpPassword;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File directoryRoot = new File(POGRR_ftpamb.ftpPathToFiles);
            File directoryFiles[] = directoryRoot.listFiles();
            int i = 0;
            while(i!=directoryFiles.length)
            {
                File testFile = directoryFiles[i];
                if(checkExtension(FilenameUtils.getExtension(testFile.getAbsolutePath())))
                {
                    String remoteFile = directory+"/"+testFile.getName();
                    InputStream inputStream = new FileInputStream(testFile);
                    boolean done = ftpClient.storeFile(remoteFile, inputStream);
                    inputStream.close();
                }
                testFile.delete();
                i++;
            }
        } 
        catch(IOException ex) 
        {
            
        } 
        finally 
        {
            try {
                if (ftpClient.isConnected()) 
                {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
    }
    
    private boolean checkExtension(String ext){
        System.out.println(ext);
        if(ext.equals("BOS") || ext.equals("HED") || ext.equals("BCD"))
        {
            return true;
        }
        return false;
    }
    
    @FXML
    private void kamlach(){
        sendFile("K");
    }
    
    @FXML
    private void Olejar(){
        sendFile("O");
    }
    
    @FXML
    private void Pojslova(){
        sendFile("P");
    }
}
