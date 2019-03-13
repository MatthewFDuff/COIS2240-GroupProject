package productivityplanner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // References:
    // Icons: https://material.io/tools/icons/?icon=settings&style=outline

    // TODO: Switch anchor panes where they're unnecessary
    // TODO: Add Icons
    // TODO: Settings?

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        FXMLDocumentController documentController = new FXMLDocumentController();
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Productivity Planner");
        Scene scene = new Scene(root);


        // Add CSS files
        scene.getStylesheets().add("productivityplanner/ui/style/tab-pane.css");
        scene.getStylesheets().add("productivityplanner/ui/style/calendar.css");
        scene.getStylesheets().add("productivityplanner/ui/style/test.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
