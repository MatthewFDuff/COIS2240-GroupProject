package productivityplanner.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.YearMonth;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        primaryStage.setTitle("Productivity Planner");
        primaryStage.setScene(new Scene(root, 1233, 850));
        //primaryStage.setScene(new Scene(new Calendar(YearMonth.now()).getView()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
