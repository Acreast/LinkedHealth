package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedicationListTableModel {

    private final StringProperty medicationName   = new SimpleStringProperty();
    private final StringProperty description   = new SimpleStringProperty();

    public MedicationListTableModel(String name, String description){
        this.medicationName.set(name);
        this.description.set(description);
    }

    public StringProperty medicationNameProperty(){return medicationName;}
    public StringProperty descriptionProperty(){return description;}



}
