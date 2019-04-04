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

    Task task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        task = TaskCellController.getSelected().getTask();
        if (task != null)
        {
            txtName.setText(task.getName());
            colourPicker.setValue(task.getColor());
            colourPicker.setOpacity(1.0);
        }
    }

    @FXML
    private void deleteTask(ActionEvent event) {

        // Update the task's information in the database.
        if (DatabaseHelper.deleteTask(task)) {
            cancel(new ActionEvent()); // Close the window when the task has been updated.
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed.");
            alert.showAndWait();
            System.out.println("Error: Unable to delete task.");
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
