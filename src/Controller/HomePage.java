package Controller;


import Model.Patient;
import Model.UserLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePage implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    Label lblGreet;
    @FXML
    Button btnViewProfile, btnViewRecord, btnManageConnection, btnLogOut;
    @FXML
    ImageView imgViewBtnIcon;

    public UserLogin user;

    public HomePage(UserLogin user) {
        this.user = user;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblGreet.setText("Welcome, " + user.getName());
        btnLogOut.setOnAction(this::backToLogin);
        btnViewProfile.setOnAction(this::toViewProfilePage);
        btnManageConnection.setText("Manage\n Connections");
        btnManageConnection.setOnAction(this::toManageConnectionPage);
        btnViewRecord.setOnAction(this::toViewRecordPage);

        if(user.getUserRole().equals("Researcher")){
            imgViewBtnIcon.setVisible(false);
            btnManageConnection.setDisable(true);
            btnManageConnection.cursorProperty().set(Cursor.DEFAULT);
            btnManageConnection.setText("");
        }
    }




    public void backToLogin(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logging out");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to log out?");

        if (alert.showAndWait().get()== ButtonType.OK){
            Parent root = null;

            try {
                root = FXMLLoader.load(getClass().getResource("../View/LoginPage.fxml"));
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

                scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void toViewRecordPage(ActionEvent actionEvent){
        SQLDatabaseConnection sc = new SQLDatabaseConnection();

        if (user.getUserRole().equals("Patient")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ManageRecord.fxml"));
            ManageRecord mr = new ManageRecord(user,"");
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

        } else if(user.getUserRole().equals("Doctor")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ViewPastRecord.fxml"));
            DoctorViewPastRecord dvpr = new DoctorViewPastRecord(user);
            loader.setController(dvpr);
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
        } else if(user.getUserRole().equals("Researcher")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ViewPastRecord.fxml"));
            ResearcherViewRecordList rvrl = new ResearcherViewRecordList(user);
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

    }

    public void toViewProfilePage(ActionEvent actionEvent){
        if (user.getUserRole().equals("Patient")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/PatientViewProfile.fxml"));
            PatientViewProfile pvp = new PatientViewProfile(user);
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

        } else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/GeneralProfile.fxml"));
            GeneralProfile gp = new GeneralProfile(user);
            loader.setController(gp);
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

    public void toManageConnectionPage(ActionEvent actionEvent){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/ManageConnection.fxml"));

        if (user.getUserRole().equals("Patient")){

            PatientManageConnection pmc = new PatientManageConnection(user);
            loader.setController(pmc);
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

        } else if(user.getUserRole().equals("Doctor")){

            DoctorManageConnection dmc = new DoctorManageConnection(user);
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
        }


    }

    public void toManageRecordPage(ActionEvent actionEvent){

    }









}
