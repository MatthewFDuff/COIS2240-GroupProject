package productivityplanner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import productivityplanner.utility.Utility;

import java.io.IOException;

public class Main extends Application {

    // +---------+
    // References:
    // +---------+
    // Icons: https://material.io/tools/icons/?icon=settings&style=outline
    // Derby Database: https://github.com/afsalashyana/Library-Assistant
    //                 Video Explanation: https://www.youtube.com/watch?v=XZAQxZcjSVE
    // Load and Save: http://www.java2s.com/Code/Java/Database-SQL-JDBC/LoadandsavedatatoDerbydatabase.htm

    static MainController controller;

    @Override
    public void start(Stage primaryStage) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = fxmlLoader.load();

            controller = fxmlLoader.getController();

            primaryStage.setTitle("Productivity Planner");
            Scene scene = new Scene(root, 1280, 800);

            // Add CSS files
            scene.getStylesheets().add("productivityplanner/ui/style/tab-pane.css");
            scene.getStylesheets().add("productivityplanner/ui/style/calendar.css");
            scene.getStylesheets().add("productivityplanner/ui/style/style.css");

            primaryStage.setMinWidth(1175);
            primaryStage.setMinHeight(725);
            primaryStage.setMaxWidth(1280);
            primaryStage.setMaxHeight(800);

            primaryStage.setScene(scene);
            primaryStage.show();
            Utility.setProgramIcon(primaryStage);

            getMainController().calendar.setDayClips(); // Day clips prevent info from extending past the size of a day pane, which needs to be done after everything is created/sized.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Allows other controller classes to communicate with our main controller.
    public static MainController getMainController()
    {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
