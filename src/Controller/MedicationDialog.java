package Controller;

import Model.DiagnosisListTableModel;
import Model.ExaminationDialogTableModel;
import Model.MedicationListTableModel;
import Model.MedicationTableModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MedicationDialog implements Initializable {

    private String collectedDiagnosis;
    private static ObservableList<MedicationTableModel> medicationItems;
    private FilteredList<MedicationListTableModel> filteredData;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<MedicationTableModel> tblAddedMedication;
    @FXML
    private TableView<MedicationListTableModel> tblMedicationList;
    @FXML
    private TableColumn<MedicationTableModel, String> colMedicineName,colDose,colDuration,colNotes;
    @FXML
    private TableColumn<MedicationListTableModel,String> colMedicationName,colDescription;
    @FXML
    private Button btnClearAll,btnConfirm,btnRemove,btnAdd,btnBack;
    @FXML
    private ListView<String> lstDiagnosis;

    private ObservableList<MedicationTableModel> items;
    private ObservableList<MedicationListTableModel> medicationList;




    public MedicationDialog(String collectedDiagnosis, ObservableList<MedicationTableModel> items) {
        medicationList = FXCollections.observableArrayList();
        this.collectedDiagnosis = collectedDiagnosis;
        this.items = FXCollections.observableArrayList();
        this.items.addAll(items);

    }


    public ObservableList<MedicationTableModel> display(MedicationDialog md){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/MedicationDialog.fxml"));
        fxmlLoader.setController(md);
        Parent parent = null;
        try {
            parent = fxmlLoader.load();

            scene = new Scene(parent);
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return medicationItems;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        SQLDatabaseConnection sc = new SQLDatabaseConnection();

        //Diagnosis list view
        if(collectedDiagnosis.split(",").length>0){
            lstDiagnosis.getItems().addAll(collectedDiagnosis.split(","));
        }


        //Medication table list
        colMedicationName.setCellValueFactory( new PropertyValueFactory<>( "medicationName" ));
        colDescription.setCellValueFactory(new PropertyValueFactory<>( "description" ));
        List<MedicationListTableModel> retrievedList = sc.getMedicationList();
        medicationList.addAll(retrievedList);
        tblMedicationList.setItems(medicationList);

        //Added medication table list
        colMedicineName.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        colMedicineName.setCellFactory(TextFieldTableCell.forTableColumn());
        colDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        tblAddedMedication.setItems(items);


        //Button set on action
        btnAdd.setOnAction(this::addMedication);
        btnRemove.setOnAction(this::removeMedication);
        btnConfirm.setOnAction(this::confirmAction);
        btnClearAll.setOnAction(this::clearAll);
        btnBack.setOnAction(this::backAction);

        //Search function
        filteredData = new FilteredList<>(medicationList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(obj -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (obj.medicationNameProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (obj.descriptionProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                }
                return false;
            });
        });
        SortedList<MedicationListTableModel> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblMedicationList.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        tblMedicationList.setItems(sortedData);






    }

    public void addMedication(ActionEvent actionEvent){
        MedicationListTableModel selectedItem = tblMedicationList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            MedicationCreateDialog mcd = new MedicationCreateDialog(selectedItem.medicationNameProperty().getValue());
            MedicationTableModel item =  mcd.display(mcd);
            if(item != null){
                items.add(item);
            }
        } else {
            MedicationCreateDialog mcd = new MedicationCreateDialog("");
            MedicationTableModel item =  mcd.display(mcd);
            if(item != null){
                items.add(item);
            }
        }

    }

    public void removeMedication(ActionEvent actionEvent){
        MedicationTableModel selectedItem = tblAddedMedication.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            items.remove(selectedItem);
        }
    }

    public void clearAll(ActionEvent actionEvent){
        items.removeAll(tblAddedMedication.getItems());
    }

    public void confirmAction(ActionEvent actionEvent){
        medicationItems = tblAddedMedication.getItems();
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }

    public void backAction(ActionEvent actionEvent){
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }


}
