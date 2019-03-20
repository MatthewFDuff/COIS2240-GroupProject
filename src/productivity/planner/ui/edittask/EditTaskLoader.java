package productivity.planner.ui.edittask;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.stage.StageStyle.*;

public class EditTaskLoader extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../../resources/view/EditTask.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add("productivity/planner/ui/style/test.css");

        stage.setScene(scene);
        stage.initStyle(UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
