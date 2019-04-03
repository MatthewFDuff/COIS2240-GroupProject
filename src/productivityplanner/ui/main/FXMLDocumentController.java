package productivityplanner.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import java.time.YearMonth;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {
    // Generally you don't need to instantiate buttons here because you reference their actions, not the button itself.
    @FXML
    private VBox calendarPane;
    @FXML
    private Tab tabTasks;
    @FXML
    private JFXListView<Task> tasks;
    @FXML
    private Label lblJournalDate;
    @FXML
    private TextArea txtJournal;
    @FXML
    private JFXButton btnToggleCompleted;
    @FXML
    private JFXButton btnToggleUncompleted;

    static public Boolean toggleComplete = true;
    static public Boolean toggleUncomplete = true;

    public ObservableList<Task> taskList = FXCollections.observableArrayList();
    ObservableList<JournalEntry> entryList = FXCollections.observableArrayList();

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(); // DO NOT REMOVE (This instantiates the database handler on startup and is required, regardless of if we use it in this class or not)

    Calendar calendar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();

        this.calendar = new Calendar(date);
        Node calendarView = calendar.getView();
        calendarPane.getChildren().add(calendarView);   // Generate the calendar GUI.

        updateSelectedDate(Calendar.selectedDay);                       // Highlight the current date.
        updateJournalTitle();                                           // Update the journal view.
        loadTasks();                                                    // Update the task lists.

        tasks.setCellFactory(completedListView -> new TaskCellController()); // Cell factory creates new task cells for each task.
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

    public void toggleSelectionVisual(Day day, boolean selected){
        if (selected){

        } else {

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

    private void updateSelectedTask(Task task) {
        for(Task currentTask : taskList){ // Go through all the tasks
            if (currentTask != null)
            {
                if (currentTask.equals(task))
                    tasks.getSelectionModel().select(currentTask);
            }
        }
    }

    // Loads all tasks for the current day and sorts them into the appropriate task list.
    public void loadTasks() {
        tasks.getItems().clear();

        try {
            if (DatabaseHelper.loadTasks(taskList)){            // If the tasks were successfully loaded..
                for(Task task : taskList){                      // Separate them into completed/uncompleted lists.
                    tasks.getItems().add(task);
                }

                calendar.updateDay(Calendar.selectedDay, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to load tasks.");
            alert.showAndWait();
        }
    }

    public ObservableList<Task> getTaskList(){
        loadTasks();
        return taskList;
    }

    // Get the selected task from the task list.
    public Task getSelectedTask(){
        return tasks.getSelectionModel().getSelectedItem();
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
            } else{
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

    public void toggleCompleted(ActionEvent actionEvent) {
        if (toggleComplete) {
            toggleComplete = false;
            ImageView image = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
            image.setFitHeight(30);
            image.setFitWidth(30);
            btnToggleCompleted.setGraphic(image);
        }
        else
        {
            toggleComplete = true;
            ImageView image = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
            image.setFitHeight(30);
            image.setFitWidth(30);
            btnToggleCompleted.setGraphic(image);
        }
        refreshTaskList(null);
    }
    //TODO: Change the imageviews to be created ONCE and then ACCESSED, rather than created each time the button is pressed.
    public void toggleUncompleted(ActionEvent actionEvent) {
        if (toggleUncomplete) {
            toggleUncomplete = false;
            ImageView image = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
            image.setFitHeight(30);
            image.setFitWidth(30);
            btnToggleUncompleted.setGraphic(image);
        }
        else
        {
            toggleUncomplete = true;
            ImageView image = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
            image.setFitHeight(30);
            image.setFitWidth(30);
            btnToggleUncompleted.setGraphic(image);
        }
        refreshTaskList(null);
    }
}
