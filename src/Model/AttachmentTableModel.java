package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttachmentTableModel {
    private final StringProperty attachmentName   = new SimpleStringProperty();
    private final StringProperty size   = new SimpleStringProperty();
    private final StringProperty notes   = new SimpleStringProperty();
    private final StringProperty filePath = new SimpleStringProperty();


    public AttachmentTableModel(String attachmentName,String size,String notes,String filePath) {
        this.attachmentName.set(attachmentName);
        this.size.set(size);
        this.notes.set(notes);
        this.filePath.set(filePath);
    }

    public StringProperty attachmentNameProperty(){return attachmentName;}
    public StringProperty sizeProperty(){return size;}
    public StringProperty notesProperty(){return notes;}
    public StringProperty filePathProperty(){return filePath;}

    @Override
    public String toString() {
        return attachmentName.getValue() + "_split_" +
                size.getValue() + "_split_" +
                notes.getValue() + "_split_" +
                filePath.getValue() + "_,_";
    }
}
