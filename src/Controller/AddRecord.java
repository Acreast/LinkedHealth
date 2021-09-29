package Controller;

import Model.*;
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
import java.text.SimpleDateFormat;
import java.util.*;

public class AddRecord implements Initializable {
    private UserLogin loginSession;
    private UserLogin owner;
    private Patient patient;
    private String doctorID;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;
    private String allergies, otherRemarks;
    private String collectedDiagnosis;

    //Record data
    private String recordedData;

    @FXML
    private TextField txtCreatedBy,txtDoctorID,txtDigitalSignature,txtOwner,txtPatientID,txtPhone,txtEmail,
            txtDOB,txtGender,txtHeightWeight,txtTemperature,txtBloodPressure,txtBloodType;
    @FXML
    private TextArea txtVisit,txtExamination,txtDiagnosis,txtNotes;

    @FXML
    private ListView<String> lstAllergies,lstOtherRemarks;

    @FXML
    private Label lblDate;

    @FXML
    private TableView<MedicationTableModel> tblMedication;

    @FXML
    private TableColumn<MedicationTableModel,String> colMedicineName,colDose,colUntil,colExtraNotes;

    @FXML
    private TableColumn<AttachmentTableModel,String> colAttachmentName, colSize,colNotes,colFilePath;

    @FXML
    private TableView<AttachmentTableModel> tblAttachments;

    @FXML
    private Button btnAddRecord,btnBack,btnExaminationDialog,btnDiagnosisDialog,btnMedicationDialog,btnAttachmentDialog;

    private ObservableList<MedicationTableModel> medicationItems;

    private ObservableList<AttachmentTableModel> attachmentItems;




    public AddRecord(UserLogin loginSession, Patient patient){
        sc = new SQLDatabaseConnection();
        this.loginSession = loginSession;
        this.owner = sc.getUserByUserID(patient.getUserID());
        this.patient = patient;
        this.doctorID = sc.getDoctorID(loginSession.getUserID());
        this.collectedDiagnosis ="";


        if (patient.getAllergy() == null || patient.getAllergy().split(",").length <1){
            this.allergies = "Nothing in list";
        } else {
            this.allergies = patient.getAllergy();
        }

        if (patient.getRemark() == null || patient.getRemark().split(",").length <1){
            this.otherRemarks = "Nothing in list";
        } else {
            this.otherRemarks = patient.getRemark();
        }

        medicationItems = FXCollections.observableArrayList(
//                new MedicationTableModel("Pneuma","3 times a day","11/11/2020","Neega better eat all of it"),
//                new MedicationTableModel("Elish","yeah","11/11/2020","Neega better eat all of it"),
//                new MedicationTableModel("Lapsap","Ye","11/11/2020","Neega better eat all of it"),
//                new MedicationTableModel("Some Random Pills","definitely not overdosed","11/11/2020","Neega better eat all of it"),
//                new MedicationTableModel("Ye","2 times a day","11/11/2020","Neega better eat all of it")
        );

        attachmentItems = FXCollections.observableArrayList(
//                new AttachmentTableModel("Xray",".jpg","11/11/2020","Neega better eat all of it"),
//                new AttachmentTableModel("Report",".xml","11/11/2020","Neega better eat all of it"),
//                new AttachmentTableModel("Not report",".txt","11/11/2020","Neega better eat all of it"),
//                new AttachmentTableModel("Good shit",".txt","11/11/2020","Neega better eat all of it"),
//                new AttachmentTableModel("Yes",".json","11/11/2020","Neega better eat all of it")
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddRecord.setOnAction(this::generateRecord);
        btnBack.setOnAction(this::toManagePatientRecordPage);
        btnExaminationDialog.setOnAction(this::openExaminationDialog);
        btnDiagnosisDialog.setOnAction(this::openDiagnosisDialog);
        btnMedicationDialog.setOnAction(this::openMedicationDialog);
        btnAttachmentDialog.setOnAction(this::openAttachmentDialog);
        txtCreatedBy.setText(loginSession.getName());
        txtDoctorID.setText(doctorID);
        txtDigitalSignature.setText("-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        lblDate.setText(sdf.format(date));
        txtOwner.setText(owner.getName());
        txtPatientID.setText(patient.getPatientID());
        txtPhone.setText(owner.getUserContactNumber());
        txtEmail.setText(owner.getUserEmail());
        txtDOB.setText(owner.getDOB());
        if(owner.getGender().equals("M")){
            txtGender.setText("Male");
        } else {
            txtGender.setText("Female");
        }
        String height = "";
        String weight = "";
        if(patient.getHeight() == 0.0f){
            height = "Not set";
        } else {
            height = patient.getHeight().toString();
        }
        if(patient.getWeight() == 0.0f){
            weight = "Not set";
        } else {
            weight = patient.getWeight().toString();
        }


        String bloodType;

        if(patient.getBloodType() == null || patient.getBloodType().equals("")){
            bloodType = "Not set";
        } else {
            bloodType = patient.getBloodType();
        }

        txtHeightWeight.setText(height + "/" + weight);
        txtBloodType.setText(bloodType);
        lstAllergies.getItems().addAll(allergies.split(","));
        lstOtherRemarks.getItems().addAll(otherRemarks.split(","));

        //Tables
        colMedicineName.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        colDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colUntil.setCellValueFactory(new PropertyValueFactory<>("date"));
        colExtraNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        colAttachmentName.setCellValueFactory(new PropertyValueFactory<>("attachmentName"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));


        tblMedication.setItems(medicationItems);
        tblAttachments.setItems(attachmentItems);




    }


    public void generateRecord(ActionEvent actionEvent){

        String recordInformation = //First block data
                txtCreatedBy.getText() + "_,_"+
                txtDoctorID.getText() + "_,_"+
                txtOwner.getText() +"_,_"+
                txtPhone.getText() +"_,_"+
                txtEmail.getText() + "_,_"+
                txtDOB.getText() +"_,_"+
                txtGender.getText() +"_,_"+
                txtHeightWeight.getText();

        String temperature = "",bloodPressure = "";

        if(txtTemperature.getText().equals("")){
            temperature = "Not set";
        } else {
            temperature = txtTemperature.getText();
        }
        if(txtBloodPressure.getText().equals("")){
            bloodPressure = "Not set";
        } else {
            bloodPressure = txtBloodPressure.getText();
        }
        String vitals = //Second block data
                temperature + "_,_" +
                bloodPressure + "_,_" +
                txtBloodType.getText();

        String reasonOfVisit = txtVisit.getText();
        String examination = txtExamination.getText();
        String diagnosis = txtDiagnosis.getText();

        String medication = "";
        for(MedicationTableModel data : tblMedication.getItems()){
            medication += data.toString();
        }
        String additionalNotes = txtNotes.getText();

        String attachments = "";
        for(AttachmentTableModel d : tblAttachments.getItems()){
            attachments += d.toString();
        }

        List<String> data = new ArrayList<>();
        data.add(recordInformation);
        data.add(vitals);
        data.add(allergies);
        data.add(otherRemarks);
        data.add(reasonOfVisit);
        data.add(examination);
        data.add(diagnosis);
        data.add(medication);
        data.add(additionalNotes);
        data.add(attachments);

        if(sc.appendRecord(data,patient.getPatientID(),doctorID,tblMedication.getItems(),tblAttachments.getItems())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Record added");
            alert.setHeaderText(null);
            alert.setContentText("Record created successfully, returning to previous page.");
            alert.showAndWait();
            btnBack.fire();
        }




    }

    public void openExaminationDialog(ActionEvent actionEvent){

        ExaminationDialog ed = new ExaminationDialog(doctorID);

        try {
            String examinationText = ed.display(ed);
            if (!examinationText.equals("")){
                txtExamination.setText(examinationText);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDiagnosisDialog(ActionEvent actionEvent){

        DiagnosisDialog dd = new DiagnosisDialog();
        try {

            String obtainedDiagnosis = dd.display();
            String[] splitDiagnosis;
            if(obtainedDiagnosis != null) {
                splitDiagnosis = obtainedDiagnosis.split("_delimiter_");
                if (splitDiagnosis.length > 1) {
                    collectedDiagnosis = splitDiagnosis[1];
                    txtDiagnosis.setText(splitDiagnosis[0]);
                } else {
                    txtDiagnosis.setText("");
                    collectedDiagnosis = "";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openMedicationDialog(ActionEvent actionEvent){
        MedicationDialog md = new MedicationDialog(collectedDiagnosis, medicationItems);
        ObservableList<MedicationTableModel> items = md.display(md);
        if (items != null){
            medicationItems.removeAll(tblMedication.getItems());
            medicationItems.addAll(items);
        }
    }

    public void openAttachmentDialog(ActionEvent actionEvent){
        AttachmentDialog ad = new AttachmentDialog(attachmentItems);
        ObservableList<AttachmentTableModel> items = ad.display(ad);
        if (items != null){
            attachmentItems.removeAll(tblAttachments.getItems());
            attachmentItems.addAll(items);
        }
    }


    public void toManagePatientRecordPage(ActionEvent actionEvent){
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
