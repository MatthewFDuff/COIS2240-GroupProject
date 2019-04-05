package productivityplanner.ui.edittask;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHelper;
import productivityplanner.ui.taskcell.TaskCellController;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTaskController implements Initializable {

    @FXML BorderPane rootPane;
    @FXML VBox vbox;
    @FXML Label title;
    @FXML Label lblName;
    @FXML JFXTextField txtName;
    @FXML Label lblColour;
    @FXML JFXColorPicker colourPicker;
    @FXML JFXButton btnSubmit;

    private Task task;  // Focused task up for editing

    @Override
    public void initialize(URL location, ResourceBundle resources) { // Setup the edit window.
        task = TaskCellController.getSelected().getTask();           // Set the task which is to be edited.

        if (task != null)                                            // If the task exists...
        {
            txtName.setText(task.getName());                         // Update the Edit form to display the task's pre-edit information.
            colourPicker.setValue(task.getColor());
        }
    }

    @FXML
    // Runs once the confirm edit button is pressed.
    private void editTask(ActionEvent event){

        String taskName = txtName.getText();                        // Get task information from the form.
        Color taskColour = colourPicker.getValue();

        if (taskName.isEmpty()){                                    // Validate the information before editing.
            Alert alert = new Alert(Alert.AlertType.ERROR);         // Create alert for the fields not being completed.
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields.");
            alert.showAndWait();
            System.out.println("Error: The task was not given a name.");
            return;
        }

        if (DatabaseHelper.updateTask(taskName, taskColour.toString(), task)) { // Update the task's information in the database.
            cancel(new ActionEvent());                                          // Close the window when the task has been updated.
        }else {                                                                 // If Task cannot be edited...
            Alert alert = new Alert(Alert.AlertType.ERROR);                     // Create alert for failing to edit.
            alert.setHeaderText(null);
            alert.setContentText("Failed.");
            alert.showAndWait();
            System.out.println("Error: Unable to execute action.");
        }
    }

    @FXML
    // Close the window. Runs when cancel is hit or after edits are saved.
    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow(); // Get access to the window.
        stage.close();                                         // Close the window.
    }
}