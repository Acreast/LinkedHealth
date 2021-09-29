package Controller;

import Model.Patient;
import Model.UserLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PatientEditProfile implements Initializable {

    private UserLogin loginSession;
    private Patient patient;
    private SQLDatabaseConnection sc;
    private Stage stage;
    private Scene scene;
    private String newImagePath;

    @FXML
    private TextField txtName,txtGender,txtMartialStatus,txtEmail,txtContact,txtAddress;
    @FXML
    private Button btnChangePicture,btnBack,btnUpdate;
    @FXML
    private ImageView imgProfile;
    @FXML
    private DatePicker dpDOB;


    public PatientEditProfile(UserLogin loginSession, Patient patient){
        this.loginSession = loginSession;
        this.patient = patient;
        sc = new SQLDatabaseConnection();
        newImagePath = "";
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtName.setText(loginSession.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dpDOB.setValue(LocalDate.parse(loginSession.getDOB(),dtf));

        if(loginSession.getGender().equals("M")){
            txtGender.setText("Male");
        } else {
            txtGender.setText("Female");
        }


        if (patient.getMartialStatus() == null || patient.getMartialStatus().equals("")){
            txtMartialStatus.setText("Not set yet");
        } else {
            txtMartialStatus.setText(patient.getMartialStatus());
        }


        if(loginSession.getUserContactNumber().length() > 1){
            txtContact.setText(loginSession.getUserContactNumber());
        }


        if(loginSession.getUserEmail().length()>1){
            txtEmail.setText(loginSession.getUserEmail());
        }


        if(loginSession.getUserAddress().length()>1){
            txtAddress.setText(loginSession.getUserAddress());
        }

        btnUpdate.setOnAction(this::updateProfile);
        btnChangePicture.setOnAction(this::selectProfileImage);
        btnBack.setOnAction(this::backToProfile);
        setImageView();


    }

    public void updateProfile(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = "";

        if(dpDOB.getValue() != null){
            date = dpDOB.getValue().format(dtf);
        } else {
            alert.setTitle("Date of birth input empty");
            alert.setHeaderText(null);
            alert.setContentText("Date of birth cannot be empty, please select your date of birth.");
            alert.showAndWait();
            return;
        }

        sc.patientUpdateProfile(loginSession.getUserID(), patient.getPatientID(), txtName.getText(),
                date, txtMartialStatus.getText(), txtContact.getText(), txtEmail.getText(), txtAddress.getText());
        loginSession.setName(txtName.getText());
        loginSession.setDOB(date);
        loginSession.setUserContactNumber(txtContact.getText());
        loginSession.setUserAddress(txtAddress.getText());
        loginSession.setUserEmail(txtEmail.getText());

        Alert inform = new Alert(Alert.AlertType.INFORMATION);
        inform.setTitle("Update complete!");
        inform.setHeaderText(null);
        inform.setContentText("Update success, now redirecting back to profile page");
        inform.showAndWait();
        btnBack.fire();

    }

    public String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public void selectProfileImage(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);


        if(selectedFile != null ){
            String fileExtension = getFileExtension(selectedFile.getName());
            System.out.println(fileExtension);
            if(fileExtension.equals("jpg")
            || fileExtension.equals("jpeg")
            || fileExtension.equals("png")
            || fileExtension.equals("jfif")
            ){
                byte[] fileByte = new byte[(int)selectedFile.length()];
                Image image = new Image(selectedFile.toURI().toString());
                imgProfile.setImage(image);

                try {
                    FileInputStream fis = new FileInputStream(selectedFile);
                    fis.read(fileByte);
                    sc.patientUpdateProfileImage(patient.getPatientID(),fileByte);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid file type");
                alert.setHeaderText(null);
                alert.setContentText("Please choose an image file.");
                alert.showAndWait();
                return;
            }



        }

    }

    public void setImageView(){
        byte[] imageByte = sc.getProfileImage(patient.getPatientID());
        if (imageByte != null){
            Image img = new Image(new ByteArrayInputStream(imageByte));
            imgProfile.setImage(img);
        }

    }

    public void backToProfile(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientViewProfile.fxml"));
        PatientViewProfile pvp = new PatientViewProfile(loginSession);
        loader.setController(pvp);
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
