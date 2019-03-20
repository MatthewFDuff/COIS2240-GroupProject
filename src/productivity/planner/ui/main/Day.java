package productivity.planner.ui.main;

import javafx.scene.Node;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static productivity.planner.ui.main.Main.getFXMLController;

public class Day extends AnchorPane {
    private LocalDate date;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> getFXMLController().setSelectedDay(this));
    }

    public LocalDate getDate(){ return date; }
    public void setDate(LocalDate date){ this.date = date; }
    public String getFormattedDate() { return date.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")); }
}
