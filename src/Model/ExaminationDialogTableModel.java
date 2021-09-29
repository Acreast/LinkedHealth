package Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExaminationDialogTableModel {
    private final BooleanProperty select = new SimpleBooleanProperty();
    private final StringProperty bodyComponent   = new SimpleStringProperty();
    private final StringProperty description   = new SimpleStringProperty();
    private final StringProperty defaultDescription = new SimpleStringProperty();



    public ExaminationDialogTableModel( boolean select, String bodyComponent,String defaultDescription,String description) {
        this.select.set( select  );
        this.bodyComponent.set(bodyComponent);
        this.defaultDescription.setValue(defaultDescription);
        this.description.set( description );
    }

    public BooleanProperty  selectProperty  () {return select;}
    public StringProperty  bodyComponentProperty  () {return bodyComponent;}
    public StringProperty defaultDescriptionProperty () {return defaultDescription;}
    public StringProperty descriptionProperty() {return description;}
}