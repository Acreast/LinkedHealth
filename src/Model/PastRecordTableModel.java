package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PastRecordTableModel {

    private final StringProperty patientID = new SimpleStringProperty();
    private final StringProperty date   = new SimpleStringProperty();
    private final StringProperty reasonOfVisit   = new SimpleStringProperty();
    private final StringProperty timeStamp = new SimpleStringProperty();
    private final StringProperty patientName = new SimpleStringProperty();


    public PastRecordTableModel(String patientID, String reasonOfVisit,String date, String timeStamp,String patientName){
        this.patientID.set(patientID);
        this.date.set(date);
        this.reasonOfVisit.set(reasonOfVisit);
        this.timeStamp.set(timeStamp);
        this.patientName.set(patientName);

    }

    public StringProperty dateProperty(){return date;}
    public StringProperty patientIDProperty(){return patientID;}
    public StringProperty patientNameProperty(){return patientName;}
    public StringProperty reasonOfVisitProperty(){return reasonOfVisit;}
    public StringProperty timeStampProperty(){return timeStamp;}
}
