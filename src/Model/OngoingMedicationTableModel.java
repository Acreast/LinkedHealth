package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OngoingMedicationTableModel {

    private final StringProperty medicationName   = new SimpleStringProperty();
    private final StringProperty takeUntil   = new SimpleStringProperty();
    private final StringProperty dose   = new SimpleStringProperty();
    private final StringProperty dateAdded   = new SimpleStringProperty();
    private final StringProperty addedBy   = new SimpleStringProperty();
    private final StringProperty notes   = new SimpleStringProperty();

    public OngoingMedicationTableModel(String medicationName,String takeUntil, String dose, String dateAdded,String addedBy,String notes) {
        this.medicationName.set(medicationName);
        this.takeUntil.set(takeUntil);
        this.dose.set(dose);
        this.dateAdded.set(dateAdded);
        this.notes.set(notes);
        this.addedBy.set(addedBy);
    }

    public StringProperty medicationNameProperty(){return medicationName;}
    public StringProperty doseProperty(){return dose;}
    public StringProperty takeUntilProperty(){return takeUntil;}
    public StringProperty dateAddedProperty(){return dateAdded;}
    public StringProperty notesProperty(){return notes;}
    public StringProperty addedByProperty(){return addedBy;}


}
