package productivityplanner.ui.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import productivityplanner.data.JournalEntry;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.database.DatabaseHelper;
import productivityplanner.ui.taskcell.TaskCellController;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {
    // Generally you don't need to instantiate buttons here because you reference their actions, not the button itself.
    @FXML
    private VBox calendarPane;
    @FXML
    private JFXTabPane tabsTasks;
    @FXML
    private JFXListView<Task> uncompletedTasks;
    @FXML
    private JFXListView<Task> completedTasks;
    @FXML
    private Label lblJournalDate;
    @FXML
    private TextArea txtJournal;

    public static ObservableList<Task> taskList = FXCollections.observableArrayList();
    ObservableList<JournalEntry> entryList = FXCollections.observableArrayList();

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(); // DO NOT REMOVE (This instantiates the database handler on startup and is required)

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();

        calendarPane.getChildren().add(new Calendar(date).getView());   // Generate the calendar GUI.
        updateSelectedDate(Calendar.selectedDay);                       // Highlight the current date.
        updateJournalTitle();                                           // Update the journal view.
        loadTasks();                                                    // Update the task lists.

        // TODO: ELLA this will be changed when you restructure with one list.
        completedTasks.setCellFactory(completedListView -> new TaskCellController());
        uncompletedTasks.setCellFactory(uncompletedTaskView -> new TaskCellController());
    }

    @FXML
    // Sets the title of the journal to the current date.
    private void updateJournalTitle() {
        lblJournalDate.setText(Calendar.selectedDay.getFormattedDate());
    }

    public void setSelectedDay(Day date){
        if (date != Calendar.selectedDay) { // The data should only be loaded if a new day is selected to prevent reloading the same day.
            updateSelectedDate(date);
        }
    }

    // Changes the currently selected day and updates task and journal data to the new day.
    public void updateSelectedDate(Day currentSelectedDay){
        if (currentSelectedDay != null)
        {
            // HIGHLIGHT/BORDER:
            // Clear the border from the current selected date.
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(2))));
            // Change the currently selected day to the one that's just been clicked on.
            Calendar.selectedDay = currentSelectedDay;
            // Add a border to the new selected date.
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.web("#292929"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
            // UPDATE JOURNAL:
            DatabaseHelper.loadJournal(entryList);
            updateJournal();
            // UPDATE TASKS:
            loadTasks();
        }
        else {
            System.out.println("Unable to update selected date.");
        }
    }

    @FXML
    // Loads the stage for adding a new task
    public void loadAddNewTask(ActionEvent actionEvent) {
        loadWindow(getClass().getResource("/productivityplanner/ui/addtask/AddNewTask.fxml"), "Add New Task", null);
    }

    @FXML
    // Loads the stage for editing an existing task
    public void loadEditTask(Task task) {
        updateSelectedTask(task);
        loadWindow(getClass().getResource("/productivityplanner/ui/edittask/EditTask.fxml"), "Edit Task", null);
    }

    @FXML
    public void loadDeleteTask(Task task){
        updateSelectedTask(task);
        loadWindow(getClass().getResource("/productivityplanner/ui/deletetask/DeleteTask.fxml"), "Delete Confirmation", null);
    }

    // Loads all tasks for the current day and sorts them into the appropriate task list.
    // TODO: ELLA this will be changed when you restructure with one list.
    public void loadTasks() {
        completedTasks.getItems().clear();
        uncompletedTasks.getItems().clear();
        try {
            if (DatabaseHelper.loadTasks(taskList)){            // If the tasks were successfully loaded..
                for(Task task : taskList){                      // Separate them into completed/uncompleted lists.
                    if (task.getCompleted()){
                        completedTasks.getItems().add(task);
                    } else {
                        uncompletedTasks.getItems().add(task);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to load tasks.");
            alert.showAndWait();
        }
    }

    public static ObservableList<Task> getTaskList(){
        return null;
    }

    // Get the selected task from the uncomplete or completed listview.
    // TODO: ELLA this will be changed when you restructure with one list.
    public Task getSelectedTask(){
        // Check which tab is open.. Completed or Uncomplete tasks
        String selectedTab = tabsTasks.getSelectionModel().getSelectedItem().getId();

        if (selectedTab.equals("tabCompletedTasks")) {
            return completedTasks.getSelectionModel().getSelectedItem();
        }
        else if (selectedTab.equals("tabUncompletedTasks")) {
            return uncompletedTasks.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    // Loads a new window (such as for then window for adding a new task).
    public static Object loadWindow(URL path, String title, Stage parentStage) {
        Object controller = null;
        try{
            FXMLLoader loader = new FXMLLoader(path);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage = null;
            if (parentStage != null){
                stage = parentStage;
            }else{
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    // Save the journal entry to the database. (Requires an update/rewrite, because only one entry is allowed per day)
    public void saveJournal(ActionEvent actionEvent) {
        // Get journal text from the form.
        JournalEntry entry = new JournalEntry(txtJournal.getText(), Calendar.selectedDay.getDate());

        // Check if the database has a journal entry yet.
        if (DatabaseHelper.loadJournal(entryList)){     // If a journal entry already exists:
            DatabaseHelper.updateJournalEntry(entry);   // Update the existing entry.
        } else {                                        // OTHERWISE
            DatabaseHelper.insertJournalEntry(entry);   // Create new journal entry.
        }
    }

    // Loads the current days journal (BUT DOES NOT UPDATE IT)
    private boolean loadJournal(LocalDate dateToLoad) {
        entryList.clear();
        String text = null;
        String date;
        String query = "SELECT * FROM JOURNAL WHERE date=\'" + dateToLoad + "\'";
        ResultSet results = databaseHandler.executeQuery(query);
        try{
            while(results.next()) {
                text = results.getString("text");
                date = results.getString("date");
                entryList.add(new JournalEntry(text,LocalDate.parse(date)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (text == null)
            return false;

        return true;
    }

    // Update the journal title, and text.
    private void updateJournal(){
        updateJournalTitle();
        if (!entryList.isEmpty()){
            txtJournal.setText(entryList.get(0).getText()); // entryList is the journal entry list, which should only be length 1 (index 0)
        } else{
            txtJournal.clear();
        }
    }

    @FXML
    // Refresh the task lists by reloading the date's tasks.
    public void refreshTaskList(ActionEvent actionEvent) {
        loadTasks();
    }

    // Finds the given task and selects it in the appropriate list view.
    // TODO: ELLA this will be changed when you restructure with one list.
    public void updateSelectedTask(Task task) {
        for(Task currentTask : taskList){
            if (task.getCompleted()){
                if (currentTask != null)
                {
                    if (currentTask.equals(task))
                        completedTasks.getSelectionModel().select(currentTask);
                }
            } else {
                if (currentTask != null)
                {
                    if (currentTask.equals(task))
                        uncompletedTasks.getSelectionModel().select(currentTask);
                }
            }
        }
    }
}