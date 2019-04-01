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

import static productivityplanner.ui.main.Main.getFXMLController;

public class Day extends VBox {
    private LocalDate date;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> getFXMLController().setSelectedDay(this));
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setSpacing(10);
    }

    public LocalDate getDate(){ return date; }
    public String getFormattedDate() { return date.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")); }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void updateTasks(){
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        Day tempDay = Calendar.selectedDay; // Save the current selected day so we can revert back to it once we're done loading info for each day.
        Calendar.selectedDay = this;

        DatabaseHelper.loadTasks(tasks);

        if (tasks != null && !tasks.isEmpty()) {
            int IMAGE_SIZE = 30;
            TilePane taskPane = new TilePane();
            taskPane.setPrefSize(this.getWidth(),this.getHeight());
            ObservableList<ImageView> taskList = FXCollections.observableArrayList();

            for (Task task: tasks){
                ImageView box;
                Image image;

                if (task.getCompleted()){
                    image = new Image("productivityplanner/ui/icons/outline_check_box_white_48dp.png");
                } else {
                    image = new Image("productivityplanner/ui/icons/outline_check_box_outline_blank_white_48dp.png");
                }

                box = new ImageView(image);
                ImageView boxClip = new ImageView(image);
                boxClip.setFitWidth(IMAGE_SIZE);
                boxClip.setFitHeight(IMAGE_SIZE);
                boxClip.setPreserveRatio(true);
                box.setClip(boxClip);
                box.setFitHeight(IMAGE_SIZE);
                box.setFitWidth(IMAGE_SIZE);
                box.setPreserveRatio(true);

                // Change the colour to match the task colour.
                ColorAdjust monochrome = new ColorAdjust();
                monochrome.setSaturation(-1.0);
                Blend colour = new Blend(BlendMode.MULTIPLY, monochrome, new ColorInput(0,0,box.getFitWidth(), box.getFitHeight(), task.getColor()));
                box.setEffect(colour);

                taskList.add(box);
            }
            taskPane.getChildren().addAll(taskList);
            this.getChildren().add(taskPane);
        }

        Calendar.selectedDay = tempDay;
    }
}
