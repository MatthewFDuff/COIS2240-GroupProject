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
    private AnchorPane calendarPane;
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
    private Font x3;
    @FXML
    private Color x4;
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

        loadTasks(Calendar.currentDate); // Update the task lists.
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
            loadTasks(Calendar.selectedDay.getDate());
        }
        else
        {
            System.out.println("Unable to update selected date.");
        }
    }

    @FXML
    public void loadAddNewTask(ActionEvent actionEvent) {
        loadWindow("/productivityplanner/ui/addtask/addnewtask.fxml", "Add New Task");
    }

    @FXML
    public void loadEditTask(ActionEvent actionEvent) {
        loadWindow("/productivityplanner/ui/edittask/edittask.fxml", "Edit Task");
    }

    // Load tasks for the given date.
    public void loadTasks(LocalDate dateToLoad) {
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
                System.out.println(name + " " + color + " " + compl + " " + date);

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
        // Check which tab is open. . Completed or Uncomplete tasks
        String selectedTab = tabsTasks.getSelectionModel().getSelectedItem().getId();
        System.out.println("ID: " + selectedTab);
        if (selectedTab.equals("tabCompletedTasks")) // Uncomplete
        {
            System.out.println("Complete Tab Selected");
            return completedTasks.getSelectionModel().getSelectedItem();
        }
        else if (selectedTab.equals("tabUncompletedTasks"))
        {
            System.out.println("UnComplete TabSelected");
            return uncompletedTasks.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    // Loads a new window (such as for adding a new task).
    void loadWindow(String path, String title) {
        try{
            Parent parent = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save the journal entry to the database. (Requires a rewrite, because only one entry is allowed per day)
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
        }else {
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

    private void updateJournal(){
        updateJournalTitle();
        if (!entryList.isEmpty()){
            txtJournal.setText(entryList.get(0).getText());
        } else{
            txtJournal.clear();
        }
        lblProgramAction.setText("Journal Updated");
    }

    public JFXListView<Task> getCompletedTaskList() {
        return completedTasks;
    }
    public JFXListView<Task> getUncompletedTaskList() {
        return uncompletedTasks;
    }

    @FXML
    public void refreshTaskList(ActionEvent actionEvent) {
        // Find all "uncompleted" tasks that have been checked and are marked
        ObservableList<Task> selected = uncompletedTasks.getSelectionModel().getSelectedItems();

        //completedTasks.getItems().clear();
        //uncompletedTasks.getItems().clear();
    }

    @FXML
    public void deleteTask(ActionEvent actionEvent){
        // If the delete button is clicked.
    }

    public void refreshTask(Task task) {

    }
}
