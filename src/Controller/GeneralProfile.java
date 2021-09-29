package Controller;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GeneralProfile implements Initializable {

    private UserLogin loginSession;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtName,txtGender,txtIC,txtContact,txtEmail,txtAddress;
    @FXML
    private Button btnUpdate,btnChangePW,btnBack;
    @FXML
    private DatePicker dpDOB;

    public GeneralProfile(UserLogin loginSession) {
        this.loginSession = loginSession;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtName.setText(loginSession.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dpDOB.setValue(LocalDate.parse(loginSession.getDOB(),dtf));
        txtGender.setEditable(false);
        if(loginSession.getGender().equals("M")){
            txtGender.setText("Male");
        } else {
            txtGender.setText("Female");
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

        txtIC.setText(loginSession.getUserIC());

        btnBack.setOnAction(this::backAction);
        btnUpdate.setOnAction(this::confirmAction);
        btnChangePW.setOnAction(this::changePasswordAction);


    }

    public void confirmAction(ActionEvent actionEvent){
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

        if(txtName.getText().equals("")){
            alert.setTitle("Name cannot be empty");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your name in name text field.");
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
            Integer.parseInt(txtContact.getText());
        } catch(NumberFormatException nfe) {
            alert.setTitle("Contact format invalid");
            alert.setHeaderText(null);
            alert.setContentText("Contact must only contain numeric characters");
            alert.showAndWait();
            return;
        }



        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        sc.generalUpdateProfile(loginSession.getUserID(),txtName.getText(),date,txtContact.getText(),txtEmail.getText(),txtAddress.getText());
        loginSession.setName(txtName.getText());
        loginSession.setUserEmail(txtEmail.getText());
        loginSession.setDOB(date);
        loginSession.setUserAddress(txtAddress.getText());
        loginSession.setUserContactNumber(txtContact.getText());
        loginSession.setUserEmail(txtEmail.getText());
        Alert complete = new Alert(Alert.AlertType.INFORMATION);
        complete.setTitle("Update complete");
        complete.setHeaderText(null);
        complete.setContentText("Profile has been updated");
        complete.showAndWait();
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

    public void changePasswordAction(ActionEvent actionEvent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ChangePassword.fxml"));
        ChangePassword cp = new ChangePassword(loginSession,"GeneralProfile");
        loader.setController(cp);
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


}
