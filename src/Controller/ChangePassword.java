package Controller;

import Model.UserLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangePassword implements Initializable {

    @FXML
    private PasswordField pwOldPassword,pwNewPassword, pwReenterPassword;
    @FXML
    private Button btnChangePassword,btnBack;

    private String lastPage;
    private UserLogin loginSession;
    private Stage stage;
    private Scene scene;

    public ChangePassword(UserLogin loginSession,String lastPage) {
        this.loginSession = loginSession;
        this.lastPage =lastPage;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnChangePassword.setOnAction(this::updateAction);
        btnBack.setOnAction(this::backAction);
    }

    public void updateAction(ActionEvent actionEvent){
        if(pwNewPassword.getText().length()<11){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password length too short");
            alert.setContentText("Please make sure that the password is more than 12 characters");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        if(pwNewPassword.getText().equals(pwReenterPassword.getText())){
            SQLDatabaseConnection sc = new SQLDatabaseConnection();
            sc.changePassword(loginSession.getUserID(),pwOldPassword.getText(),pwNewPassword.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password mismatch");
            alert.setContentText("New password does not match with re-enter password");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }
    }

    public void backAction(ActionEvent actionEvent){
        if(lastPage.equals("GeneralProfile")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/GeneralProfile.fxml"));
            GeneralProfile gp = new GeneralProfile(loginSession);
            loader.setController(gp);
            try {
                Parent root = loader.load();
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientViewProfile.fxml"));
            PatientViewProfile pvp = new PatientViewProfile(loginSession);
            loader.setController(pvp);
            try {
                Parent root = loader.load();
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
}
