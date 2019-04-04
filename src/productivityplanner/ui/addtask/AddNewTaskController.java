package productivityplanner.ui.addtask;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHelper;
import productivityplanner.ui.main.Calendar;

import java.net.URL;
import java.util.ResourceBundle;

import static productivityplanner.ui.main.Main.getFXMLController;

public class AddNewTaskController implements Initializable {

    @FXML
    BorderPane rootPane;
    @FXML
    VBox vbox;
    @FXML
    Pane titlePane;
    @FXML
    Pane namePane;
    @FXML
    Pane colourPane;
    @FXML
    Label title;
    @FXML
    Label lblName;
    @FXML
    JFXTextField txtName;
    @FXML
    Label lblColour;
    @FXML
    JFXColorPicker colourPicker;
    @FXML
    JFXButton btnAddTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    // Adds a new task to the uncompleted task list.
    private void addNewTask(ActionEvent event){

        String taskName = txtName.getText();                        // Get information from the form.
        Color taskColour = colourPicker.getValue();

        if (taskName.isEmpty()){                                    // Validate the data.
            Alert alert = new Alert(Alert.AlertType.ERROR);         // Create an alerts for fields not being complete
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields.");
            alert.showAndWait();
            System.out.println("Error: The task was not given a name.");
            return;
        }

        Task task = new Task(Calendar.selectedDay.getDate(), taskName, taskColour); // Create task out of data.

        // Insert the task.
        if (DatabaseHelper.insertTask(task)) {  // Insert the task into the database.
            getFXMLController().loadTasks();    // Reload the task lists so the new task is displayed.
            addCancel(new ActionEvent());       // Close the window after successfully adding the task.
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert for being unable to add the task
            alert.setHeaderText(null);
            alert.setContentText("Unable to add new task.");
            alert.showAndWait();
            System.out.println("Error: Unable to add task.");
        }
    }

    @FXML
    // Closes the stage when the user presses the cancel button.
    public void addCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow(); // Get access to the window
        stage.close();                                         // Close the window
    }
}

