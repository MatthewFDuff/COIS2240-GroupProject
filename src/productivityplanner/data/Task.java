package productivityplanner.data;

import java.time.LocalDate;
import javafx.scene.paint.Color;

// Represents a task for the user to complete.
public class Task {
    private LocalDate date;         // The date the task was created.
    private String name;            // The name of the task (eg. Laundry, Do Dishes, etc).
    private Color color;            // The colour of the task's GUI.
    private boolean isCompleted;    // The completion status of the task (Done, Not Done).

    public Task(LocalDate dateCreated, String taskName, Color taskColor)
    {
        this.date = dateCreated;
        this.name = taskName;
        this.color = taskColor;
        this.isCompleted = false;
    }

    public Task(LocalDate dateCreated, String taskName, Color taskColor, boolean complete)
    {
        this.date = dateCreated;
        this.name = taskName;
        this.color = taskColor;
        this.isCompleted = complete;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean getCompleted(){ return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
