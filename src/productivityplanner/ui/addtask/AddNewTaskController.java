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
import productivityplanner.database.DataAssistant;
import productivityplanner.database.DatabaseHandler;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

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

    // Create DatabaseHandler
    DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = new DatabaseHandler();

        checkData();
    }

    @FXML
    private void addNewTask(ActionEvent event){
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

        String action = "INSERT INTO TASK VALUES ("+
                "'"+ taskName + "'," +
                "'"+ taskColour + "'," +
                ""+ "false" + "," +
                "'"+ LocalDate.now() + "'" +
                ")";
        System.out.println(action); // DEBUG

        if (databaseHandler.executeAction(action)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success.");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed.");
            alert.showAndWait();
            System.out.println("Error: Unable to execute action.");
        }

        Task task = new Task(LocalDate.now(), taskName, taskColour);
        boolean result = DataAssistant.insertNewTask(task);
        if (result) {
            System.out.println("Task Added.");
        }else {
            System.out.println("Unable to add task.");
        }
    }

    private void checkData(){
        String query = "SELECT colour FROM TASK";
        ResultSet results = databaseHandler.executeQuery(query);
        try{
            while(results.next()){
                String name = results.getString("colour");
                System.out.println(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void addCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}

