package Model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PatientConnectionTableModel {
    private final StringProperty name   = new SimpleStringProperty();
    private final StringProperty id   = new SimpleStringProperty();
    private final StringProperty date   = new SimpleStringProperty();
    private final StringProperty status   = new SimpleStringProperty();


    public PatientConnectionTableModel(String id,String name, String date, String status) {
        this.id.set(id);
        this.name.set(name);
        this.date.set(date);
        this.status.set(status);
    }

    public StringProperty  idProperty  () { return id;   }
    public StringProperty  nameProperty  () { return name;   }
    public StringProperty  dateProperty  () { return date;   }
    public StringProperty  statusProperty  () { return status;   }




}



