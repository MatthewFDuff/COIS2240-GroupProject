package productivityplanner.database;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import productivityplanner.data.JournalEntry;
import productivityplanner.data.Task;
import productivityplanner.ui.main.Calendar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static productivityplanner.ui.main.Main.getMainController;


// Class which allows the program to change Tasks and JournalEntry's saved into the database
public class DatabaseHelper {

    // Insert task into database.
    public static boolean insertTask(Task task){
        try{
            String query = "INSERT INTO TASK VALUES (?,?,?,?)";                 // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(query); // Setup statement for query
            statement.setString(1, task.getName());               // Add task's name to the statement
            statement.setString(2, task.getColor().toString());   // Add task's colour to the statement
            statement.setString(3, "false"); // Task completion variable will always be false when a new task is created/added.
            statement.setString(4, task.getDate().toString());    // Add task's date to the statement
            int result = statement.executeUpdate();                            // send out the statement
            return (result > 0);
        } catch (SQLException e) {                                         // If the task wasn't able to be added
            e.printStackTrace();                                           // Create alert for If the task was unable to be added
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to add new task.");
            alert.showAndWait();
        }
        return false;
    }

    // Remove a task from the database.
    public static boolean deleteTask(Task task) {
        try{
            String query = "DELETE FROM TASK WHERE (NAME=? AND COLOUR=? AND DATE=?)";   // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(query); // Setup statement for query
            statement.setString(1, task.getName());                       // Add task's values to the statement
            statement.setString(2, task.getColor().toString());
            statement.setString(3, task.getDate().toString());
            int result = statement.executeUpdate();                                     // send out the statement
            getMainController().loadTasks();
            return (result > 0);
        } catch (SQLException e) {                                  // If the task wasn't able to be deleted
            e.printStackTrace();                                    // Create alert for failing to delete
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to delete task.");
            alert.showAndWait();
        }
        return false;
    }

    // Update a task's information within the database.
    public static boolean updateTask(String taskName, String taskColour, Task task){
        String previousName = task.getName();    // Get information of the task up for editing
        Color previousColor = task.getColor();
        try{
            String query = "UPDATE TASK SET NAME = ?, COLOUR = ? WHERE (NAME = ? AND COLOUR = ? AND DATE = ?)"; // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(query); // Setup statement for query
            statement.setString(1, taskName);                     // Add new values to the statement
            statement.setString(2, taskColour);
            statement.setString(3, previousName);                 // Add old values to the statement
            statement.setString(4, previousColor.toString());
            statement.setString(5, Calendar.selectedDay.getDate().toString());
            int result = statement.executeUpdate();               // send out the statement
            if (result > 0)         // If it was successful, reload the task list.
            {
                getMainController().loadTasks();
            }
            return (result > 0);
        } catch (SQLException e) {                          // If task was unable to be edited
            e.printStackTrace();                            // Create alert for failing to edit
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to edit task.");
            alert.showAndWait();
        }
        return false;
    }

    // Loads all tasks of the current day from the database.
    public static boolean loadTasks(ObservableList<Task> taskList) {
        taskList.clear();
        try{
            String query = "SELECT * FROM TASK WHERE date=\'" + Calendar.selectedDay.getDate() + "\'"; // Setup query for task
            ResultSet results = DatabaseHandler.executeQuery(query);            // Setup ResultSet for query

            while(results.next()){                                              // Go through all task found of the selected day
                String name = results.getString("name");             // store values of task
                String color = results.getString("colour");
                Boolean complete = results.getBoolean("isComplete");
                String date = results.getString("date");

                taskList.add(new Task(LocalDate.parse(date), name, Color.web(color), complete)); //store task into list
            }
            return true;
        } catch (SQLException e) {                          // If task couldn't be loaded
            e.printStackTrace();                            // Create alert for failing to load
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to load tasks.");
            alert.showAndWait();
        }
        return false;
    }

    // Toggles the completion status of a task.
    public static boolean toggleComplete(Task task) {
        Boolean complete = task.getCompleted();          // Get the current value

        // TOGGLE COMPLETE
        if (complete)         // store the opposite of the current value
            complete = false;
        else
            complete = true;
        try{
            String query = "UPDATE TASK SET ISCOMPLETE = ? WHERE (NAME = ? AND COLOUR = ? AND DATE = ?)"; // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(query);  // Setup statement for query
            statement.setString(1, complete.toString());                        // Store new isCompleted value
            statement.setString(2, task.getName());                             // store values of task
            statement.setString(3, task.getColor().toString());
            statement.setString(4, Calendar.selectedDay.getDate().toString());
            int result = statement.executeUpdate();                                           // send out the statement
            if (result > 0){                        // If swapped successfully, re-load the tasks
                getMainController().loadTasks();    // Reload the task list to update the graphics.
            }
            return (result > 0);
        } catch (SQLException e) {
            e.printStackTrace();                            // If task was unable to be edited
            e.printStackTrace();                            // Create alert for failing to edit
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to edit task.");
            alert.showAndWait();
        }
        return false;
    }

    // Insert a new journal entry into the database.
    public static boolean insertJournalEntry(JournalEntry entry){
        try {
            String action = "INSERT INTO JOURNAL VALUES (?, ?)";                  // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(action); // Setup statement for query
            statement.setString(1, entry.getDate().toString());     // Store date of the journal
            statement.setString(2, entry.getText());                // Store content of journal
            int result = statement.executeUpdate();                              // send out the statement
            return (result > 1);
        } catch (SQLException e) {                                               // If Journal was unable to be saved
            e.printStackTrace();                                                 // Create new alert for failing to save
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to add new entry.");
            alert.showAndWait();
        }
        return false;
    }

    // Change an existing journal entry if the user presses the save button.
    public static boolean updateJournalEntry(JournalEntry entry){
        try{
            String query = "UPDATE JOURNAL SET TEXT = ? WHERE DATE=?";          // Setup query for task
            PreparedStatement statement = DatabaseHandler.getConnection().prepareStatement(query);  // Setup statement for query
            statement.setString(1, entry.getText());               // Store info of the Journal
            statement.setString(2, entry.getDate().toString());
            int result = statement.executeUpdate();                             // send out the statement
            return (result > 0);
        } catch (SQLException e) {                                              // If Journal was unable to be edited
            e.printStackTrace();                                                // Create alert for failing to edit
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to update journal entry.");
            alert.showAndWait();
        }
        return false;
    }

    // Loads the current day's journal entry from the database.
    public static boolean loadJournal(ObservableList<JournalEntry> entryList){
        entryList.clear();
        try{
            String query = "SELECT * FROM JOURNAL WHERE date=\'" + Calendar.selectedDay.getDate() + "\'"; // Setup query for task
            ResultSet results = DatabaseHandler.executeQuery(query);                // Setup ResultSet for query
            // Loop through journal entries
            while(results.next()) {
                String text = results.getString("text");                // Store attributes of journal entry
                String date = results.getString("date");
                entryList.add(new JournalEntry(text, LocalDate.parse(date)));      // save into list
            }
            return true;
        } catch (SQLException e) {                                  // If Journals are unable to be loaded
            e.printStackTrace();                                    // Create alert for failing to load
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Unable to load journal entry.");
            alert.showAndWait();
        }
        return false;
    }
}
