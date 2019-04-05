package productivityplanner.database;

import java.sql.*;

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

    public static Connection getConnection() {
        return connection;
    }

    // Creates a new handler the first time it's called and returns it each time it's called.
    public static DatabaseHandler getInstance(){
        if (handler == null) {
            handler = new DatabaseHandler();
        }

        return handler;
    }

    // Create a connection to the database.
    private void createConnection(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(DB_URL);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Used to execute statements such as SELECT statements which return results.
    static ResultSet executeQuery(String query) {
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

    // Used to execute statements such as INSERT/CREATE_TABLE which don't return results.
    public boolean executeAction(String action) {
        try {
            statement = connection.createStatement();
            statement.execute(action);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Creates a new table to store tasks if the table doesn't exist.
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
                            + "primary key(name, colour, date))"); // This could be done with an auto-incremented ID instead.
                System.out.println("Database successfully created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates a new table to store journal entries if the table doesn't exist yet.
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
}
