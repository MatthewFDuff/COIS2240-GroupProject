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
import productivityplanner.ui.main.Calendar;
import productivityplanner.ui.main.FXMLDocumentController;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // Create DatabaseHandler
    DatabaseHandler databaseHandler;
    Task task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();

        task = getFXMLController().getSelectedTask();
        if (task != null)
        {
            txtName.setText(task.getName());
            colourPicker.setValue(task.getColor());
        }
    }

    @FXML
    private void editTask(ActionEvent event){
        // Get the task which is selected for edit
        // Get information from the form
        String taskName = txtName.getText();
        Color taskColour = colourPicker.getValue();

        if (taskName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter in all fields.");
            alert.showAndWait();
            System.out.println("Error: The task was not given a name.");
            return;
        }

        if (databaseHandler.updateTask(taskName, taskColour.toString(), task)) {
            cancel(new ActionEvent());
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed.");
            alert.showAndWait();
            System.out.println("Error: Unable to execute action.");
        }
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}

