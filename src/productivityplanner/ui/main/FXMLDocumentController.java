package productivityplanner.ui.main;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import productivityplanner.data.JournalEntry;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.ui.taskcell.TaskCellController;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {
    @FXML
    private VBox calendarPane;
    @FXML
    private JFXTabPane tabsMainBar;
    @FXML
    private Tab tabTasks;
    @FXML
    private JFXTabPane tabsTasks;
    @FXML
    private Tab tabUncompletedTasks;
    @FXML
    private JFXButton btnAddTask;
    @FXML
    private JFXListView<Task> uncompletedTasks;
    @FXML
    private JFXListView<Task> completedTasks;
    @FXML
    private Tab tabCompletedTasks;
    @FXML
    private Tab tabJournal;
    @FXML
    private Label lblJournalDate;
    @FXML
    private JFXButton btnSaveJournal;
    @FXML
    private Tab tabSettings;
    @FXML
    private Label lblUserAction;
    @FXML
    private Label lblProgramAction;
    @FXML
    private TextArea txtJournal;
    @FXML
    private JFXButton btnRefreshTaskList;

    ObservableList<Task> taskList = FXCollections.observableArrayList();
    ObservableList<JournalEntry> entryList = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        YearMonth date = YearMonth.now();
        // Generate the calendar
        calendarPane.getChildren().add(new Calendar(date).getView());

        updateSelectedDate(Calendar.selectedDay); // Highlight the current date.

        updateJournalTitle(); // Update the journal view

        loadTasks(); // Update the task lists.
        completedTasks.setCellFactory(completedListView -> new TaskCellController());
        uncompletedTasks.setCellFactory(uncompletedTaskView -> new TaskCellController());
    }

    @FXML
    private void updateJournalTitle() {
        lblJournalDate.setText(Calendar.selectedDay.getFormattedDate());
    }

    public void setSelectedDay(Day date){
        System.out.println("The date of this day is: " + date.getDate());
        // Create a border around the currently selected day
        updateSelectedDate(date);
    }

    public void updateSelectedDate(Day currentSelectedDay){
        //TODO: Don't update if the day is still the same as before.
        if (currentSelectedDay != null)
        {
            // HIGHLIGHT/BORDER
            // Clear the border from the current selected date.
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(2))));
            // Change the currently selected day to the one that's just been clicked on
            Calendar.selectedDay = currentSelectedDay;
            // Add a border to the new selected date
            Calendar.selectedDay.setBorder(new Border(new BorderStroke(Color.web("#292929"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
            // UPDATE JOURNAL
            loadJournal(Calendar.selectedDay.getDate());
            updateJournal();
            // UPDATE TASKS
            loadTasks();
        }
        else {
            System.out.println("Unable to update selected date.");
        }
    }

    @FXML
    public void loadAddNewTask(ActionEvent actionEvent) {
        loadWindow(getClass().getResource("/productivityplanner/ui/addtask/AddNewTask.fxml"), "Add New Task", null);
    }

    @FXML
    public void loadEditTask(Task task) {
        updateSelectedTask(task);
        loadWindow(getClass().getResource("/productivityplanner/ui/edittask/EditTask.fxml"), "Edit Task", null);
    }

    public void loadTasks() {
        LocalDate dateToLoad = Calendar.selectedDay.getDate();
        taskList.clear();
        completedTasks.getItems().clear();
        uncompletedTasks.getItems().clear();

        String query = "SELECT * FROM TASK WHERE date=\'" + dateToLoad + "\'";
        ResultSet results = databaseHandler.executeQuery(query);
        try{
            while(results.next()){
                String name = results.getString("name");
                String color = results.getString("colour");
                Boolean compl = results.getBoolean("isComplete");
                String date = results.getString("date");
                //System.out.println(name + " " + color + " " + compl + " " + date);

                taskList.add(new Task(LocalDate.parse(date), name, Color.web(color), compl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(Task task : taskList){
            if (task.getCompleted()){
                completedTasks.getItems().add(task);
            } else {
                uncompletedTasks.getItems().add(task);
            }
        }
    }

    // Get the selected task from the uncomplete or completed listview.
    public Task getSelectedTask(){
        // Check which tab is open.. Completed or Uncomplete tasks
        String selectedTab = tabsTasks.getSelectionModel().getSelectedItem().getId();

        if (selectedTab.equals("tabCompletedTasks")) // Complete
        {
            System.out.println("Complete Tab Selected");
            return completedTasks.getSelectionModel().getSelectedItem();
        }
        else if (selectedTab.equals("tabUncompletedTasks")) // Uncompleted
        {
            System.out.println("UnComplete TabSelected");
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
        String journalText = txtJournal.getText();

        // Should the user be allowed to save if there is nothing written?
        // TODO: Add an alert which asks the user for confirmation when saving an empty entry.
        if (journalText.isEmpty()){
        }
        // Check if the database has a journal entry yet.
        if (loadJournal(Calendar.selectedDay.getDate())){
            databaseHandler.updateJournalEntry(journalText);
        } else {
            addNewJournalEntry(journalText);
        }
    }


    private boolean addNewJournalEntry(String text) {
        try {
            String action = "INSERT INTO JOURNAL VALUES (?, ?)";
            PreparedStatement statement = databaseHandler.getConnection().prepareStatement(action);
            statement.setString(1, Calendar.selectedDay.getDate().toString());
            statement.setString(2, text);
            int result = statement.executeUpdate();
            if (result > 1)
                lblUserAction.setText("Added new journal entry.");
            return (result > 1);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to add new entry.");
            alert.showAndWait();
            lblProgramAction.setText("Unable to add new entry.");
        }
        return false;
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
        lblProgramAction.setText("Journal Updated");
    }

    @FXML
    // Refresh the task lists by reloading the date's tasks.
    public void refreshTaskList(ActionEvent actionEvent) {
        loadTasks();
    }

    // TODO: Decide if this should show every task ever created or just the details of the current task (date created, date completed, etc).
    public void viewAllTasks(ActionEvent actionEvent) {
        loadTasks(); // Reload the full task list from the database TODO: Make this LoadAllTasks because currently it's only loading the current day's tasks.
        loadWindow(getClass().getResource("/productivityplanner/ui/tasklist/TaskList.fxml"), "Task List", null);
    }

    // Finds the task cell that's provided and selects in in the appropriate list view.
    public void updateSelectedTask(Task task) {
        for(Task currentTask : taskList){ // Go through all the
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
