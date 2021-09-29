package Controller;

import Model.Patient;
import Model.UserLogin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DoctorEditPatientPhysical implements Initializable {
    private Stage stage;
    private Scene scene;
    private UserLogin loginSession;
    private Patient patient;
    private String allergyString = "";
    private String otherRemarksString = "";
    private ObservableList<String> allergyOList;
    private ObservableList<String> otherRemarksOList;

    @FXML
    private TextField txtAllergies,txtOtherRemarks,txtBloodType,txtWeight,txtHeight,txtMainInsurance;
    @FXML
    private ListView<String> lstAllergies,lstOtherRemarks;
    @FXML
    private Button btnAddAllergies,btnRemoveAllergies,btnAddRemarks,btnRemoveRemarks,btnBack,btnUpdate;

    public DoctorEditPatientPhysical(UserLogin loginSession, Patient patient) {
        this.loginSession = loginSession;
        this.patient = patient;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allergyOList = FXCollections.observableArrayList();
        if (patient.getAllergy()!= null){
            allergyOList.addAll(Arrays.stream(patient.getAllergy().split(",")).collect(Collectors.toList()));
        }

        otherRemarksOList = FXCollections.observableArrayList();
        if(patient.getRemark()!=null){
            otherRemarksOList.addAll(Arrays.stream(patient.getRemark().split(",")).collect(Collectors.toList()));
        }

        lstAllergies.setItems(allergyOList);
        lstOtherRemarks.setItems(otherRemarksOList);

        if(patient.getBloodType() != null){
            txtBloodType.setText(patient.getBloodType());
        } else {
            txtBloodType.setText("Not set");
        }

        if(patient.getWeight() != null || patient.getWeight() != 0.0f){
            txtWeight.setText(patient.getWeight().toString());
        } else {
            txtWeight.setText("Not set");
        }

        if(patient.getHeight() != null || patient.getWeight() != 0.0f){
            txtHeight.setText(patient.getHeight().toString());
        } else {
            txtHeight.setText("Not set");
        }
        if(patient.getMainInsurance() != null){
            txtMainInsurance.setText(patient.getMainInsurance().toString());
        } else {
            txtMainInsurance.setText("Not set");
        }




        btnAddAllergies.setOnAction(this::addAllergies);
        btnAddRemarks.setOnAction(this::addOtherRemarks);
        btnRemoveAllergies.setOnAction(this::removeAllergies);
        btnRemoveRemarks.setOnAction(this::removeOtherRemarks);
        btnUpdate.setOnAction(this::updateAction);
        btnBack.setOnAction(this::backAction);








    }

    public void addAllergies(ActionEvent actionEvent){
        if(!txtAllergies.getText().equals("")){
            allergyOList.add(txtAllergies.getText());
            txtAllergies.setText("");
        }
    }
    public void removeAllergies(ActionEvent actionEvent){
        String selectedAllergy = lstAllergies.getSelectionModel().getSelectedItem();
        if(selectedAllergy!= null){
            allergyOList.remove(selectedAllergy);
            txtOtherRemarks.setText("");
        }
    }


    public void addOtherRemarks(ActionEvent actionEvent){
        if(!txtOtherRemarks.getText().equals("")){
            otherRemarksOList.add(txtOtherRemarks.getText());
        }
    }

    public void removeOtherRemarks(ActionEvent actionEvent){
        String selectedOtherRemarks = lstOtherRemarks.getSelectionModel().getSelectedItem();
        if(selectedOtherRemarks!= null){
            otherRemarksOList.remove(selectedOtherRemarks);
        }
    }

    public void updateAction(ActionEvent actionEvent){
        List<String> allergyList = allergyOList.stream().collect(Collectors.toList());
        List<String> otherRemarksList = otherRemarksOList.stream().collect(Collectors.toList());

        if(txtBloodType.getText().length()>10){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Blood type length invalid");
            alert.setHeaderText(null);
            alert.setContentText("Blood type length can not be more than 10");
            alert.showAndWait();
            return;
        }

        if(txtMainInsurance.getText().length()>50){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Main insurance length invalid");
            alert.setHeaderText(null);
            alert.setContentText("Main insurance can not be more than 50 words");
            alert.showAndWait();
            return;
        }

        Float weight;
        Float height;
        try{
            weight = Float.parseFloat(txtWeight.getText());
        } catch (NumberFormatException nfe){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid syntax");
            alert.setHeaderText(null);
            alert.setContentText("Weight must be in number format");
            alert.showAndWait();
            return;
        }
        try{
            height = Float.parseFloat(txtHeight.getText());
        } catch (NumberFormatException nfe){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid syntax");
            alert.setHeaderText(null);
            alert.setContentText("Weight must be in number format");
            alert.showAndWait();
            return;
        }
        if(weight>999||weight < 1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid number");
            alert.setHeaderText(null);
            alert.setContentText("Weight must be more than 0 and less than 999");
            alert.showAndWait();
            return;
        }
        if(height>999||height < 1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid number");
            alert.setHeaderText(null);
            alert.setContentText("Weight must be more than 0 and less than 999");
            alert.showAndWait();
            return;
        }


        allergyList.forEach(e->{
            allergyString += e.toString() + ",";
        });

        otherRemarksList.forEach(e->{
            otherRemarksString += e.toString()+ ",";
        });


        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        if(
                sc.updatePatientPhysical(patient.getPatientID(),allergyString,otherRemarksString,txtBloodType.getText(),
                weight,height,txtMainInsurance.getText())
        ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Complete");
            alert.setHeaderText(null);
            alert.setContentText("Patient Infomation updated");
            alert.showAndWait();
            patient.setAllergy(allergyString);
            patient.setRemark(otherRemarksString);
            patient.setBloodType(txtBloodType.getText());
            patient.setWeight(weight);
            patient.setHeight(height);
            patient.setMainInsurance(txtMainInsurance.getText());
        }

    }

    public void backAction(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ManageRecord.fxml"));

        ManageRecord mr = new ManageRecord(loginSession,patient.getPatientID());
        loader.setController(mr);
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
