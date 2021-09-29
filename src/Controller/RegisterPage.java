package Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.poi.util.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegisterPage implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorAccountCredentials;
    @FXML
    private AnchorPane anchorAccountDetails;
    @FXML
    private TextField txtUsername, txtAddress, txtIC, txtName, txtContact,txtEmail;
    @FXML
    private DatePicker dpDOB;
    @FXML
    private ChoiceBox<String> cbGender, cbRole;
    @FXML
    private PasswordField txtPassword, txtRePassword;
    @FXML
    private  Button btnBackToLogin;


    private String[] genderArray = {"Male","Female"};
    private String[] roleArray = {"Patient","Doctor","Researcher"};

    //Connect to database
    private SQLDatabaseConnection databaseConnection = new SQLDatabaseConnection();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbGender.getItems().addAll(genderArray);
        cbRole.getItems().addAll(roleArray);
    }

    public void navigateNext(){
        if(checkAvailability() && checkPassword()){
            anchorAccountCredentials.setVisible(false);
            anchorAccountDetails.setVisible(true);
        }
    }


    public void navigateBack(){
        anchorAccountCredentials.setVisible(true);
        anchorAccountDetails.setVisible(false);
    }

    public void backToLogin(ActionEvent actionEvent) throws IOException {
        Parent root = null;
        root = FXMLLoader.load(getClass().getResource("../View/LoginPage.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }

    public void checkAvailabilityAction(){
        if(checkAvailability()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("User available");
            alert.setHeaderText(null);
            alert.setContentText("The username: "+txtUsername.getText()+" is available");
            alert.showAndWait();
        }
    }

    public boolean checkAvailability(){
        if(txtUsername.getText().length()<8){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Username too short");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a username with length longer than 7");
            alert.showAndWait();
            return false;
        }else if(txtUsername.getText().length()>20){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Username too long");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a username with length no longer than 20");
            alert.showAndWait();
            return false;
        }

        if(databaseConnection.checkAvailability(txtUsername.getText())){
            return true;
        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("User unavailable");
            alert.setHeaderText(null);
            alert.setContentText("The username: "+txtUsername.getText()+" is unavailable");
            alert.showAndWait();
            return false;
        }


    }

    public boolean checkPassword(){

        if(this.txtPassword.getLength()==0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password field empty");
            alert.setHeaderText(null);
            alert.setContentText("Password is necessary for account creation");
            alert.showAndWait();
            return false;
        } else if(this.txtPassword.getLength()<12){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect password length");
            alert.setHeaderText(null);
            alert.setContentText("Password length must be more than 12");
            alert.showAndWait();
            return false;
        } else if(this.txtPassword.getLength()>25){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password length too long");
            alert.setHeaderText(null);
            alert.setContentText("Password length must be less than 25");
            alert.showAndWait();
            return false;
        }


        if(!txtPassword.getText().equals(txtRePassword.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Password and re-enter password does not match, please reenter the password");
            alert.showAndWait();
            return false;
        } else{
            return true;
        }

    }



    public void registerButtonListener(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = null;
        String gender = null;



        //Input validation
        if(txtName.getText().length() == 0 ){
            alert.setTitle("Name input empty");
            alert.setHeaderText(null);
            alert.setContentText("Name cannot be empty, please enter your real name");
            alert.showAndWait();
            return;
        }

        if(cbGender.getValue() == null){
            alert.setTitle("Gender input empty");
            alert.setHeaderText(null);
            alert.setContentText("Gender cannot be empty, please select your gender.");
            alert.showAndWait();
            return;
        } else if(cbGender.getValue().equals("Male")){
            gender = "M";
        } else if(cbGender.getValue().equals("Female")){
            gender = "F";
        }


        if(dpDOB.getValue() != null){
            date = dpDOB.getValue().format(dtf);
        } else {
            alert.setTitle("Date of birth input empty");
            alert.setHeaderText(null);
            alert.setContentText("Date of birth cannot be empty, please select your date of birth.");
            alert.showAndWait();
            return;
        }

        if(cbRole.getValue() == null){
            alert.setTitle("Role input empty");
            alert.setHeaderText(null);
            alert.setContentText("Role cannot be empty, please select your account role");
            alert.showAndWait();
            return;
        }

        if(txtAddress.getText().length()>99){
            alert.setTitle("Limit exceeded");
            alert.setHeaderText(null);
            alert.setContentText("Address length must no exceed 100");
            alert.showAndWait();
            return;
        }
        if(txtContact.getText().length()>15){
            alert.setTitle("Limit exceeded");
            alert.setHeaderText(null);
            alert.setContentText("Contact length must no exceed 15");
            alert.showAndWait();
            return;
        }
        try{
            Double.parseDouble(txtContact.getText());


        } catch(NumberFormatException nfe) {
            alert.setTitle("Contact format invalid");
            alert.setHeaderText(null);
            alert.setContentText("Contact must only contain numeric characters");
            alert.showAndWait();
            return;
        }

        if(txtIC.getText().length() != 12 || txtIC.getText().contains("-") ||txtIC.getText().contains(".")){
            alert.setTitle("IC input empty/invalid");
            alert.setHeaderText(null);
            alert.setContentText("IC length must be 12 and should only contain numbers");
            alert.showAndWait();
            return;
        }

        try{
            Double.parseDouble(txtIC.getText());
        } catch(NumberFormatException nfe) {
            alert.setTitle("IC format invalid");
            alert.setHeaderText(null);
            alert.setContentText("IC must only contain numeric characters/");
            alert.showAndWait();
            return;
        }



        databaseConnection.registerUser(
                txtUsername.getText(),
                txtPassword.getText(),
                txtName.getText(),
                txtAddress.getText(),
                cbRole.getValue(),
                gender,
                date,
                txtIC.getText(),
                txtContact.getText(),
                txtEmail.getText()
        );
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration complete!");
        alert.setHeaderText(null);
        alert.setContentText("Registration success, now redirecting back to login page");
        alert.showAndWait();
        btnBackToLogin.fire();

    }









}
