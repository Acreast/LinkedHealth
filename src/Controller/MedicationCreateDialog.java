package Controller;

import Model.MedicationTableModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class MedicationCreateDialog implements Initializable {

    private static MedicationTableModel item;
    private String medicationName;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtMedicationName, txtDose;
    @FXML
    private TextArea txtNotes;
    @FXML
    private DatePicker dpTakeUntil;
    @FXML
    private Button btnConfirm,btnCancel;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public MedicationCreateDialog(String medicationName) {
        this.medicationName = medicationName;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtMedicationName.setText(medicationName);
        txtDose.setText("");
        txtNotes.setText("");
        btnConfirm.setOnAction(this::confirmAction);
        btnCancel.setOnAction(this::cancelAction);

    }

    public MedicationTableModel display(MedicationCreateDialog mcd){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/MedicationCreateDialog.fxml"));
        fxmlLoader.setController(mcd);
        Parent parent = null;
        try {
            parent = fxmlLoader.load();

            scene = new Scene(parent);
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    public void confirmAction(ActionEvent actionEvent){

        if(txtMedicationName.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Medication field empty");
            alert.setHeaderText(null);
            alert.setContentText("Please specify the name of the medication.");
            alert.showAndWait();
            return;
        }

        if(txtDose.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dose field empty");
            alert.setHeaderText(null);
            alert.setContentText("Please specify the dosage of the medication.");
            alert.showAndWait();
            return;
        }

        if(dpTakeUntil.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Date field empty");
            alert.setHeaderText(null);
            alert.setContentText("Please specify until when the medication is taken.");
            alert.showAndWait();
            return;
        }
        Date date = Calendar.getInstance().getTime();

        if(dpTakeUntil.getValue().isBefore(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Date input invalid");
            alert.setHeaderText(null);
            alert.setContentText("Date cannot be before today's date.");
            alert.showAndWait();
            return;
        }

        String notes = "";
        if(txtNotes.equals("") || txtNotes.getText().length()<1){
            notes = "None";
        } else {
            notes = txtNotes.getText();
        }



        item = new MedicationTableModel(txtMedicationName.getText(),txtDose.getText(),dpTakeUntil.getValue().format(dtf),notes);

        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }

    public void cancelAction(ActionEvent actionEvent){
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }


}
