package Controller;

import Model.AttachmentTableModel;
import Model.ExaminationDialogTableModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

public class AttachmentDialog implements Initializable {

    private static ObservableList<AttachmentTableModel> attachmentItems;
    private ObservableList<AttachmentTableModel> items;
    private Stage stage;
    private Scene scene;

    @FXML
    private TableColumn<AttachmentTableModel,String> colAttachmentName,colNotes,colSize,colFilePath;
    @FXML
    private TableView<AttachmentTableModel> tblAttachments;
    @FXML
    private Button btnClearAll, btnRemove, btnAdd, btnConfirm, btnBack;

    public AttachmentDialog(ObservableList<AttachmentTableModel> items){
        this.items = FXCollections.observableArrayList();
        this.items.addAll(items);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //Table
        colAttachmentName.setCellValueFactory(new PropertyValueFactory<>("attachmentName"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colNotes.setCellFactory(TextFieldTableCell.forTableColumn());
        tblAttachments.setItems(items);
        tblAttachments.setEditable(true);



        //Button set on action
        btnAdd.setOnAction(this::addFile);
        btnConfirm.setOnAction(this::confirmAction);
        btnRemove.setOnAction(this::removeFile);
        btnClearAll.setOnAction(this::removeAll);
        btnBack.setOnAction(this::backAction);


    }

    public ObservableList<AttachmentTableModel> display(AttachmentDialog ad){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/AttachmentDialog.fxml"));
        fxmlLoader.setController(ad);
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

        return attachmentItems;

    }

    public void addFile(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);


        if(selectedFile != null ){
            if(selectedFile.length()>10485760){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Size too large");
                alert.setHeaderText(null);
                alert.setContentText("Please select a file lesser than 10mb");
                alert.showAndWait();
                return;
            }
            String size = String.format("%.2f", selectedFile.length()/1024f/1024f) + " mb";
            String path = selectedFile.getPath();
            items.add(new AttachmentTableModel(selectedFile.getName(),size,"",path));

        }
    }

    public void confirmAction(ActionEvent actionEvent){
        attachmentItems = tblAttachments.getItems();
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }

    public void removeFile(ActionEvent actionEvent){
        AttachmentTableModel selectedItem = tblAttachments.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            items.remove(selectedItem);
        }
    }

    public void removeAll(ActionEvent actionEvent){
        items.removeAll(tblAttachments.getItems());
    }

    public void backAction(ActionEvent actionEvent){
        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }


}
