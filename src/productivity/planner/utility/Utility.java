package productivity.planner.utility;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Utility {
    private static String ICON_PATH = "productivity/planner/ui/icons/outline_check_box_white_48dp.png";

    public static void setProgramIcon(Stage stage){
        stage.getIcons().add(new Image(ICON_PATH));
    }
}
