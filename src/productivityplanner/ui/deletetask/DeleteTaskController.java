package productivityplanner.ui.deletetask;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHelper;
import productivityplanner.ui.taskcell.TaskCellController;

import java.net.URL;
import java.util.ResourceBundle;

import static productivityplanner.ui.main.Main.getFXMLController;

public class DeleteTaskController implements Initializable {
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXColorPicker colourPicker;
    @FXML
    private BorderPane rootPane;

    Task task;                                               // Focused task up for deletion

    @Override
    // Setup the delete window
    public void initialize(URL location, ResourceBundle resources) {
        task = TaskCellController.getSelected().getTask();           // Set focused task
        if (task != null)                                            // If the task exists...
        {
            txtName.setText(task.getName());                         // Update the Edit form to display the task's pre-edit information.
            colourPicker.setValue(task.getColor());
            colourPicker.setOpacity(1.0);                            //Since the colour picker isn't in use, it fades the colour
        }                                                            //This line counteracts that change
    }

    @FXML
    // Runs once the confirm delete is pressed
    private void deleteTask(ActionEvent event) {


        if (DatabaseHelper.deleteTask(task)) {              // Update the task's information in the database.
            cancel(new ActionEvent());                      // Close the window when the task has been updated.
        }else {                                             // If It can not delete task
            Alert alert = new Alert(Alert.AlertType.ERROR); // Create Alert for failing to delete
            alert.setHeaderText(null);
            alert.setContentText("Failed to delete task.");
            alert.showAndWait();
            System.out.println("Error: Unable to delete task.");
        }
    }

    @FXML
    // Closes the window, Runs if cancel is hit and after deletion
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow(); // Get access to the window
        stage.close();                                         // Close the window
    }
}
