package Controller;


import Model.ExaminationDialogTableModel;
import Model.MedicationTableModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ExaminationDialog implements Initializable {

    private Stage stage;
    private Scene scene;
    private String doctorID;
    private static String examinationString = "";

    @FXML
    private TableView<ExaminationDialogTableModel> tblExamination;
    @FXML
    private TableColumn<ExaminationDialogTableModel,String> colBodyComponent,colDescription;
    @FXML
    private TableColumn<ExaminationDialogTableModel,Boolean> colSelect;
    @FXML
    private TableColumn<ExaminationDialogTableModel,Void> colDefault;


    @FXML
    private Button btnConfirm,btnClearAll,btnBack;

    private ObservableList<ExaminationDialogTableModel> items;


    public ExaminationDialog(String doctorID) {
        this.doctorID = doctorID;
        items = FXCollections.observableArrayList();
    }

    public String display(ExaminationDialog e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/ExaminationDialog.fxml"));
        fxmlLoader.setController(e);
        Parent parent = fxmlLoader.load();
        examinationString = "";


        scene = new Scene(parent);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.showAndWait();

        return examinationString;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Table
        colSelect.setCellValueFactory( new PropertyValueFactory<>( "select" ));
        colSelect.setCellFactory( tc -> new CheckBoxTableCell<>());
        colBodyComponent.setCellValueFactory( new PropertyValueFactory<>( "bodyComponent" ));
        colDefault.setCellFactory(cellFactory);

        colDescription.setCellValueFactory( new PropertyValueFactory<>( "description" ));
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setPrefWidth(406);

        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        List<ExaminationDialogTableModel> examniationList = sc.getExaminationList();
        items.addAll(examniationList);
        tblExamination.setItems(items);

        //Set on action
        btnConfirm.setOnAction(this::confirmAction);





    }

    Callback<TableColumn<ExaminationDialogTableModel, Void>, TableCell<ExaminationDialogTableModel, Void>> cellFactory = new Callback<TableColumn<ExaminationDialogTableModel, Void>, TableCell<ExaminationDialogTableModel, Void>>() {
        @Override
        public TableCell<ExaminationDialogTableModel, Void> call(final TableColumn<ExaminationDialogTableModel, Void> param) {
            final TableCell<ExaminationDialogTableModel, Void> cell = new TableCell<ExaminationDialogTableModel, Void>() {

                private final Button btn = new Button("Set");

                {
                    btn.setOnAction((ActionEvent event) -> {
                            items.get(getIndex()).selectProperty().setValue(true);
                            items.get(getIndex()).descriptionProperty().setValue(
                                    items.get(getIndex()).defaultDescriptionProperty().getValue()
                            );
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };
            return cell;
        }
    };

    public void confirmAction(ActionEvent actionEvent){
        final Set<ExaminationDialogTableModel> selectedItems = new HashSet<>();
        for(final ExaminationDialogTableModel obj : tblExamination.getItems()){
            if(obj.selectProperty().get()){
                examinationString +=obj.bodyComponentProperty().getValue() + " - " + obj.descriptionProperty().getValue() + "\n";
            }
        }

        Stage currentStage = (Stage) btnConfirm.getScene().getWindow();
        currentStage.close();
    }











}
