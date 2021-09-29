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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientViewProfile implements Initializable {
    @FXML
    private Label lblPatientID,lblTransparencyLevel;

    @FXML
    private Button btnManageTransparency,btnChangePassword,btnUpdate,btnBack;

    @FXML
    private TextField txtName,txtDOB,txtGender,txtMartialStatus,txtContact,txtEmail,txtAddress,
            txtBloodType,txtMainInsurance,txtWeight,txtHeight;

    @FXML
    private ListView<String> lstAllergies;

    private UserLogin loginSession;
    private Patient patient;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;

    public PatientViewProfile(UserLogin user){
        this.loginSession = user;
        sc = new SQLDatabaseConnection();
        this.patient = sc.getPatientByUserID(loginSession.getUserID());
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblPatientID.setText(patient.getPatientID());
        txtName.setText(loginSession.getName());
        txtDOB.setText(loginSession.getDOB());
        btnUpdate.setOnAction(this::toEditProfilePage);
        btnBack.setOnAction(this::backAction);
        btnManageTransparency.setOnAction(this::toManageTransparencyPage);
        btnChangePassword.setOnAction(this::toChangePasswordPage);

        if(loginSession.getGender().equals("M")){
            txtGender.setText("Male");
        } else {
            txtGender.setText("Female");
        }
        if (patient.getMartialStatus() == null || patient.getMartialStatus().equals("")){
            txtMartialStatus.setText("Not set yet");
        } else {
            txtMartialStatus.setText(patient.getMartialStatus());
        }
        if(loginSession.getUserContactNumber().length() > 1){
            txtContact.setText(loginSession.getUserContactNumber());
        }
        if(loginSession.getUserEmail().length()>1){
            txtEmail.setText(loginSession.getUserEmail());
        }
        if(loginSession.getUserAddress().length()>1){
            txtAddress.setText(loginSession.getUserAddress());
        }
        if(patient.getBloodType() == null || patient.getBloodType().equals("")){
            txtBloodType.setText("Not set yet");
        } else{
            txtBloodType.setText(patient.getBloodType());
        }
        if(patient.getWeight() == 0.0f){
            txtWeight.setText("Not set yet");
        } else{
            txtWeight.setText(patient.getWeight().toString());
        }
        if(patient.getHeight() == 0.0f){
            txtHeight.setText("Not set yet");
        } else{
            txtHeight.setText(patient.getHeight().toString());
        }
        if(patient.getMainInsurance() == null || patient.getMainInsurance().equals("")){
            txtMainInsurance.setText("Not set yet");
        } else{
            txtMainInsurance.setText(patient.getMainInsurance());
        }

    }


    public void toEditProfilePage(ActionEvent actionEvent){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientEditProfile.fxml"));
        PatientEditProfile pep = new PatientEditProfile(loginSession,patient);
        loader.setController(pep);
        try {
            Parent root = loader.load();
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void toChangePasswordPage(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ChangePassword.fxml"));
        ChangePassword cp = new ChangePassword(loginSession,"");
        loader.setController(cp);
        Parent root = null;
        try {
            root = loader.load();
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toManageTransparencyPage(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientManageDataTransparency.fxml"));
        PatientManageDataTransparency pmdt = new PatientManageDataTransparency(loginSession,patient);
        loader.setController(pmdt);
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

    public void backAction(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/HomePage.fxml"));
        HomePage newController = new HomePage(loginSession);
        loader.setController(newController);
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
