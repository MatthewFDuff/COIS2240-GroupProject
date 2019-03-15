package productivityplanner.ui.tasklist;

import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.ui.main.FXMLDocumentController;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static productivityplanner.ui.main.Main.getFXMLController;

public class TaskListController implements Initializable {

    ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;

//    JFXListView<Task> completedTaskList;
//    JFXListView<Task> uncompletedTaskList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        completedTaskList = getFXMLController().getCompletedTaskList();
//        uncompletedTaskList = getFXMLController().getUncompletedTaskList();

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
                System.out.println(name + " " + color + " " + compl + " " + date);

                taskList.add(new Task(LocalDate.parse(date), name, Color.web(color), compl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        for(Task task : taskList){
//            if (task.getCompleted()){
//                completedTaskList.getItems().add(task);
//            } else {
//                uncompletedTaskList.getItems().add(task);
//            }
//        }
    }
}
