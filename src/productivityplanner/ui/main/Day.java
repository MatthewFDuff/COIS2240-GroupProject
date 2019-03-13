package productivityplanner.ui.main;

import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class Day extends AnchorPane {
    private LocalDate date;
    private boolean isWeekend;
    private int numberOfTasks;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> daySelected());
    }

    public void daySelected(){
        System.out.println("The date of this day is: " + date);
        // Create a border around the currently selected day
        updateSelectedDate(this);
    }

    public void updateSelectedDate(Day currentSelectedDay){
        if (currentSelectedDay != null)
        {
            // HIGHLIGHT/BORDER
            // Clear the border from the current selected date.
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(2))));
            // Change the currently selected day to the one that's just been clicked on
            Calendar.selectedDay = currentSelectedDay;
            // Add a border to the new selected date
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.web("#292929"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));

        }
        else
        {
            System.out.println("Unable to update selected date.");
        }
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public void setWeekend(boolean weekend) {
        isWeekend = weekend;
    }
}
