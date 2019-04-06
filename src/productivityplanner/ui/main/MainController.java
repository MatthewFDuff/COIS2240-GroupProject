package productivityplanner.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import productivityplanner.data.JournalEntry;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.database.DatabaseHelper;
import productivityplanner.ui.taskcell.TaskCellController;
import productivityplanner.utility.Utility;

import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // Generally you don't need to instantiate buttons here because you reference their actions, not the button itself.
    @FXML private VBox calendarPane;
    @FXML private JFXListView<Task> tasks;
    @FXML private Label lblJournalDate;
    @FXML private TextArea txtJournal;
    @FXML private JFXButton btnToggleCompleted;
    @FXML private JFXButton btnToggleUncompleted;

    // booleans which tell what to show in the task list
    // shows completed tasks if true
    static public Boolean toggleComplete = true;
    // shows uncompleted tasks if true
    static public Boolean toggleUncomplete = true;

    // holds the most recently deleted task so it can be undone
    Task savedTask;

    public ObservableList<Task> taskList = FXCollections.observableArrayList();
    ObservableList<JournalEntry> entryList = FXCollections.observableArrayList();

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(); // DO NOT REMOVE (This instantiates the database handler on startup and is required, regardless of if we use it in this class or not)

    Calendar calendar;

    @Override
    // set up main window
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();

        this.calendar = new Calendar(date);
        Node calendarView = calendar.getView();
        calendarPane.getChildren().add(calendarView);  // Generate the calendar GUI.

        updateSelectedDate(Calendar.selectedDay);   // Highlight the current date.
        updateJournalTitle();                       // Update the journal view.
        loadTasks();                                // Update the task lists.

        tasks.setCellFactory(completedListView -> new TaskCellController()); // Cell factory creates new task cells for each task.
    }

    @FXML
    // Sets the title of the journal to the current date.
    private void updateJournalTitle() {
        lblJournalDate.setText(Calendar.selectedDay.getFormattedDate());
    }

    // Checks if a new day was selected and sets the new selected day.
    void setSelectedDay(Day date){
        if (date != Calendar.selectedDay) {         // The data should only be loaded if a new day is selected to prevent reloading the same day.
            TaskCellController.setSelected(null);   // The task that was selected no longer exists.
            updateSelectedDate(date);
        }
    }

    // Changes the currently selected day and updates task and journal data to the new day.
    void updateSelectedDate(Day currentSelectedDay){
        if (currentSelectedDay != null)
        {
            // HIGHLIGHT/BORDER:
            // Clear the border from the current selected date.
            //Calendar.selectedDay.getBanner().setStyle("-fx-background-color: #FFFFFF");
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.web("TRANSPARENT"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));

            // Change the currently selected day to the one that's just been clicked on.
            Calendar.selectedDay = currentSelectedDay;

            // Add a border to the new selected date.
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.web("#292929"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
            //Calendar.selectedDay.getBanner().setStyle("-fx-background-color: #FF9800");

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

    // Loads all tasks for the current day and sorts them into the appropriate task list.
    public void loadTasks() {
        tasks.getItems().clear();                               // Clear the prior task list.

        try {
            if (DatabaseHelper.loadTasks(taskList)){            // If the tasks were successfully loaded..
                for(Task task : taskList){
                    if (task.getCompleted()){                   // Check if task is completed.
                        if (toggleComplete)                     // Add if task is completed and completed are to be shown.
                            tasks.getItems().add(task);
                    }
                    else {
                        if (toggleUncomplete)                   // Add if task is uncompleted and uncompleted are to be shown.
                            tasks.getItems().add(task);
                    }
                }

                calendar.updateDay(Calendar.selectedDay, null);
            }
        } catch (Exception e) {                                 // If task list was not able to be loaded...
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);     // Create an alert for being unable to load tasks.
            alert.setHeaderText(null);
            alert.setContentText("Unable to load tasks.");
            alert.showAndWait();
        }
    }

    // Save the journal entry to the database. (Requires an update/rewrite, because only one entry is allowed per day).
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
            txtJournal.setText(entryList.get(0).getText()); // entryList is the journal entry list, which should only be length 1 (index 0).
        } else{
            txtJournal.clear();
        }
    }

    @FXML
    // Undo the last deleted task and restore it in the task list.
    void undoDelete(ActionEvent event) {

        if(savedTask == null){                              // Stop if there had been no task deleted
        }else if (DatabaseHelper.insertTask(savedTask)) {   // Insert the task into the database.
            savedTask = null;
            loadTasks();                                    // Reload the task lists so the new task is displayed.

        }else {                                             // If task could not be re-added...
            Alert alert = new Alert(Alert.AlertType.ERROR); // create alerts for being unable to re-add
            alert.setHeaderText(null);
            alert.setContentText("Unable to re-add new task.");
            alert.showAndWait();
            System.out.println("Error: Unable to re-add task.");
        }
    }

    @FXML
    // Loads the stage for adding a new task
    public void loadAddNewTask(ActionEvent actionEvent) {
        Utility.loadWindow(getClass().getResource("/productivityplanner/ui/addtask/AddNewTask.fxml"), "Add New Task", null);
    }

    @FXML
    // Loads the stage for editing an existing task
    public void loadEditTask(Task task) {
        Utility.loadWindow(getClass().getResource("/productivityplanner/ui/edittask/EditTask.fxml"), "Edit Task", null);
    }

    @FXML
    // Loads the stage for deleting a task
    public void loadDeleteTask(Task task){
        savedTask = task; // save the deleted task in case the user wants to undo
        Utility.loadWindow(getClass().getResource("/productivityplanner/ui/deletetask/DeleteTask.fxml"), "Delete Confirmation", null);
    }

    // Toggles button fot showing and hiding completed tasks
    public void toggleCompleted(ActionEvent actionEvent) {
        taskList.clear();                                   // Empty task list

        ImageView image = new ImageView();                  // image to be set as the new button icon


        if (toggleComplete) {                               // If set to show completed tasks
            toggleComplete = false;                         // swap boolean
                                                            // swap image
            image = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
        }
        else                                                // If set to not show completed tasks
        {
            toggleComplete = true;
            image = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
        }
        image.setFitHeight(30.0);                             // Size of image
        image.setFitWidth(30.0);

        btnToggleCompleted.setGraphic(image);               // Set graphic to be the new image
        loadTasks();
    }

    // Toggles button fot showing and hiding uncompleted tasks
    public void toggleUncompleted(ActionEvent actionEvent) {
        taskList.clear();                                   // Empty task list.

        ImageView image = new ImageView();                  // image to be set as the new button icon.

        if (toggleUncomplete) {                             // If set to show uncompleted tasks.
            toggleUncomplete = false;                       // Swap boolean.
                                                            // Swap Image.
            image = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
        }
        else {                                              // If set to not show uncompleted tasks.
            toggleUncomplete = true;
            image = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
        }

        image.setFitHeight(30.0);                           // Size of image.
        image.setFitWidth(30.0);

        btnToggleUncompleted.setGraphic(image);             // Set graphic to be new image.
        loadTasks();
    }

    // Toggle whether the task list is enabled or disabled (Button found in Settings tab within program).
    public void toggleTaskList(ActionEvent actionEvent) {
        if (tasks.isDisabled()){    // Enable the tasklist.
            tasks.setDisable(false);
        }
        else{   // Disable the tasklist.
            tasks.setDisable(true);
            tasks.setOpacity(1); // Removes effect on disable which makes the list darker.
        }
    }
}
