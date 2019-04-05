package productivityplanner.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class Utility {

    // Sets the icon of a window to a checkbox.
    public static void setProgramIcon(Stage stage){
        String ICON_PATH = "productivityplanner/ui/icons/outline_check_box_black_48dp.png";
        stage.getIcons().add(new Image(ICON_PATH));
    }

    // Loads a new window (such as for then window for adding a new task).
    public static Object loadWindow(URL path, String title, Stage parentStage) {
        Object controller = null;
        try{
            FXMLLoader loader = new FXMLLoader(path);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage;
            if (parentStage != null){
                stage = parentStage;
            } else{
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    // Checks if a hex colour is bright.
    public static Boolean isBright(String hexColour) {
        Integer r, g, b;

        r = 16 * hexToInt(hexColour.substring(0,1));
        r += hexToInt(hexColour.substring(1,2));

        g = 16 * hexToInt(hexColour.substring(2,3));
        g += hexToInt(hexColour.substring(3,4));

        b = 16 * hexToInt(hexColour.substring(4,5));
        b += hexToInt(hexColour.substring(5,6));

        Double brightness = Math.sqrt((r*r*0.241) + (g*g*0.691) + (b*b*0.068));

        if (brightness > 160)
            return true;
        else return false;
    }

    // Changes a single hexadecimal number (0-f) to decimal.
    public static Integer hexToInt(String hex){
        Integer result = 0;

        try {
            result = Integer.parseInt(hex);
        } catch(NumberFormatException e){
            switch(hex) {
                case "a":
                    result = 10;
                    break;
                case "b":
                    result = 11;
                    break;
                case "c":
                    result = 12;
                    break;
                case "d":
                    result = 13;
                    break;
                case "e":
                    result = 14;
                    break;
                case "f":
                    result = 15;
                    break;
            }
        }

        return result;
    }
}
