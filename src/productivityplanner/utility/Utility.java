package productivityplanner.utility;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Utility {
    private static String ICON_PATH = "productivityplanner/ui/icons/outline_check_box_white_48dp.png";

    public static void setProgramIcon(Stage stage){
        stage.getIcons().add(new Image(ICON_PATH));
    }
}
