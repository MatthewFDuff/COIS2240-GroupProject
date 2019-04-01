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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.database.DatabaseHelper;

import java.net.URL;
import java.util.ResourceBundle;

import static productivityplanner.ui.main.Main.getFXMLController;

public class EditTaskController implements Initializable {

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
    JFXButton btnSubmit;

    Task task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        task = getFXMLController().getSelectedTask();

        if (task != null)
        {
            // Update the Edit form to display the task's pre-edit information.
            txtName.setText(task.getName());
            colourPicker.setValue(task.getColor());
        }
    }

    @FXML
    private void editTask(ActionEvent event){
        // Get task information from the form.
        String taskName = txtName.getText();
        Color taskColour = colourPicker.getValue();

        // Validate the information before editing.
        if (taskName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields.");
            alert.showAndWait();
            System.out.println("Error: The task was not given a name.");
            return;
        }

        // Update the task's information in the database.
        if (DatabaseHelper.updateTask(taskName, taskColour.toString(), task)) {
            cancel(new ActionEvent()); // Close the window when the task has been updated.
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed.");
            alert.showAndWait();
            System.out.println("Error: Unable to execute action.");
        }
    }

    @FXML
    // Close the window.
    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
