package productivityplanner.ui.tasklist;

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
import productivityplanner.database.DatabaseHandler;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.ResourceBundle;

public class TaskListController implements Initializable {

    ObservableList<Task> list = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<Task> table;
    @FXML
    private TableColumn<Task, String> nameCol;
    @FXML
    private TableColumn<Task, Boolean> cbCol;
    @FXML
    private TableColumn<Task, String> colourCol;
    @FXML
    private TableColumn<Task, String> dateCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeColumns();
        loadData();
    }

    private void loadData() {
        list.clear();

        databaseHandler = new DatabaseHandler();
        String query = "SELECT * FROM TASK";
        ResultSet results = databaseHandler.executeQuery(query);
        try{
            while(results.next()){
                String name = results.getString("name");
                String color = results.getString("colour");
                Boolean compl = results.getBoolean("isComplete");
                String date = results.getString("date");
                System.out.println(name + " " + color + " " + compl + " " + date);

                list.add(new Task(name, color, compl, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(list);
    }

    private void initializeColumns() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        colourCol.setCellValueFactory(new PropertyValueFactory<>("colour"));
        cbCol.setCellValueFactory(new PropertyValueFactory<>("isComplete"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    public static class Task{
        private final SimpleStringProperty name;
        private final SimpleStringProperty colour;
        private final SimpleStringProperty complete;
        private final SimpleStringProperty date;

        public Task(String n, String colour, boolean b, String date){
            this.name = new SimpleStringProperty(n);
            this.colour = new SimpleStringProperty(colour);
            this.date = new SimpleStringProperty(date);

            if (b){
                complete = new SimpleStringProperty("Completed");
            }else{
                complete = new SimpleStringProperty("Not Completed");
            }
        }

        public String getName() {
            return name.get();
        }

        public String getColour() {
            return colour.get();
        }


        public String getComplete() {
            return complete.get();
        }

        public String getDate() {
            return date.get();
        }
    }
}
