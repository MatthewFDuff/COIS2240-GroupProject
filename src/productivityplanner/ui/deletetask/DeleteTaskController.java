package productivityplanner.ui.deletetask;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHelper;

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
        task = getFXMLController().getSelectedTask();
        if (task != null)
        {
            txtName.setText(task.getName());
            colourPicker.setValue(task.getColor());
        }
    }

    @FXML
    private void deleteTask(ActionEvent event) {
        DatabaseHelper.deleteTask(task);

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
