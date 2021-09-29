package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DiagnosisListTableModel {

    private final StringProperty diagnosisName   = new SimpleStringProperty();
    private final StringProperty diagnosisDescription   = new SimpleStringProperty();

    public DiagnosisListTableModel( String diagnosisName,String diagnosisDescription ) {

        this.diagnosisName.set(diagnosisName);
        this.diagnosisDescription.set( diagnosisDescription );
    }

    public StringProperty diagnosisNameProperty  () {return diagnosisName;}
    public StringProperty  diagnosisDescriptionProperty  () {return diagnosisDescription;}

}
