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

import static productivityplanner.ui.main.Main.getMainController;

// References:  https://stackoverflow.com/questions/47511132/javafx-custom-listview
//              https://www.turais.de/how-to-custom-listview-cell-in-javafx/
public class TaskCellController extends ListCell<Task> {
    @FXML HBox cell;
    @FXML Label lblTaskName;
    @FXML Button btnComplete;
    @FXML JFXButton btnDelete;
    @FXML JFXButton btnEdit;

    private static TaskCellController selected; // Keeps track of which task is selected within the list.

    // Load image objects and set image height/widths.
    private ImageView cbBlackCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_black_48dp.png");
    private ImageView cbBlackUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_black_48dp.png");
    private ImageView cbWhiteCompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
    private ImageView cbWhiteUncompleted = new ImageView("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");

    private Task task;                      // The current cell's task.
    private Boolean lightColour = false;    // Used to change both the task name and checkbox colour based on the task colour.

    public TaskCellController() {
        loadFXML();

        btnDelete.setOnAction(e -> getMainController().loadDeleteTask(this.task));
        btnEdit.setOnAction((e -> getMainController().loadEditTask(this.task)));

        // Toggle the task cell as selected.
        cell.setOnMouseClicked(e -> {
            TaskCellController previousSelection = null;
            if (selected != null) // Used to save what was previously selected in order to update it once we change the selected task.
              previousSelection = selected;

            if (previousSelection == this) // If the task is already selected, deselect it.
                selected = null;
            else
                selected = this;            // Otherwise, select it.
            updateItem(task, false);

            if (previousSelection != null)
                previousSelection.updateItem(task, false);
        });

        // Toggles the task being completed if the checkbox button is pressed.
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

    // Load the design of the task cell.
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

    public static void setSelected(TaskCellController taskCellController) {
        selected = taskCellController;
    }
    public static TaskCellController getSelected(){
        return selected;
    }
    public Task getTask() {
        return task;
    }

    // Sets the delete and edit buttons visible within the task cell (Used only when a task is selected).
    private void setButtonsVisible(boolean bool) {
        btnDelete.setVisible(bool);
        btnEdit.setVisible(bool);
        setGraphic(cell);
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
            String taskColour = item.getColor().toString().substring(2,8); // Substring makes "0xff66cc33" into "ff66cc"

            lblTaskName.setMinWidth(cell.getMaxWidth());

            // Determine the brightness of the task's colour.
            lightColour = Utility.isBright(taskColour);
            if (lightColour)
                lblTaskName.setStyle("-fx-text-fill: black;");
            else lblTaskName.setStyle("-fx-text-fill: white;");

            lblTaskName.setText(item.getName());                        // Set the cell's name to the name of the task.
            cell.setStyle("-fx-background-color: #" + taskColour);      // Set the cell colour to the task colour.
            btnComplete.setStyle("-fx-background-color: TRANSPARENT");  // Set the button's background transparent.

            // Set the cell's button to display a check mark if it has been completed (set without checkmark by default).
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

            // Set the Edit and Delete buttons visible if the task is currently selected.
            if (this == selected)
                setButtonsVisible(true);
            else
                setButtonsVisible(false);

            // Display the cell.
            setGraphic(cell);
        }
    }
}