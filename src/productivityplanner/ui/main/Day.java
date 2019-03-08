package productivityplanner.ui.main;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class Day extends AnchorPane {
    private LocalDate date;
    private int numberOfTasks;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> System.out.println("The date of this day is: " + date));
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }
}
