package productivity.planner.ui.tasklist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import productivity.planner.data.Task;
import productivity.planner.database.DatabaseHandler;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TaskListController implements Initializable {

    ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    private void loadData() {
        taskList.clear();
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        String query = "SELECT * FROM TASK";
        ResultSet results = databaseHandler.executeQuery(query);
        try{
            while(results.next()){
                String name = results.getString("name");
                String color = results.getString("colour");
                Boolean compl = results.getBoolean("isComplete");
                String date = results.getString("date");

                taskList.add(new Task(LocalDate.parse(date), name, Color.web(color), compl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
