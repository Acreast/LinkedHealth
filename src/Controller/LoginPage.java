package Controller;


import Model.UserLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginPage {

    @FXML
    TextField txtUsername;
    @FXML
    PasswordField pwPassword;

    private Stage stage;
    private Scene scene;




    SQLDatabaseConnection databaseConnection = new SQLDatabaseConnection();



    public void loginUser(ActionEvent actionEvent) throws IOException {
        if(txtUsername.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Username empty");
            alert.setContentText("Please enter username");
            alert.showAndWait();
            return;
        }
        if(pwPassword.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Password empty");
            alert.setContentText("Please enter password");
            alert.showAndWait();
            return;
        }



        UserLogin newUser = databaseConnection.loginUser(txtUsername.getText(),pwPassword.getText());


        if (newUser != null){

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/HomePage.fxml"));
            HomePage newController = new HomePage(newUser);
            loader.setController(newController);
            Parent root = loader.load();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Success!");
            alert.setHeaderText(null);
            alert.setContentText("Logging in as : " + newUser.getName());
            alert.showAndWait();


            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login failed!");
            alert.setHeaderText(null);
            alert.setContentText("User does not exist or credentials mismatch");
            alert.showAndWait();

        }

    }



    public void registerUser(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../View/RegisterPage.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }

    public void emergencyQuery(ActionEvent actionEvent){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../View/EmergencyQuery.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
