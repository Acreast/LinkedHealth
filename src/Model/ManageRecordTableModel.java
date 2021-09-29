package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManageRecordTableModel {

    private final StringProperty date   = new SimpleStringProperty();
    private final StringProperty reasonOfVisit   = new SimpleStringProperty();
    private final StringProperty timeStamp = new SimpleStringProperty();

    public ManageRecordTableModel(String date, String reasonOfVisit, String timeStamp){
        this.date.set(date);
        this.reasonOfVisit.set(reasonOfVisit);
        this.timeStamp.set(timeStamp);
    }

    public StringProperty dateProperty(){return date;}
    public StringProperty reasonOfVisitProperty(){return reasonOfVisit;}
    public StringProperty timeStampProperty(){return timeStamp;}
}
