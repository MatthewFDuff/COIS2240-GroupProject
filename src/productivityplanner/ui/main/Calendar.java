package productivityplanner.ui.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

// Reference: https://github.com/SirGoose3432/javafx-calendar
class Calendar {

    private ArrayList<Day> calendarDays = new ArrayList<>(42); // 6 rows * 7 columns
    private VBox view;
    private Text calendarTitle;
    private YearMonth currentYearMonth;

    public Calendar(YearMonth yearMonth){
        int CALENDAR_WIDTH = 910;
        int CALENDAR_HEIGHT = 720;
        int DAY_WIDTH = CALENDAR_WIDTH / 7;     // 910/7 = 130
        int DAY_HEIGHT = CALENDAR_WIDTH / 6;    // 720/6 = 120

        currentYearMonth = yearMonth; // Get the current year and month.

        // Create a new grid for the calendar.
        GridPane calendar = new GridPane();
        calendar.setPrefSize(CALENDAR_WIDTH,CALENDAR_HEIGHT);
        calendar.setGridLinesVisible(true);

        // For every day in the calendar
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                Day day = new Day();
                day.setPrefSize(DAY_WIDTH, DAY_HEIGHT);
                calendar.add(day, j, i);    // Add to the grid
                calendarDays.add(day);      // Add the day to a list so we can reference specific days quickly or iterate through days which are currently displayed.
            }
        }

        // Create labels for each day of the week.
        Text[] Weekdays = {new Text("Sunday"), new Text("Monday"), new Text("Tuesday"), new Text("Wednesday"), new Text("Thursday"), new Text("Friday"), new Text("Saturday")};

        GridPane weekdayLabels = new GridPane();
        weekdayLabels.setPrefWidth(CALENDAR_WIDTH);

        Integer col = 0; // Determines the order the days appear in our grid.
        // TODO: Would it be better to just use an HBox here? We don't need rows, so we don't technically need a grid box.
        for (Text text: Weekdays){
            text.setFont(new Font(16)); // Set weekday font size

            AnchorPane pane = new AnchorPane();
            pane.getStyleClass().add("weekday-pane");
            pane.setPrefSize(DAY_WIDTH, 30);
            pane.setPadding(new Insets(0,0,0,20));
            pane.setBottomAnchor(text, 5.0);
            pane.getChildren().add(text);
            weekdayLabels.add(pane, col++, 0);
        }

        // Create calendarTitle and buttons to change current month
        calendarTitle = new Text();
        calendarTitle.getStyleClass().add("calendar-title");

        // Create new buttons
        Button previousMonth = new Button("<");
        Button nextMonth = new Button(">");

        // Allows us to change the CSS for the month selection arrows without changing other buttons too
        previousMonth.getStyleClass().add("month-selector");
        nextMonth.getStyleClass().add("month-selector");

        // If a button is pressed, call a function to change the calendar.
        previousMonth.setOnAction(e -> previousMonth());
        nextMonth.setOnAction(e -> nextMonth());


        HBox titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        titleBar.getStyleClass().add("calendar-title-pane");
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        // Populate calendar with the appropriate day numbers
        updateCalendar(yearMonth);

        // Create the calendar view (which is added to the calendarPane AnchorPane which you can see in Scenebuilder).
        view = new VBox(titleBar, weekdayLabels, calendar);
    }

    public void updateCalendar(YearMonth yearMonth) {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            calendarDate = calendarDate.minusDays(1);
        }

        // Populate the days on the calendar with numbers
        for (Day currentDay : calendarDays) {
            // TODO: Tag days which aren't in the currently selected month so they can be greyed out.

            if (currentDay.getChildren().size() != 0) {
                currentDay.getChildren().remove(0);
            }

            // Individual day's number
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            currentDay.setDate(calendarDate);
            currentDay.setTopAnchor(txt, 5.0);
            currentDay.setLeftAnchor(txt, 5.0);
            currentDay.getChildren().add(txt);
            calendarDate = calendarDate.plusDays(1); // Iterate to next day (Equivalent to: "days++;")
        }

        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    // Switch the calendar to the previous month and update the calendar.
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar(currentYearMonth);
    }

    // Switch the calendar to the next month and update the calendar.
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar(currentYearMonth);
    }

    //
    public VBox getView() {
        return view;
    }
}
