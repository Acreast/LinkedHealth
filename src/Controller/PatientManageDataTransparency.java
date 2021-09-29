package Controller;

import Model.Patient;
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
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientManageDataTransparency implements Initializable {

    @FXML
    RadioButton radHigh, radMedium, radLow,radCustom;
    @FXML
    Button btnBack, btnConfirmChanges, btnConfigure;

    private UserLogin loginSession;
    private Patient patient;
    private Stage stage;
    private Scene scene;

    public PatientManageDataTransparency(UserLogin loginSession, Patient patient) {
        this.loginSession = loginSession;
        this.patient = patient;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (patient.getPatientTransparency().equals("false,false,true,true,true,false,false,true,false")){
            radMedium.setSelected(true);
        } else if(patient.getPatientTransparency().equals("true,true,true,true,true,true,true,true,true")){
            radHigh.setSelected(true);
        } else if(patient.getPatientTransparency().equals("false,false,false,false,false,false,false,false,false")){
            radLow.setSelected(true);
        } else {
            radCustom.setSelected(true);
        }
        btnConfigure.setOnAction(this::toCustomizeTransparencyPage);
        btnConfirmChanges.setOnAction(this::updateTransparency);
        btnBack.setOnAction(this::backToViewProfile);
    }



    public void toCustomizeTransparencyPage(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientCustomTransparency.fxml"));
        PatientCustomTransparency pct = new PatientCustomTransparency(loginSession,patient);
        loader.setController(pct);
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

    public void updateTransparency(ActionEvent actionEvent){
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        Boolean updateAttempt = null;
        if (radHigh.isSelected()){
            updateAttempt = sc.updateTransparency(patient.getPatientID(),"true,true,true,true,true,true,true,true,true");
        } else if (radMedium.isSelected()){
            updateAttempt = sc.updateTransparency(patient.getPatientID(),"false,false,true,true,true,false,false,true,false");
        } else if (radLow.isSelected()){
            updateAttempt = sc.updateTransparency(patient.getPatientID(),"false,false,false,false,false,false,false,false,false");
        }
        if(updateAttempt != null && updateAttempt){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transparency settings updated!");
            alert.setHeaderText(null);
            alert.setContentText("Modification have been saved");
            alert.showAndWait();
        } else if(updateAttempt == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Changes are not made");
            alert.setHeaderText(null);
            alert.setContentText("Custom settings have to be performed at the custom configuration page," +
                    "Please click on \"Configure settings>>\" \nto modify ");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Transparency settings are not modified");
            alert.setHeaderText(null);
            alert.setContentText("Modification is not saved, a technical error has occured");
            alert.showAndWait();
        }
    }

    public void backToViewProfile(ActionEvent actionEvent){
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
