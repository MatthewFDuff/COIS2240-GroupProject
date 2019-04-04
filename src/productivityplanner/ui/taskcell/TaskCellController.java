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
import productivityplanner.database.DatabaseHelper;
import productivityplanner.utility.Utility;

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

    private Task task;
    private static TaskCellController selected;

    // Load image objects and set image height/widths
    private ImageView cbBlackCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_black_48dp.png");
    private ImageView cbBlackUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_black_48dp.png");
    private ImageView cbWhiteCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
    private ImageView cbWhiteUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");

    private Boolean lightColour = false;

    public TaskCellController() {
        loadFXML();

        btnDelete.setOnAction(e -> getFXMLController().loadDeleteTask(this.task));
        btnEdit.setOnAction((e -> getFXMLController().loadEditTask(this.task)));

        cell.setOnMouseClicked(e -> {
            TaskCellController previousSelection = null;
            if (selected != null)
              previousSelection = selected;

            selected = this;
            updateItem(task, false);

            if (previousSelection != null)
                previousSelection.updateItem(task, false);
        });

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

    public static TaskCellController getSelected(){
        return selected;
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

            lightColour = Utility.isBright(taskColour);
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

            if (this == selected)
                setCellSelected(true);
            else
                setCellSelected(false);

            setGraphic(cell);
        }
    }
}