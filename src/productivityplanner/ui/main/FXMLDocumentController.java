package productivityplanner.ui.main;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static productivityplanner.ui.main.Main.getFXMLController;

public class FXMLDocumentController implements Initializable{

    @FXML
    Label lblProgramAction;
    @FXML
    AnchorPane calendarPane; // Pane to create the calendar
    @FXML
    JFXListView uncompletedTasks; // Pane to add new to-do tasks
    @FXML
    JFXListView completedTasks; // Pane to add completed tasks
    @FXML
    Label lblJournalDate; // The title of each journal entry is displayed here


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();
        // Generate the calendar
        calendarPane.getChildren().add(new Calendar(date).getView());
        updateSelectedDate(Calendar.selectedDay); // Highlight the current date.
        updateJournalTitle();
    }

    @FXML
    private void updateJournalTitle() {
        lblJournalDate.setText(Calendar.selectedDay.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")));
    }


    public void setSelectedDay(Day date){
        System.out.println("The date of this day is: " + date.getDate());
        // Create a border around the currently selected day
        updateSelectedDate(date);
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
            // UPDATE JOURNAL
            updateJournalTitle();
            // UPDATE TASKS
        }
        else
        {
            System.out.println("Unable to update selected date.");
        }
    }

    public void updateJournal(LocalDate date){
        // Update title based on the selected date
        lblJournalDate.setText(date.toString());
    }
}
