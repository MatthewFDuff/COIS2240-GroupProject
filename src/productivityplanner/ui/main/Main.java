package productivityplanner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        primaryStage.setTitle("Productivity Planner");
        Scene scene = new Scene(root);

        // Add CSS files
        scene.getStylesheets().add("productivityplanner/ui/style/test.css");
        scene.getStylesheets().add("productivityplanner/ui/style/tab-pane.css");
        scene.getStylesheets().add("productivityplanner/ui/style/calendar.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
