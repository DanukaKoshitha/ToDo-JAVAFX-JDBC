package model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TaskObject {
    private SimpleStringProperty taskName;
    private SimpleStringProperty description;
    private SimpleStringProperty dueDate;
    private SimpleBooleanProperty completed;

    public TaskObject(String taskName, String description, String dueDate) {
        this.taskName = new SimpleStringProperty(taskName);
        this.description = new SimpleStringProperty(description);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.completed = new SimpleBooleanProperty(false);
    }


    public Property<Boolean> completedProperty() {
        return completed;
    }
}
