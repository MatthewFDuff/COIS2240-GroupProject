package productivity.planner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import productivity.planner.database.DatabaseHandler;
import productivity.planner.utility.Utility;

import java.io.IOException;

public class Main extends Application {

    // References:
    // Icons: https://material.io/tools/icons/?icon=settings&style=outline
    // Derby Database:
    // Load and Save: http://www.java2s.com/Code/Java/Database-SQL-JDBC/LoadandsavedatatoDerbydatabase.htm

    // DATABASE
    // TODO: Delete database (fresh start)
    // TODO: Switch to PreparedStatements

    // CALENDAR
    // TODO: Add the number of tasks/statistics to each calendar day on startup, and update the selected day each time a task is edited/deleted.

    // JOURNAL
    // TODO: Add save confirmation when the text area is empty (Because it will overwrite the previous entry if it existed)

    // TASKS/TASKLISTS
    // TODO: Finish add/edit task designs

    // PROJECT CODE
    // TODO: Cleanup database and controllers so the methods they use are suited to them/make sense
    // TODO: Rename variables to follow a standard because right now it's a mess. (Colour/color, or txtVariable vs variableTextbox)

    // CSS
    // TODO: Create on main file for all CSS and organize the components appropriately

    // DOCUMENTATION
    // TODO: ALL

    // OPTIONAL
    // TODO: Settings: Add the ability to switch colour schemes?

    static MainController controller;

    @Override
    public void start(Stage primaryStage) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainUI.fxml"));
            Parent root = fxmlLoader.load();

            controller = (MainController) fxmlLoader.getController();

            primaryStage.setTitle("Productivity Planner");
            Scene scene = new Scene(root, 1280, 800);

            // Add CSS files
            scene.getStylesheets().add("productivity/planner/ui/style/tab-pane.css");
            scene.getStylesheets().add("productivity/planner/ui/style/calendar.css");
            scene.getStylesheets().add("productivity/planner/ui/style/test.css");

            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(800);
            primaryStage.setScene(scene);
            primaryStage.show();
            Utility.setProgramIcon(primaryStage);

            DatabaseHandler.getInstance(); // Instantiate new database
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MainController getFXMLController()
    {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
