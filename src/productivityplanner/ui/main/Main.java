package productivityplanner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // References:
    // Icons: https://material.io/tools/icons/?icon=settings&style=outline

    // TASKS AND JOURNAL
    // TODO: Each time a new day is selected, the task lists and journal need to update.

    // TASKS/TASKLISTS
    // TODO: Delete Task
    // TODO: Add New Task
    // TODO: Edit Task


    // OPTIONAL
    // TODO: Settings: Add the ability to switch colour schemes?

    static FXMLDocumentController controller;

    @Override
    public void start(Stage primaryStage) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = fxmlLoader.load();

            controller = (FXMLDocumentController) fxmlLoader.getController();

            primaryStage.setTitle("Productivity Planner");
            Scene scene = new Scene(root);

            // Add CSS files
            scene.getStylesheets().add("productivityplanner/ui/style/tab-pane.css");
            scene.getStylesheets().add("productivityplanner/ui/style/calendar.css");
            scene.getStylesheets().add("productivityplanner/ui/style/test.css");

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FXMLDocumentController getFXMLController()
    {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
