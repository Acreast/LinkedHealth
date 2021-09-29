package Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttachmentModel {
    private final StringProperty fileName   = new SimpleStringProperty();
    private final StringProperty fileSize   = new SimpleStringProperty();
    private final StringProperty fileNotes = new SimpleStringProperty();
    private final StringProperty filePath = new SimpleStringProperty();

    public AttachmentModel(String fileName, String fileSize, String fileNotes, String filePath){
        this.fileName.set(fileName);
        this.fileSize.set(fileSize);
        this.fileNotes.set(fileNotes);
        this.filePath.set(filePath);
    }

    public StringProperty fileNameProperty(){return fileName;}
    public StringProperty fileSizeProperty(){return fileSize;}
    public StringProperty fileNotesProperty(){return fileNotes;}
    public StringProperty filePathProperty(){return filePath;}
}
