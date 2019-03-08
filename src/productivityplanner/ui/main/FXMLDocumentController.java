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
import java.time.YearMonth;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarPane.getChildren().add(new Calendar(YearMonth.now()).getView());
    }
}
