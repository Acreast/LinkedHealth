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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageRecord implements Initializable {


    private UserLogin loginSession;
    private UserLogin recordOwner;
    private Patient patient;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;
    private List<Record> recordList;
    private ObservableList<ManageRecordTableModel> recordTableItem;
    private FilteredList<ManageRecordTableModel> filteredData;
    private ObservableList<OngoingMedicationTableModel> medicationTableItem;


    @FXML
    private Label lblName,lblDOB,lblGender,lblEmail,lblPhone,lblBloodType,lblHeight,lblWeight,lblMainInsurance;
    @FXML
    private ImageView imgProfile;
    @FXML
    private ListView<String> lstAllergies,lstOtherRemarks;
    @FXML
    private TableView<ManageRecordTableModel> tblRecords;
    @FXML
    private TableColumn<ManageRecordTableModel,String> colDate,colReasonOfVisit;
    @FXML
    private TableView<OngoingMedicationTableModel> tblOngoingMedications;
    @FXML
    private TableColumn<OngoingMedicationTableModel,String> colMedicationName,colTakeUntil,colDose,colDateAdded,colAddedBy,colNotes;

    @FXML
    private TableView<?> tblTreatment;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnBack,btnAddNew,btnViewRecord,btnManagePatientPhysical;



    public ManageRecord(UserLogin loginSession, String patientIDString){
        sc = new SQLDatabaseConnection();
        this.loginSession = loginSession;


        if(!loginSession.getUserRole().equals("Patient")){
            this.patient = sc.getPatientByPatientID(patientIDString);
            recordOwner = sc.getUserByUserID(patient.getUserID());
        } else{
            this.patient = sc.getPatientByUserID(loginSession.getUserID());
            recordOwner = loginSession;
        }

        recordTableItem = FXCollections.observableArrayList();
        medicationTableItem = FXCollections.observableArrayList();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(loginSession.getUserRole().equals("Patient")){
            btnAddNew.setVisible(false);
            btnManagePatientPhysical.setVisible(false);
        }
        lblName.setText(recordOwner.getName());
        lblDOB.setText("DOB: "+recordOwner.getDOB());
        if(recordOwner.getGender().equals("M")){
            lblGender.setText("Gender: Male");
        } else {
            lblGender.setText("Gender: Female");
        }

        lblEmail.setText(recordOwner.getUserEmail());
        lblPhone.setText(recordOwner.getUserContactNumber());
        if(patient.getMainInsurance()!= null){
            lblMainInsurance.setText(patient.getMainInsurance());
        } else {
            lblMainInsurance.setText("Not set");
        }
        if(patient.getBloodType() == null || patient.getBloodType().equals("")){
            lblBloodType.setText("Blood type: not set");
        } else {
            lblBloodType.setText("Blood type: " + patient.getBloodType());
        }
        if(patient.getWeight() == 0.0f){
            lblWeight.setText("Weight: Not set");
        } else {
            lblWeight.setText("Weight: "+patient.getWeight().toString());
        }
        if(patient.getHeight() == 0.0f){
            lblHeight.setText("Height: not set");
        } else {
            lblHeight.setText("Height: "+patient.getHeight().toString());
        }
        if(patient.getMainInsurance() == null || patient.getMainInsurance().equals("")){
            lblHeight.setText("Height: not set");
        } else {
            lblHeight.setText("Height: "+patient.getHeight().toString());
        }

        if(patient.getAllergy()!= null){
            String[] allergies;
            allergies = patient.getAllergy().split(",");
            lstAllergies.getItems().addAll(allergies);
        }

        if(patient.getRemark()!=null){
            String[] otherRemarks;
            otherRemarks = patient.getRemark().split(",");
            lstOtherRemarks.getItems().addAll(otherRemarks);
        }



        setImageView();

        //Set on action
        btnAddNew.setOnAction(this::toAddNewRecordPage);
        btnViewRecord.setOnAction(this::viewRecord);
        btnManagePatientPhysical.setOnAction(this::toEditPatientPhysicalPage);
        btnBack.setOnAction(this::backAction);

        //Record table
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReasonOfVisit.setCellValueFactory(new PropertyValueFactory<>("reasonOfVisit"));
        tblRecords.setItems(recordTableItem);
        getRecords();

        //Medication table
        colMedicationName.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        colTakeUntil.setCellValueFactory(new PropertyValueFactory<>("takeUntil"));
        colDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colAddedBy.setCellValueFactory(new PropertyValueFactory<>("addedBy"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colDateAdded.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        medicationTableItem.addAll(sc.obtainOngoingMedication(patient.getPatientID()));
        tblOngoingMedications.setItems(medicationTableItem);

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
        SortedList<ManageRecordTableModel> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblRecords.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        tblRecords.setItems(sortedData);


    }


    public void setImageView(){
        byte[] imageByte = sc.getProfileImage(patient.getPatientID());
        if (imageByte != null){
            Image img = new Image(new ByteArrayInputStream(imageByte));
            imgProfile.setImage(img);
        }

    }




    public void backAction(ActionEvent actionEvent){
        if(loginSession.getUserRole().equals("Doctor")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ManageConnection.fxml"));
            DoctorManageConnection dmc = new DoctorManageConnection(loginSession);
            loader.setController(dmc);
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
        } else {
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

    public void toAddNewRecordPage(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Record.fxml"));
        AddRecord ar = new AddRecord(loginSession,patient);
        loader.setController(ar);
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

    public void getRecords(){
        recordList = BlockChain.obtainPatientBlock(patient.getPatientID());
        if (recordList == null) return;
        recordList.forEach((e)->
                populateTable(e.getDate(),e.getDiagnosis(),e.getTimeStamp())
                );

    }

    public void populateTable(String date,String diagnosis,String timeStamp ){
        recordTableItem.add(new ManageRecordTableModel(date,diagnosis,timeStamp));
    }


    public void viewRecord(ActionEvent actionEvent){
        ManageRecordTableModel data = tblRecords.getSelectionModel().getSelectedItem();

        if (data == null) return;

        Record selectedRecord = recordList.stream()
                .filter(r -> data.timeStampProperty().getValue().equals(r.getTimeStamp()))
                .findAny()
                .orElse(null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Record.fxml"));
        ViewRecord vr = new ViewRecord(loginSession,patient,selectedRecord,"");
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

    public void toEditPatientPhysicalPage(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/DoctorEditPatientMedical.fxml"));
        DoctorEditPatientPhysical depp = new DoctorEditPatientPhysical(loginSession,patient);
        loader.setController(depp);
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
