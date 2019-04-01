package productivityplanner.ui.deletetask;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;

import java.net.URL;
import java.util.ResourceBundle;

import static productivityplanner.ui.main.Main.getFXMLController;

public class DeleteTaskController implements Initializable {

    @FXML
    private Pane namePane;
    @FXML
    private Pane titlePane;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private VBox vbox;
    @FXML
    private Label lblName;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXColorPicker colourPicker;
    @FXML
    private Label lblColour;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label title;
    @FXML
    private Pane colourPane;
    @FXML
    private JFXButton btnConfirm;

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



    //TODO: create the actual delete stuff
    @FXML
    private void deleteTask(ActionEvent event) {
        databaseHandler.deleteTask(task);

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

}
