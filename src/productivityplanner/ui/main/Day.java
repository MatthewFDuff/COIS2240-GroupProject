package productivityplanner.ui.main;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static productivityplanner.ui.main.Main.getFXMLController;

public class Day extends HBox {
    private LocalDate date;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> getFXMLController().setSelectedDay(this));
        this.setPadding(new Insets(5, 5, 5, 5));
    }

    public LocalDate getDate(){ return date; }
    public String getFormattedDate() { return date.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY")); }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
