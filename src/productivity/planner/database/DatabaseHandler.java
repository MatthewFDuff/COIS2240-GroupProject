package productivity.planner.database;

import javafx.scene.paint.Color;
import productivity.planner.data.Task;
import productivity.planner.ui.main.Calendar;

import java.sql.*;

import static productivity.planner.ui.main.Main.getFXMLController;

// Reference: https://github.com/afsalashyana/Library-Assistant
// Video Explanation: https://www.youtube.com/watch?v=XZAQxZcjSVE
public class DatabaseHandler {
    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:derby:database/calendar;create=true";
    private static Connection connection = null;
    private static Statement statement = null;

    private DatabaseHandler() {
        createConnection();
        setupTaskTable();
        setupJournalTable();
    }

    // Creates a new handler the first time it's called and returns it each time it's called.
    public static DatabaseHandler getInstance(){
        if (handler == null) {
            handler = new DatabaseHandler();
        }

        return handler;
    }

    void createConnection(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(DB_URL);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeQuery(String query) { // Used to execute statements such as SELECT statements which return results.
        ResultSet result;
        try{
            statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public boolean executeAction(String action) { // Used to execute statements such as INSERT/CREATE_TABLE which don't return results.
        try {
            statement = connection.createStatement();
            statement.execute(action);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTask(String taskName, String taskColour, Task task){
        String previousName = task.getName();
        Color previousColor = task.getColor();
        try{
            String query = "UPDATE TASK SET NAME = ?, COLOUR = ? WHERE (NAME = ? AND COLOUR = ? AND DATE = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, taskName);
            statement.setString(2, taskColour);
            statement.setString(3, previousName);
            statement.setString(4, previousColor.toString());
            statement.setString(5, Calendar.selectedDay.getDate().toString());
            int result = statement.executeUpdate();
            if (result > 0)
            {
                getFXMLController().loadTasks();
            }
            return (result > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Connection getConnection() {
        return connection;
    }

    void setupTaskTable(){
        String TABLE_NAME = "TASK";
        try{
            statement = connection.createStatement();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists.");
            } else {
                statement.execute("CREATE TABLE " + TABLE_NAME + "("
                            + "name varchar(200),\n"
                            + "colour varchar(200), \n"
                            + "isComplete boolean default false, \n"
                            + "date varchar(20), \n"
                            + "primary key(name, colour, date))");
                System.out.println("Database successfully created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setupJournalTable(){
        String TABLE_NAME = "JOURNAL";
        try{
            statement = connection.createStatement();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists.");
            }else
            {
                statement.execute("CREATE TABLE " + TABLE_NAME + "("
                            + "date varchar(20) primary key,\n" // Only one journal entry allowed per day, which is why the date is the primary key.
                            + "text long varchar)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: Requires more testing.
    public boolean deleteTask(Task task) {
        //TODO: Add confirmation before deleting? Or a way to see what was deleted.
        try{
            String query = "DELETE FROM TASK WHERE (NAME=? AND COLOUR=? AND DATE=?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, task.getName());
            statement.setString(2, task.getColor().toString());
            statement.setString(3, task.getDate().toString());
            int result = statement.executeUpdate();
            getFXMLController().loadTasks();
            return (result > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleComplete(Task task) {
        Boolean complete = task.getCompleted();

        // TOGGLE COMPLETE
        if (complete)
            complete = false;
        else
            complete = true;

        try{
            String query = "UPDATE TASK SET ISCOMPLETE = ? WHERE (NAME = ? AND COLOUR = ? AND DATE = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, complete.toString());
            statement.setString(2, task.getName());
            statement.setString(3, task.getColor().toString());
            statement.setString(4, Calendar.selectedDay.getDate().toString());
            int result = statement.executeUpdate();
            if (result > 0){
                getFXMLController().loadTasks();
            }
            return (result > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
