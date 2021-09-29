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

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class DoctorManageConnection implements Initializable {

    private UserLogin loginSession;
    private String doctorID;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtSearch,txtPatientID;
    @FXML
    private Button btnApprove,btnDisapprove, btnBack,btnAddNew,btnCancelRequest,btnManageRecords;
    @FXML
    private TableView<PatientConnectionTableModel> tblConnection;
    @FXML
    private TableColumn<PatientConnectionTableModel,String> colID,colName,colDate,colStatus;

    private FilteredList<PatientConnectionTableModel> filteredData;

    private ObservableList<PatientConnectionTableModel> items;

    public DoctorManageConnection(UserLogin loginSession){
        this.loginSession = loginSession;
        sc = new SQLDatabaseConnection();
        this.doctorID = sc.getDoctorID(loginSession.getUserID());
        items = sc.obtainPatientConnectionList(doctorID, "DoctorID");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set Visibility
        btnApprove.setVisible(false);
        btnDisapprove.setVisible(false);
        btnCancelRequest.setVisible(true);
        btnAddNew.setVisible(true);
        btnManageRecords.setVisible(true);
        txtPatientID.setVisible(true);

        //Set on action
        btnAddNew.setOnAction(this::applyConnection);
        btnCancelRequest.setOnAction(this::cancelRequest);
        btnBack.setOnAction(this::backToHomepage);
        btnManageRecords.setOnAction(this::toManagePatientRecordPage);



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


    }

    public void applyConnection(ActionEvent actionEvent){
        UserLogin queryUser = sc.findPatientByIC(txtPatientID.getText());


        if(queryUser!= null){

            if(!queryUser.getUserRole().equals("Patient")) {
                Alert userRoleError = new Alert(Alert.AlertType.WARNING);
                userRoleError.setTitle("User is not a patient");
                userRoleError.setHeaderText(null);
                userRoleError.setContentText("The user with the given IC is not a patient.");
                userRoleError.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("User found");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to apply the connection with "+queryUser.getName()+"?");

            if(alert.showAndWait().get() == ButtonType.OK) {

                if (sc.applyConnection(queryUser.getUserID(),doctorID)){
                    Alert connectionApplied = new Alert(Alert.AlertType.INFORMATION);
                    connectionApplied.setTitle("Connection request applied");
                    connectionApplied.setHeaderText(null);
                    connectionApplied.setContentText("Connection with "+ queryUser.getName()+ " has been applied!");
                    connectionApplied.showAndWait();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();


                    items.add( new PatientConnectionTableModel(
                            queryUser.getUserID(),
                            queryUser.getName(),
                            sdf.format(date),
                            "Pending"
                    ));
                    txtPatientID.setText("");
                } else {
                    Alert complete = new Alert(Alert.AlertType.WARNING);
                    complete.setTitle("Connection exists");
                    complete.setHeaderText(null);
                    complete.setContentText("Connection with " + queryUser.getName()+" already exist.");
                    complete.showAndWait();
                }

            }


        }else {
            Alert userDoesNotExist = new Alert(Alert.AlertType.WARNING);
            userDoesNotExist.setTitle("User does not exist");
            userDoesNotExist.setHeaderText(null);
            userDoesNotExist.setContentText("There is no patient with the given IC.");
            userDoesNotExist.showAndWait();
        }

    }

    public void cancelRequest(ActionEvent actionEvent) {
        PatientConnectionTableModel data = tblConnection.getSelectionModel().getSelectedItem();

        if(data.statusProperty().equals("Approved")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid action");
            alert.setHeaderText(null);
            alert.setContentText("Connection is approved, cannot be canceled");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("User found");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to cancel the connection request with " + data.nameProperty().getValue() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if(sc.cancelConnectionRequest(data.idProperty().getValue(),doctorID)){
                Alert removed = new Alert(Alert.AlertType.INFORMATION);
                removed.setTitle("Request removed");
                removed.setHeaderText(null);
                removed.setContentText("The request has been removed.");
                removed.showAndWait();

                items.remove(data);
            } else{
                Alert removed = new Alert(Alert.AlertType.WARNING);
                removed.setTitle("Technical error");
                removed.setHeaderText(null);
                removed.setContentText("A technical error has occurred, please contact for assistance.");
                removed.showAndWait();
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

    public void toManagePatientRecordPage(ActionEvent actionEvent){
        PatientConnectionTableModel data = tblConnection.getSelectionModel().getSelectedItem();
        if(data != null){
            if(!data.statusProperty().getValue().equals("Approved")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setTitle("Connection not approved yet");
                alert.setContentText("The connection must be approved by the user to manage his/her records.");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ManageRecord.fxml"));

            ManageRecord mr = new ManageRecord(loginSession,data.idProperty().getValue());
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

}
