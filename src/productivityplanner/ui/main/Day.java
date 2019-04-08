package productivityplanner.ui.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import productivityplanner.data.Task;
import productivityplanner.database.DatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static productivityplanner.ui.main.Main.getMainController;

public class Day extends VBox {

    private LocalDate date; // Holds the day's date.

    private HBox banner; // Technically this isn't being used TODO: Remove banner (and related code) or implement new selection visual.

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> getMainController().setSelectedDay(this));
        this.setPadding(new Insets(1, 1, 1, 1));
    }

    // Getters and Setters for date and banner.
    public LocalDate getDate(){ return date; }
    public String getFormattedDate() { return date.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")); }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setBanner(HBox hb){
        banner = hb;
    }
    public HBox getBanner(){ return banner; }

    // Puts out the tasks of a given day on the calender.
    public void updateTasks(){
        ObservableList<Task> tasks = FXCollections.observableArrayList();

        DatabaseHelper.loadTasksFromDate(tasks, this.getDate());

        if (!tasks.isEmpty()) {  // If the day has at least 1 task.
            int IMAGE_SIZE = 30;
            TilePane taskPane = new TilePane();
            taskPane.setPrefSize(this.getWidth(),this.getHeight());
            ObservableList<ImageView> taskList = FXCollections.observableArrayList();

            for (Task task: tasks){ // Go through each task of the day.
                ImageView box;
                Image image;

                if (task.getCompleted()){ // Set appropriate image based on whether task is completed.
                    image = new Image("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
                } else {
                    image = new Image("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
                }

                box = new ImageView(image); // Setup box which holds the image
                ImageView boxClip = new ImageView(image);
                boxClip.setFitWidth(IMAGE_SIZE);
                boxClip.setFitHeight(IMAGE_SIZE);
                boxClip.setPreserveRatio(true);
                box.setClip(boxClip);
                box.setFitHeight(IMAGE_SIZE);
                box.setFitWidth(IMAGE_SIZE);
                box.setPreserveRatio(true);

                ColorAdjust monochrome = new ColorAdjust(); // Change the colour to match the task colour.
                monochrome.setSaturation(-1.0);
                Blend colour = new Blend(BlendMode.MULTIPLY, monochrome, new ColorInput(0,0,box.getFitWidth(), box.getFitHeight(), task.getColor()));
                box.setEffect(colour);

                taskList.add(box); // Add current box to the list of tasks.
            }

            taskPane.getChildren().addAll(taskList);
            this.getChildren().add(taskPane);
        }
    }
}
