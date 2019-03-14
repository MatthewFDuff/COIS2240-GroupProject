package productivityplanner.database;

import java.sql.*;

// Reference: https://github.com/afsalashyana/Library-Assistant
// Video Explanation: https://www.youtube.com/watch?v=XZAQxZcjSVE
public class DatabaseHandler {
    private static DatabaseHandler handler;

    private static final String DB_URL = "jdbc:derby:database/calendar;create=true";
    private static Connection connection = null;
    private static Statement statement = null;

    public DatabaseHandler() {
        createConnection();
        setupTaskTable();
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

    public ResultSet executeQuery(String query) { // Used to execute statements such as SELECT statements which return results.
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
                            + "date varchar(200), \n"
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
                            + "date varchar(200) primary key,\n"
                            + "text varchar(255)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
