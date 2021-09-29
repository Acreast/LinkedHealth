package Controller;

import Model.DiagnosisListTableModel;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class DiagnosisDialog implements Initializable {



    private Stage stage;
    private Scene scene;
    private static String diagnosistring;
    private String collectedDiagnosis;

    @FXML
    private TableView<DiagnosisListTableModel> tblDiagnosisList, tblAddedDiagnosis;
    @FXML
    private TableColumn<DiagnosisListTableModel,String> colName,colDescription, colAddedDiagnosis,colNotes;
    @FXML
    private Button btnAdd, btnClearAll, btnRemove, btnConfirm, btnBack;
    @FXML
    private TextField txtSearch;

    private ObservableList<DiagnosisListTableModel> diagnosisItems, addedDiagnosisItem;

    private FilteredList<DiagnosisListTableModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        diagnosistring = "";
        collectedDiagnosis = null;
        SQLDatabaseConnection sc = new SQLDatabaseConnection();

        //Tables
        //Table to search diagnosis
        diagnosisItems = FXCollections.observableArrayList();
        List<DiagnosisListTableModel> diagnosisList = sc.getDiagnosisList();
        colName.setCellValueFactory( new PropertyValueFactory<>( "diagnosisName" ));
        colDescription.setCellValueFactory( new PropertyValueFactory<>( "diagnosisDescription" ));
        tblDiagnosisList.setItems(diagnosisItems);
        diagnosisItems.addAll(diagnosisList);


        //Filter function
        filteredData = new FilteredList<>(diagnosisItems, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(obj -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (obj.diagnosisNameProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (obj.diagnosisDescriptionProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                }
                return false;
            });
        });
        SortedList<DiagnosisListTableModel> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblDiagnosisList.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        tblDiagnosisList.setItems(sortedData);




        //Table of added diagnosis
        addedDiagnosisItem = FXCollections.observableArrayList();
        colAddedDiagnosis.setCellValueFactory( new PropertyValueFactory<>( "diagnosisName" ));
        colNotes.setCellValueFactory( new PropertyValueFactory<>( "diagnosisDescription" ));
        colNotes.setCellFactory(TextFieldTableCell.forTableColumn());
        tblAddedDiagnosis.setItems(addedDiagnosisItem);


        //Button set on action
        btnAdd.setOnAction(this::addDiagnosis);
        btnConfirm.setOnAction(this::confirm);
        btnClearAll.setOnAction(this::removeAll);
        btnRemove.setOnAction(this::removeSingle);
        btnBack.setOnAction(this::backAction);

    }

    public String display() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/DiagnosisDialog.fxml"));
        Parent parent = fxmlLoader.load();


        scene = new Scene(parent);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.showAndWait();

        return diagnosistring;
    }

    public void addDiagnosis(ActionEvent actionEvent){
        DiagnosisListTableModel data = tblDiagnosisList.getSelectionModel().getSelectedItem();
        if(data != null){
            addedDiagnosisItem.add(new DiagnosisListTableModel(
                    data.diagnosisNameProperty().getValue(),""
            ));
        }

    }

    public void removeAll(ActionEvent actionEvent){
        addedDiagnosisItem.removeAll(tblAddedDiagnosis.getItems());
    }

    public void removeSingle(ActionEvent actionEvent){
        DiagnosisListTableModel selectedItem = tblAddedDiagnosis.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            addedDiagnosisItem.remove(selectedItem);
        }
    }

    public void confirm(ActionEvent actionEvent){
        collectedDiagnosis = "";
        final Set<DiagnosisListTableModel> selectedItems = new HashSet<>();
        for(final DiagnosisListTableModel d : tblAddedDiagnosis.getItems()){
            collectedDiagnosis += d.diagnosisNameProperty().getValue() + ",";
            diagnosistring += d.diagnosisNameProperty().getValue() + " - " + d.diagnosisDescriptionProperty().getValue() + "\n" ;
        }

        diagnosistring += "_delimiter_" + collectedDiagnosis;

        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }

    public void backAction(ActionEvent actionEvent){
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }
}
