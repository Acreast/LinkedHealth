package Controller;

import Model.*;
import Model.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class DoctorViewPastRecord implements Initializable {


    private List<Record> recordList;

    private UserLogin loginSession;
    private String doctorID;
    private ObservableList<PastRecordTableModel> recordTableItem;
    private FilteredList<PastRecordTableModel> filteredData;
    private Stage stage;
    private Scene scene;


    @FXML
    private TableView<PastRecordTableModel> tblRecords;
    @FXML
    private TableColumn<PastRecordTableModel,String> colOwner,colDate,colReasonOfVisit;
    @FXML
    private Button btnBack,btnView;
    @FXML
    private TextField txtSearch;

    public DoctorViewPastRecord(UserLogin loginSession) {
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        this.loginSession = loginSession;
        doctorID = sc.getDoctorID(loginSession.getUserID());

    }


    public void getRecords(){
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        recordList = BlockChain.obtainPastRecordBlock(doctorID);
        if (recordList == null) return;
        recordList.forEach((e)->{

            populateTable(e.getPatientID(),e.getDate(),e.getDiagnosis(),e.getTimeStamp(),sc.getUserByUserID(sc.getPatientByPatientID(e.getPatientID()).getUserID()).getName());
            }
        );

    }

    public void populateTable(String patientID,String date,String diagnosis,String timeStamp,String name ){
        recordTableItem.add(new PastRecordTableModel(patientID,diagnosis,date,timeStamp,name));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recordTableItem = FXCollections.observableArrayList();
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReasonOfVisit.setCellValueFactory(new PropertyValueFactory<>("reasonOfVisit"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("patientName"));


        tblRecords.setItems(recordTableItem);
        getRecords();
        //Button on action
        btnBack.setOnAction(this::backAction);
        btnView.setOnAction(this::viewRecord);

        //Search function
        filteredData = new FilteredList<>(recordTableItem, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(obj -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (obj.reasonOfVisitProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (obj.dateProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                }
                return false;
            });
        });
        SortedList<PastRecordTableModel> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblRecords.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        tblRecords.setItems(sortedData);


    }

    public void backAction(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/HomePage.fxml"));
        HomePage newController = new HomePage(loginSession);
        loader.setController(newController);
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

    public void viewRecord(ActionEvent actionEvent){
        PastRecordTableModel data = tblRecords.getSelectionModel().getSelectedItem();
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        if (data == null) return;

        Record selectedRecord = recordList.stream()
                .filter(r -> data.timeStampProperty().getValue().equals(r.getTimeStamp()))
                .findAny()
                .orElse(null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Record.fxml"));
        ViewRecord vr = new ViewRecord(loginSession,sc.getPatientByPatientID(data.patientIDProperty().getValue()),selectedRecord,"ViewPastRecord");
        loader.setController(vr);
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
