package Controller;

import Model.Patient;
import Model.PatientConnectionTableModel;
import Model.UserLogin;
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
import java.util.ResourceBundle;

public class PatientManageConnection implements Initializable {

    private UserLogin loginSession;
    private Patient patient;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;


    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnApprove,btnDisapprove, btnBack;
    @FXML
    private TableView<PatientConnectionTableModel> tblConnection;
    @FXML
    private TableColumn<PatientConnectionTableModel,String> colID,colName,colDate,colStatus;

    private FilteredList<PatientConnectionTableModel> filteredData;

    private ObservableList<PatientConnectionTableModel> items;




    public PatientManageConnection(UserLogin loginSession) {
        this.loginSession = loginSession;
        sc = new SQLDatabaseConnection();
        this.patient = sc.getPatientByUserID(loginSession.getUserID());
        items = sc.obtainPatientConnectionList(patient.getPatientID(), "PatientID");

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Populate the table
        final ObservableList<TableColumn<PatientConnectionTableModel, ?>> columns = tblConnection.getColumns();
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>( "name" ));
        colDate.setCellValueFactory(new PropertyValueFactory<>( "date" ));
        colStatus.setCellValueFactory(new PropertyValueFactory<>( "status" ));

        tblConnection.setItems(items);


        //Filter function
        filteredData = new FilteredList<>(items, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(obj -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (obj.nameProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (obj.dateProperty().getValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                }
                return false;
            });
        });
        SortedList<PatientConnectionTableModel> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblConnection.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        tblConnection.setItems(sortedData);


        btnApprove.setOnAction(this::approveConnection);
        btnDisapprove.setOnAction(this::disapproveConnection);
        btnBack.setOnAction(this::backToHomepage);
    }

    public void approveConnection(ActionEvent actionEvent){
        PatientConnectionTableModel data = tblConnection.getSelectionModel().getSelectedItem();

        if(data.statusProperty().getValue().equals("Approved")){
            Alert inform = new Alert(Alert.AlertType.INFORMATION);
            inform.setTitle("Connection already approved");
            inform.setHeaderText(null);
            inform.setContentText("Connection with " + data.nameProperty().getValue() + " is already established.");
            inform.showAndWait();
            return;
        }

        int itemIndex = tblConnection.getSelectionModel().getSelectedIndex();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Connection confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to approve the connection with "+data.nameProperty().getValue()+"?");
        if(alert.showAndWait().get() == ButtonType.OK){

            if(sc.updateConnection(patient.getPatientID(),data.idProperty().getValue(),"Approved")){
                items.get(itemIndex).statusProperty().setValue(
                        "Approved"
                );
                Alert complete = new Alert(Alert.AlertType.INFORMATION);
                complete.setTitle("Connection approved");
                complete.setHeaderText(null);
                complete.setContentText("Connection with " + data.nameProperty().getValue() + " has been established," +
                        " he/she can now add medical records");
                complete.showAndWait();

            } else {
                Alert complete = new Alert(Alert.AlertType.WARNING);
                complete.setTitle("Technical issue occurred");
                complete.setHeaderText(null);
                complete.setContentText("A technical issue has occurred, please contact for support if this happens again.");
                complete.showAndWait();
            }


        }
    }

    public void disapproveConnection(ActionEvent actionEvent){
        PatientConnectionTableModel data = tblConnection.getSelectionModel().getSelectedItem();

        if(data.statusProperty().getValue().equals("Disapproved")){
            Alert inform = new Alert(Alert.AlertType.INFORMATION);
            inform.setTitle("Connection already disapproved");
            inform.setHeaderText(null);
            inform.setContentText("Connection request from " + data.nameProperty().getValue() + " has already been rejected.");
            inform.showAndWait();
            return;
        }

        //Option to change if the connection approval cannot be reversed
//        if(data.statusProperty().getValue().equals("Approved")){
//            Alert inform = new Alert(Alert.AlertType.INFORMATION);
//            inform.setTitle("Connection already approved");
//            inform.setHeaderText(null);
//            inform.setContentText("Connection request from " + data.nameProperty().getValue() + " has already been approved, " +
//                    "the action cannot be reversed.");
//            inform.showAndWait();
//            return;
//        }

        int itemIndex = tblConnection.getSelectionModel().getSelectedIndex();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Connection confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to disapprove the connection with "+data.nameProperty().getValue()+"? You can still approve if you changed your mind.");
        if(alert.showAndWait().get() == ButtonType.OK){

            if(sc.updateConnection(patient.getPatientID(),data.idProperty().getValue(),"Disapproved")){
                items.get(itemIndex).statusProperty().setValue(
                        "Disapproved"
                );
                Alert complete = new Alert(Alert.AlertType.INFORMATION);
                complete.setTitle("Connection disapproved");
                complete.setHeaderText(null);
                complete.setContentText("Connection request from " + data.nameProperty().getValue() + " has been rejected.");
                complete.showAndWait();

            } else {
                Alert complete = new Alert(Alert.AlertType.WARNING);
                complete.setTitle("Technical issue occurred");
                complete.setHeaderText(null);
                complete.setContentText("A technical issue has occurred, please contact for support if this happens again.");
                complete.showAndWait();
            }
        }
    }

    public void backToHomepage(ActionEvent actionEvent){
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
