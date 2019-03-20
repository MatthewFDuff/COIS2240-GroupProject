package productivity.planner.ui.main;

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
import productivity.planner.ui.taskcell.TaskCellController;
import productivity.planner.data.JournalEntry;
import productivity.planner.data.Task;
import productivity.planner.database.DatabaseHandler;
import productivity.planner.database.DatabaseHelper;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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
    private Label lblUserAction;
    @FXML
    private Label lblProgramAction;
    @FXML
    private TextArea txtJournal;

    ObservableList<Task> taskList = FXCollections.observableArrayList();
    ObservableList<JournalEntry> entryList = FXCollections.observableArrayList();

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(); // DO NOT REMOVE (This instantiates the database handler on startup and is required)

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();

        calendarPane.getChildren().add(new Calendar(date).getView());   // Generate the calendar GUI.
        updateSelectedDate(Calendar.selectedDay);                       // Highlight the current date.
        updateJournalTitle();                                           // Update the journal view.
        loadTasks();                                                    // Update the task lists.

        // Set the cell factories so any tasks which are added to the task lists automatically get created.
        completedTasks.setCellFactory(completedListView -> new TaskCellController());
        uncompletedTasks.setCellFactory(uncompletedTaskView -> new TaskCellController());
    }

    @FXML
    // Sets the journal title to the currently selected day.
    private void updateJournalTitle() {
        lblJournalDate.setText(Calendar.selectedDay.getFormattedDate());
    }

    // Sets the currently selected day to the date provided.
    public void setSelectedDay(Day date){
        // Sets the day's border and updates the task and journal data.
        if (date != Calendar.selectedDay) // Only reload the data if the day isn't already selected.
        {
            updateSelectedDate(date);
        }
    }

    // Loads the task and journal data of the selected day and updates border.
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
        loadWindow(getClass().getResource("/productivity/planner/ui/addtask/AddNewTask.fxml"), "Add New Task", null);
    }

    @FXML
    // Loads the stage for editing an existing task
    public void loadEditTask(Task task) {
        updateSelectedTask(task);
        loadWindow(getClass().getResource("/productivity/planner/ui/edittask/EditTask.fxml"), "Edit Task", null);
    }

    // TODO: Decide if this should show every task ever created or just the details of the current task (date created, date completed, etc).
    public void viewAllTasks(ActionEvent actionEvent) {
        loadTasks(); // Reload the full task list from the database TODO: Make this LoadAllTasks because currently it's only loading the current day's tasks.
        loadWindow(getClass().getResource("/productivity/planner/ui/tasklist/TaskList.fxml"), "Task List", null);
    }

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

    // Get the selected task from the uncomplete or completed list view.
    public Task getSelectedTask(){
        // Check which tab is open.. Completed or Uncomplete tasks
        String selectedTab = tabsTasks.getSelectionModel().getSelectedItem().getId();

        if (selectedTab.equals("tabCompletedTasks")) // Complete
        {
            return completedTasks.getSelectionModel().getSelectedItem();
        }
        else if (selectedTab.equals("tabUncompletedTasks")) // Uncompleted
        {
            return uncompletedTasks.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    // Loads a new window/stage (such as for adding a new task).
    public static Object loadWindow(URL path, String title, Stage parentStage) {
        Object controller = null;
        try{
            FXMLLoader loader = new FXMLLoader(path);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage;
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

    // Save the journal entry to the database. (Requires a rewrite, because only one entry is allowed per day)
    public void saveJournal(ActionEvent actionEvent) {
        // Get journal text from the form.
        JournalEntry entry = new JournalEntry(txtJournal.getText(), Calendar.selectedDay.getDate());

        // Check if the database has a journal entry yet.
        if (DatabaseHelper.loadJournal(entryList)){
            DatabaseHelper.updateJournalEntry(entry);    // Update journal entry.
        }else {
            DatabaseHelper.insertJournalEntry(entry);                    // Create journal entry.
        }
    }

    // Update the journal title, and text.
    private void updateJournal(){
        updateJournalTitle();
        if (!entryList.isEmpty()){
            txtJournal.setText(entryList.get(0).getText()); // entryList is the journal entry list, which should only be length 1 (index 0)
        } else{
            txtJournal.clear();
        }
        lblProgramAction.setText("Journal Updated");
    }

    @FXML
    // Refresh the task lists by reloading the date's tasks.
    public void refreshTaskList(ActionEvent actionEvent) {
        loadTasks();
    }

    // Finds the task cell that's provided and selects in in the appropriate list view.
    public void updateSelectedTask(Task task) {
        for(Task currentTask : taskList){ // We need to find the provided task in the existing task list.
            if (currentTask != null) {
                if (task.getCompleted()) {
                    if (currentTask.equals(task))
                        completedTasks.getSelectionModel().select(currentTask);
                } else {
                    if (currentTask.equals(task))
                        uncompletedTasks.getSelectionModel().select(currentTask);
                }
            }
        }
    }
}
