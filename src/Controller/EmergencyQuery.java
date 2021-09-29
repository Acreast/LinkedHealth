package Controller;

import Model.MedicationTableModel;
import Model.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmergencyQuery implements Initializable {

    private List<Record> recordList;
    @FXML
    private Button btnSearch,btnBack;
    @FXML
    private TextField txtPatientID,txtBloodType;
    @FXML
    private ListView<String> lstAllergies;
    @FXML
    private TableView<MedicationTableModel> tblMedication;
    @FXML
    private TableColumn<MedicationTableModel,String> colMedicineName,colDose,colUntil,colExtraNotes;
    private ObservableList<MedicationTableModel> medicationItems;

    private Stage stage;
    private Scene scene;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        medicationItems = FXCollections.observableArrayList();
        tblMedication.setItems(medicationItems);

        colMedicineName.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        colDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colUntil.setCellValueFactory(new PropertyValueFactory<>("date"));
        colExtraNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    public void searchAction(){
        Record record;
        recordList = BlockChain.obtainPatientBlock(txtPatientID.getText());
        if (recordList.size() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Patient not found");
            alert.setHeaderText(null);
            alert.setContentText("Could not find any records with the given patient ID");
            alert.showAndWait();
            return;
        } else {
            //Get last record
            record = recordList.get(recordList.size()-1);
            String bloodType = record.getVitals().split("_,_")[2];
            txtBloodType.setText(bloodType);
            String allergies = record.getAllergies();
            lstAllergies.getItems().addAll(allergies.split(","));
            populateTable(record);

        }


    }

    public void populateTable(Record record) {
        String[] individualMedicationItem = record.getMedication().split("_,_");
        if (individualMedicationItem.length == 0) {
            medicationItems.add(new MedicationTableModel(
                    "Nothing in list", "", "", ""
            ));
        } else {
            for (int i = 0; i < individualMedicationItem.length; i++) {
                String[] metaData = individualMedicationItem[i].split("_split_");
                medicationItems.add(new MedicationTableModel(
                        metaData[0],
                        metaData[1],
                        metaData[2],
                        metaData[3]
                ));
            }
        }

    }

    public void backAction(ActionEvent actionEvent){
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("../View/LoginPage.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
