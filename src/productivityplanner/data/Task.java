package productivityplanner.data;

import java.awt.*;
import java.time.LocalDate;
import javafx.scene.paint.Color;

public class Task {
    // TODO: Create a FXML file for a Task
    // TODO: Add Task Events (Delete, Edit, etc).

    // Fields:
    private LocalDate date;
    private String name;
    private Color color;
    private boolean isCompleted;

    public Task(LocalDate dateCreated, String taskName, Color taskColor)
    {
        this.date = dateCreated;
        this.name = taskName;
        this.color = taskColor;
        this.isCompleted = false;
    }

    // GET/SET
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean getCompleted(){ return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
