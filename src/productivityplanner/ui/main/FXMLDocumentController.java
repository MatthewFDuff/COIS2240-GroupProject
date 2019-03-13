package productivityplanner.ui.main;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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
        lblJournalDate.setText(Calendar.FindDay(LocalDate.now()).getDate().format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")));
    }


    @FXML
    public void updateJournal(LocalDate date){
        // Update title based on the selected date
        lblJournalDate.setText(date.toString());
    }
}
