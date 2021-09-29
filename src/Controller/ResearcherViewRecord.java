package Controller;

import Model.Record;
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
import java.util.ResourceBundle;

public class ResearcherViewRecord implements Initializable {

    private UserLogin loginSession;
    private Patient patient;
    private String doctorID;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;
    private Record record;


    //General Data
    private String createdBy,ownerName,contactNumber,userEmail,DOB,gender,heightWeight;
    //Vitals
    private String temperature,bloodPressure,bloodType;

    private String allergies;
    private String otherRemarks;

    //Encrypted field
    private String reasonOfVisit, additionalNotes, attachments;



    //All data combined
    private String combinedString;

    private String lastPage;
    private String[] dataTransparencySettings;

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
    private TableColumn<AttachmentTableModel,String> colAttachmentName,colSize,colNotes,colFilePath;

    @FXML
    private TableView<AttachmentTableModel> tblAttachments;

    @FXML
    private Button btnAddRecord,btnVerify,btnBack,btnDownload,btnAttachmentDialog,btnExaminationDialog,btnMedicationDialog,btnDiagnosisDialog;

    private ObservableList<MedicationTableModel> medicationItems;

    private ObservableList<AttachmentTableModel> attachmentItems;


    public ResearcherViewRecord(UserLogin loginSession, Patient patient, Record record, String lastPage) {
        this.loginSession = loginSession;
        this.record = record;
        this.patient = patient;
        this.lastPage = lastPage;

        //Initialization of observable list
        medicationItems = FXCollections.observableArrayList();
        attachmentItems = FXCollections.observableArrayList();

        combinedString = record.getGeneralInformation() + record.getVitals() + record.getAllergies() + record.getOtherRemarks() +
                record.getReasonOfVisit() + record.getExamination() + record.getDiagnosis() +record.getMedication() +
                record.getNotes() + record.getAttachments();

        SymmCrypto sc = new SymmCrypto();
        String generalInformation = sc.decrypt(record.getGeneralInformation(),patient.getKey());
        String[] generalInformationArray = generalInformation.split("_,_");

        dataTransparencySettings = patient.getPatientTransparency().split(",");


        createdBy = generalInformationArray[0];
        doctorID = generalInformationArray[1];
        ownerName = generalInformationArray[2];
        contactNumber = generalInformationArray[3];
        userEmail = generalInformationArray[4];
        DOB = generalInformationArray[5];
        gender = generalInformationArray[6];
        heightWeight = generalInformationArray[7];

        String[] vitals = record.getVitals().split("_,_");
        temperature = vitals[0];
        bloodPressure = vitals[1];
        bloodType = vitals[2];

        allergies = record.getAllergies();
        otherRemarks = sc.decrypt(record.getOtherRemarks(),patient.getKey());

        reasonOfVisit = sc.decrypt(record.getReasonOfVisit(),patient.getKey());

        additionalNotes = sc.decrypt(record.getNotes(),patient.getKey());

        attachments = sc.decrypt(record.getAttachments(), patient.getKey());





    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtCreatedBy.setText(createdBy);
        txtDoctorID.setText(doctorID);
        txtDigitalSignature.setText(record.getDigitalSignature());
        if(dataTransparencySettings[0].equals("false")){
            txtOwner.setText("Hidden");
            txtOwner.getStyleClass().add("hiddenText");
        } else{
            txtOwner.setText(ownerName);
        }

        if(dataTransparencySettings[1].equals("false")){
            txtEmail.setText("Hidden");
            txtEmail.getStyleClass().add("hiddenText");
            txtPhone.setText("Hidden");
            txtPhone.getStyleClass().add("hiddenText");
        } else {
            txtPhone.setText(contactNumber);
            txtEmail.setText(userEmail);
        }


        txtPatientID.setText(patient.getPatientID());

        if(dataTransparencySettings[2].equals("false")){
            txtDOB.setText("Hidden");
            txtDOB.getStyleClass().add("hiddenText");
        } else {
            txtDOB.setText(DOB);
        }

        if(dataTransparencySettings[3].equals("false")){
            txtGender.setText("Hidden");
            txtGender.getStyleClass().add("hiddenText");
        } else{
            txtGender.setText(gender);
        }

        if(dataTransparencySettings[4].equals("false")){
            txtHeightWeight.setText("Hidden");
            txtHeightWeight.getStyleClass().add("hiddenText");
        } else {
            txtHeightWeight.setText(heightWeight);
        }

        if(dataTransparencySettings[5].equals("false")){
            lstOtherRemarks.getItems().add("hidden");
        } else {
            lstOtherRemarks.getItems().addAll(otherRemarks.split(","));
        }

        if(dataTransparencySettings[6].equals("false")){
            txtVisit.setText("Hidden");
            txtVisit.getStyleClass().add("hiddenText");
        } else {
            txtVisit.setText(reasonOfVisit);
        }

        if(dataTransparencySettings[7].equals("false")){
            txtNotes.setText("Hidden");
            txtNotes.getStyleClass().add("hiddenTextArea");
        } else {
            txtNotes.setText(additionalNotes);
        }






        txtBloodType.setText(bloodType);
        txtTemperature.setText(temperature);
        txtBloodPressure.setText(bloodPressure);


        lstAllergies.getItems().addAll(allergies.split(","));


        txtExamination.setText(record.getExamination());
        txtDiagnosis.setText(record.getDiagnosis());


        lblDate.setText(record.getDate());

        btnAddRecord.setVisible(false);
        btnVerify.setVisible(true);
        btnBack.setOnAction(this::toManagePatientRecordPage);
        btnVerify.setOnAction(this::verifyDigitalSignature);
        btnDownload.setVisible(true);
        btnDownload.setOnAction(this::downloadFile);
        btnAttachmentDialog.setVisible(false);
        btnDiagnosisDialog.setVisible(false);
        btnExaminationDialog.setVisible(false);
        btnMedicationDialog.setVisible(false);
        txtTemperature.setEditable(false);
        txtBloodPressure.setEditable(false);
        txtDiagnosis.setEditable(false);
        txtVisit.setEditable(false);
        txtNotes.setEditable(false);
        txtExamination.setEditable(false);




        //tables
        colMedicineName.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        colDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colUntil.setCellValueFactory(new PropertyValueFactory<>("date"));
        colExtraNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        colAttachmentName.setCellValueFactory(new PropertyValueFactory<>("attachmentName"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        populateTable();

        if(dataTransparencySettings[8].equals("false")){
            tblAttachments.getStyleClass().add("hiddenTable");
            attachmentItems.removeAll(tblAttachments.getItems());
            attachmentItems.add(new AttachmentTableModel("Hidden","","",""));
            btnDownload.setVisible(false);
        }

    }

    public void verifyDigitalSignature(ActionEvent actionEvent){
        DigitalSignature ds = new DigitalSignature();
        boolean isAuthentic = ds.verify(combinedString,record.getDigitalSignature(),record.getPublicKey(doctorID)) ;
        if (isAuthentic){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Digital Signature verified");
            alert.setHeaderText(null);
            alert.setContentText("Data has not been tampered");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Data has been tampered");
            alert.setHeaderText(null);
            alert.setContentText("This record has been tampered, please contact the firm to report this issue");
            alert.showAndWait();
        }
    }

    public void populateTable(){
        String[] individualMedicationItem = record.getMedication().split("_,_");
        if(individualMedicationItem.length ==1){
            medicationItems.add(new MedicationTableModel(
                    "Nothing in list","", "" ,""
            ));
        } else {
            for(int i = 0; i<individualMedicationItem.length ; i++){
                String[] metaData = individualMedicationItem[i].split("_split_");
                medicationItems.add(new MedicationTableModel(
                        metaData[0],
                        metaData[1],
                        metaData[2],
                        metaData[3]
                ));
            }
        }



        String[] individualAttachmentItem = attachments.split("_,_");
        if(individualAttachmentItem.length == 1){
            attachmentItems.add(new AttachmentTableModel(
                    "Nothing in list","","",""
            ));
        } else {
            for(int i = 0; i<individualAttachmentItem.length ; i++){
                String[] metaData = individualAttachmentItem[i].split("_split_");
                attachmentItems.add(new AttachmentTableModel(
                        metaData[0],
                        metaData[1],
                        metaData[2],
                        metaData[3]
                ));
            }
        }


        tblMedication.setItems(medicationItems);
        tblAttachments.setItems(attachmentItems);
    }

    public void toManagePatientRecordPage(ActionEvent actionEvent){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ViewPastRecord.fxml"));
            ResearcherViewRecordList rvrl = new ResearcherViewRecordList(loginSession);
            loader.setController(rvrl);
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

    public void downloadFile(ActionEvent actionEvent){
        AttachmentTableModel selectedItem = tblAttachments.getSelectionModel().getSelectedItem();

        if(selectedItem!=null){
            SQLDatabaseConnection sc = new SQLDatabaseConnection();
            sc.retrieveAttachment(patient.getPatientID(),record.getTimeStamp(),selectedItem.attachmentNameProperty().getValue());

        }
    }


}
