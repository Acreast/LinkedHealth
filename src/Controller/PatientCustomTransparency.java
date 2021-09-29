package Controller;

import Model.Patient;
import Model.UserLogin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientCustomTransparency implements Initializable {

    private UserLogin loginSession;
    private Patient patient;
    private Stage stage;
    private Scene scene;
    private String[] transparencySettings;

    @FXML
    private CheckBox cbName,cbContact,cbDOB,cbGender,cbHeightWeight,cbOtherRemarks,cbReasonOfVisit,cbAdditionalNotes,cbAttachments;
    @FXML
    private TextField txtName,txtPhone,txtEmail,txtDOB,txtGender,txtHeightWeight;
    @FXML
    private ListView<String> lstOtherRemarks;
    @FXML
    private TextArea txtReasonOfVisit,txtAdditionalNotes;
    @FXML
    private TableView<String> tblAttachments;
    @FXML
    private Button btnBack, btnSaveChanges;

    public PatientCustomTransparency(UserLogin loginSession, Patient patient) {
        this.loginSession = loginSession;
        this.patient = patient;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        transparencySettings = patient.getPatientTransparency().split(",");
        btnSaveChanges.setOnAction(this::updateTransparency);
        btnBack.setOnAction(this::backToManageTransparency);


        cbName.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtName.setText("Hidden");
                    txtName.getStyleClass().add("hiddenText");

                }else{
                    txtName.setText("Your Name");
                    txtName.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbContact.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtEmail.setText("Hidden");
                    txtEmail.getStyleClass().add("hiddenText");
                    txtPhone.setText("Hidden");
                    txtPhone.getStyleClass().add("hiddenText");

                }else{
                    txtEmail.setText("Your Email");
                    txtEmail.getStyleClass().remove("hiddenText");
                    txtPhone.setText("Your Phone Number");
                    txtPhone.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbDOB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtDOB.setText("Hidden");
                    txtDOB.getStyleClass().add("hiddenText");

                }else{
                    txtDOB.setText("Your Date of Birth");
                    txtDOB.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbGender.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtGender.setText("Hidden");
                    txtGender.getStyleClass().add("hiddenText");

                }else{
                    txtGender.setText("Your Gender");
                    txtGender.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbHeightWeight.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtHeightWeight.setText("Hidden");
                    txtHeightWeight.getStyleClass().add("hiddenText");

                }else{
                    txtHeightWeight.setText("Your height and weight");
                    txtHeightWeight.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbOtherRemarks.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    lstOtherRemarks.setStyle("-fx-background-color: #a1a1a1");

                }else{
                    lstOtherRemarks.setStyle("-fx-background-color: white");

                }
            }
        });

        cbReasonOfVisit.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if(newValue){
                    txtReasonOfVisit.setText("Reason of visit is hidden from others");
                    txtReasonOfVisit.getStyleClass().add("hiddenText");

                }else{
                    txtReasonOfVisit.setText("Reason of visit will be recorded here");
                    txtReasonOfVisit.getStyleClass().remove("hiddenText");

                }
            }
        });

        cbAdditionalNotes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    txtAdditionalNotes.setText("Additional notes added by the doctor will be hidden from others");
                    txtAdditionalNotes.getStyleClass().add("hiddenTextArea");

                }else{
                    txtAdditionalNotes.setText("Additional notes added by the doctor");
                    txtAdditionalNotes.getStyleClass().remove("hiddenTextArea");

                }
            }
        });

        cbAttachments.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                if(newValue){
                    tblAttachments.getStyleClass().add("hiddenTable");

                }else{
                    tblAttachments.getStyleClass().remove("hiddenTable");

                }
            }
        });






        setupCheckBox();
    }

    public void setupCheckBox(){
        if(transparencySettings[0].equals("false")){
            cbName.setSelected(true);
        }
        if(transparencySettings[1].equals("false")){
            cbContact.setSelected(true);
        }
        if(transparencySettings[2].equals("false")){
            cbDOB.setSelected(true);
        }
        if(transparencySettings[3].equals("false")){
            cbGender.setSelected(true);
        }
        if(transparencySettings[4].equals("false")){
            cbHeightWeight.setSelected(true);
        }
        if(transparencySettings[5].equals("false")){
            cbOtherRemarks.setSelected(true);
        }
        if(transparencySettings[6].equals("false")){
            cbReasonOfVisit.setSelected(true);
        }
        if(transparencySettings[7].equals("false")){
            cbAdditionalNotes.setSelected(true);
        }
        if(transparencySettings[8].equals("false")){
            cbAttachments.setSelected(true);
        }
    }

    public void updateTransparency(ActionEvent actionEvent){
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        String settings = "";

        if (cbName.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbContact.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbDOB.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbGender.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbHeightWeight.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }


        if (cbOtherRemarks.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbAdditionalNotes.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbName.isSelected()){
            settings += "false,";
        } else{
            settings += "true,";
        }

        if (cbAttachments.isSelected()){
            settings += "false";
        } else{
            settings += "true";
        }

        if(sc.updateTransparency(patient.getPatientID(),settings)){
            patient.setPatientTransparency(settings);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transparency settings updated!");
            alert.setHeaderText(null);
            alert.setContentText("Modification have been saved");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Transparency settings not updated!");
            alert.setHeaderText(null);
            alert.setContentText("An issue has occured");
            alert.showAndWait();
        }
    }

    public void backToManageTransparency(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientManageDataTransparency.fxml"));
        PatientManageDataTransparency pmdt = new PatientManageDataTransparency(loginSession,patient);
        loader.setController(pmdt);
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


}
