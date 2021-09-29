package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedicationTableModel {
    private final StringProperty medicationName   = new SimpleStringProperty();
    private final StringProperty dose   = new SimpleStringProperty();
    private final StringProperty date   = new SimpleStringProperty();
    private final StringProperty notes   = new SimpleStringProperty();

    public MedicationTableModel(String name, String dose, String date, String notes){
        this.medicationName.set(name);
        this.dose.set(dose);
        this.date.set(date);
        this.notes.set(notes);
    }

    public StringProperty medicationNameProperty(){return medicationName;}
    public StringProperty doseProperty(){return dose;}
    public StringProperty dateProperty(){return date;}
    public StringProperty notesProperty(){return notes;}


    @Override
    public String toString() {
        return medicationName.getValue() + "_split_" +
                dose.getValue() + "_split_" +
                date.getValue() + "_split_" +
                notes.getValue() + "_,_";
    }




}
