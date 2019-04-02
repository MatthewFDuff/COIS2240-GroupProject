package productivityplanner.ui.calendar;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import productivityplanner.ui.main.Day;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import static productivityplanner.ui.main.Main.getFXMLController;

public class CalendarController extends VBox{
    @FXML
    private JFXButton btnPreviousMonth;
    @FXML
    private JFXButton btnNextMonth;
    @FXML
    private HBox hboxMonthTitle;
    @FXML
    private HBox hboxWeekTitles;
    @FXML
    private GridPane daysGrid;

    private static Text calendarTitle;

    private static ArrayList<Day> calendarDays = new ArrayList<>(42);

    private YearMonth currentYearMonth;
    public static LocalDate currentDate;
    public static Day selectedDay;

    public CalendarController(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Calendar.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int PREF_WIDTH = 700;
        int PREF_HEIGHT = 600;
        hboxMonthTitle.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        hboxWeekTitles.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        hboxWeekTitles.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        hboxMonthTitle.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        daysGrid.setPrefSize(PREF_WIDTH, PREF_HEIGHT);

        btnPreviousMonth.setOnAction(e -> previousMonth(null));
        btnNextMonth.setOnAction((e -> nextMonth(null)));

        currentYearMonth = YearMonth.now();
        currentDate = LocalDate.now();

        // DAY:
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 7; j++){
                Day day = new Day();
                day.setMinSize(PREF_WIDTH / 7,PREF_HEIGHT / 6);
                day.setMaxSize(PREF_WIDTH / 7,PREF_HEIGHT / 6);
                day.setClip(new Rectangle(PREF_WIDTH / 7,PREF_HEIGHT / 6));
                daysGrid.add(day, j, i); // Add the day to the grid.
                calendarDays.add(day);   // Add the day to the list.
            }
        }

        // WEEK:
        Text[] weekdays = {new Text("Sunday"), new Text("Monday"), new Text("Tuesday"), new Text("Wednesday"), new Text("Thursday"), new Text("Friday"), new Text("Saturday")};
        for(Text weekday: weekdays){
            weekday.setFont(new Font(16));

            HBox pane = new HBox(); // Use HBox here to have the labels easily centered.
            pane.setAlignment(Pos.CENTER);
            pane.getStyleClass().add("weekday-pane");
            pane.setMinSize(PREF_WIDTH / 7, 30);
            pane.setPrefWidth(PREF_WIDTH);
            pane.getChildren().add(weekday);           // Add the text to the day box
            hboxWeekTitles.getChildren().add(pane);    // Add the pane to the weekday box
        }

        // MONTH:
        calendarTitle = new Text();
        calendarTitle.setTextAlignment(TextAlignment.CENTER);
        calendarTitle.getStyleClass().add("calendar-title");
        hboxMonthTitle.getChildren().add(1, calendarTitle);

        // Create the arrow images which will be used to change the current month.
        int arrowButtonSize = 30;

        ImageView nextArrow = new ImageView("productivityplanner/ui/icons/outline_arrow_forward_ios_white_48dp.png");
        nextArrow.setFitHeight(arrowButtonSize);
        nextArrow.setPreserveRatio(true);

        ImageView previousArrow = new ImageView("productivityplanner/ui/icons/outline_arrow_back_ios_white_48dp.png");
        previousArrow.setFitHeight(arrowButtonSize);
        previousArrow.setPreserveRatio(true);

        // Add the arrow images to the buttons.
        btnNextMonth.setGraphic(nextArrow);
        btnPreviousMonth.setGraphic(previousArrow);

        // This code must be called last.
        updateCalendar(currentYearMonth);
        selectedDay = FindDay(currentDate);
        System.out.println(selectedDay.getDate());
    }

    public static Day FindDay(LocalDate newDay)
    {
        for(Day day: calendarDays) // Find the current day
        {
            if (day.getDate().compareTo(newDay) == 0)
            {
                return day;
            }
        }
        System.out.println("FindDay: Could not find day.");
        return null;
    }

    // Used to update the calendar for a single day, rather than all the days.
    public static void updateDay(Day day){
        // Then we can change it in the same way we do updateDays();
        day.getChildren().clear();

        // Individual day's number
        Text txt = new Text(String.valueOf(day.getDate().getDayOfMonth()));
        day.setDate(day.getDate());
        day.getChildren().add(txt);
        // Individual day's tasks if they exist.
        day.updateTasks();
    }

    public static void updateDays(LocalDate calendarDate){
        // Populate the days on the calendar with numbers and information (task and journal data)
        for (Day currentDay : calendarDays) {
            // TODO: OPTIONAL - Tag days which aren't in the currently selected month so they can be greyed out.
            currentDay.getChildren().clear();

            // Individual day's number
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            currentDay.setDate(calendarDate);
            currentDay.getChildren().add(txt);
            // Individual day's tasks if they exist.
            currentDay.updateTasks();

            calendarDate = calendarDate.plusDays(1); // Iterate to next day (Equivalent to: "days++;")
            currentDay.setClip(new Rectangle(currentDay.getWidth(), currentDay.getHeight()));
        }
    }

    // Sets up each day that is currently visible, including setting their dates.
    public static void updateCalendar(YearMonth yearMonth) {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        // Go back one day at a time until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            calendarDate = calendarDate.minusDays(1);
        }

        updateDays(calendarDate);

        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + (yearMonth.getYear()));
    }

    public void previousMonth(ActionEvent actionEvent) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar(currentYearMonth);
        getFXMLController().updateSelectedDate(FindDay(LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), 1))); // Highlight the current date.
    }

    public void nextMonth(ActionEvent actionEvent) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar(currentYearMonth);
        getFXMLController().updateSelectedDate(FindDay(LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), 1))); // Highlight the current date.
    }
}
