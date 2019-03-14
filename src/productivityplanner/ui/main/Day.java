package productivityplanner.ui.main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;

import static productivityplanner.ui.main.Main.getFXMLController;

public class Day extends AnchorPane {
    private LocalDate date;

    public Day(Node... children)
    {
        super(children);
        this.setOnMouseClicked(e -> getFXMLController().setSelectedDay(this));
    }

    public LocalDate getDate(){ return date; }
    public void setDate(LocalDate date){
        this.date = date;
    }
}
