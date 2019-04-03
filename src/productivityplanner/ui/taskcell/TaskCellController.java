package productivityplanner.ui.taskcell;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHandler;
import productivityplanner.ui.main.FXMLDocumentController;
import productivityplanner.database.DatabaseHelper;

import java.awt.*;
import java.io.IOException;

import static productivityplanner.ui.main.Main.getFXMLController;

// References:  https://stackoverflow.com/questions/47511132/javafx-custom-listview
//              https://www.turais.de/how-to-custom-listview-cell-in-javafx/
public class TaskCellController extends ListCell<Task> {

    public Task getTask() {
        return task;
    }

    @FXML
    HBox cell;
    @FXML
    Label lblTaskName;
    @FXML
    Button btnComplete;
    @FXML
    JFXButton btnDelete;
    @FXML
    JFXButton btnEdit;

    Task task;

    // Load image objects and set image height/widths
    ImageView cbBlackCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_black_48dp.png");
    ImageView cbBlackUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_black_48dp.png");
    ImageView cbWhiteCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
    ImageView cbWhiteUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");

    private Boolean lightColour = false;

    public TaskCellController() {
        loadFXML();

        btnDelete.setOnAction(e -> getFXMLController().loadDeleteTask(this.task));
        btnEdit.setOnAction((e -> getFXMLController().loadEditTask(this.task)));

        btnComplete.setOnAction((event) -> {
            DatabaseHelper.toggleComplete(this.task);

            // Toggles between checked/unchecked image when clicked
            if (lightColour) {
                if (task.getCompleted()) {
                    btnComplete.setGraphic(cbBlackCompleted);
                } else {
                    btnComplete.setGraphic(cbBlackUncompleted);
                }
            } else {
                if (task.getCompleted()) {
                    btnComplete.setGraphic(cbBlackCompleted);
                } else {
                    btnComplete.setGraphic(cbBlackUncompleted);
                }
            }
        });
    }

    private void setCellSelected(boolean bool) {
        btnDelete.setVisible(bool);
        btnEdit.setVisible(bool);
        setGraphic(cell);
    }

    private void loadFXML(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TaskCell.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Task item, boolean empty){
        super.updateItem(item, empty);

        btnDelete.setMinSize(30,30);
        btnEdit.setMinSize(30,30);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            task = item;
            String taskColour = item.getColor().toString().substring(2,8); // Substring makes "0xff66cc33" into this "ff66cc"

            lblTaskName.setMinWidth(cell.getMaxWidth());

            lightColour = isBright(taskColour);
            if (lightColour)
                lblTaskName.setStyle("-fx-text-fill: black;");
            else lblTaskName.setStyle("-fx-text-fill: white;");

            lblTaskName.setText(item.getName());                    // Set the cell's name to the name of the task.
            cell.setStyle("-fx-background-color: #" + taskColour);  // Set the cell colour to the task colour.
            btnComplete.setStyle("-fx-background-color: #" + taskColour);  // Set the button colour to the task colour.

            // Set the cell's button to display a check mark if it has been completed (set without by default)
            if (lightColour) {
                if (task.getCompleted()) {
                    cbBlackCompleted.setFitHeight(30.0);
                    cbBlackCompleted.setFitWidth(30.0);
                    btnComplete.setGraphic(cbBlackCompleted);
                } else {
                    cbBlackUncompleted.setFitHeight(30.0);
                    cbBlackUncompleted.setFitWidth(30.0);
                    btnComplete.setGraphic(cbBlackUncompleted);
                }
            } else {
                if (task.getCompleted()) {
                    cbWhiteCompleted.setFitHeight(30.0);
                    cbWhiteCompleted.setFitWidth(30.0);
                    btnComplete.setGraphic(cbWhiteCompleted);
                } else {
                    cbWhiteUncompleted.setFitHeight(30.0);
                    cbWhiteUncompleted.setFitWidth(30.0);
                    btnComplete.setGraphic(cbWhiteUncompleted);
                }
            }

            setCellSelected(true);

            setGraphic(cell);
        }
    }

    // Checks if a hex colour is bright
    private Boolean isBright(String hexColour) {

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

    // changes a single hexadecimal number (0-f) to decimal
    private Integer hexToInt(String hex){

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
