package productivityplanner.ui.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        currentYearMonth = yearMonth;

        GridPane calendar = new GridPane();
        calendar.setPrefSize(910,720); // 910/7 = 130, 720/6 = 120
        calendar.setGridLinesVisible(true);

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                Day day = new Day();
                day.setPrefSize(130,120);
                calendar.add(day, j, i);
                calendarDays.add(day);
            }
        }

        // Labels
        Text[] Weekdays = {new Text("Sunday"), new Text("Monday"), new Text("Tuesday"), new Text("Wednesday"), new Text("Thursday"), new Text("Friday"), new Text("Saturday")};
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(910);
        Integer col = 0;
        for (Text text: Weekdays){
            AnchorPane pane = new AnchorPane();
            pane.setPrefSize(130, 20);
            pane.setBottomAnchor(text, 5.0);
            pane.getChildren().add(text);
            dayLabels.add(pane, col++, 0);
        }

        // Create calendarTitle and buttons to change current month
        calendarTitle = new Text();
        Button previousMonth = new Button("<");
        previousMonth.setOnAction(e -> previousMonth());
        Button nextMonth = new Button(">");
        nextMonth.setOnAction(e -> nextMonth());
        HBox titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        titleBar.setAlignment(Pos.BASELINE_CENTER);
        // Populate calendar with the appropriate day numbers
        populateCalendar(yearMonth);
        // Create the calendar view
        view = new VBox(titleBar, dayLabels, calendar);
    }


    public void populateCalendar(YearMonth yearMonth) {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            calendarDate = calendarDate.minusDays(1);
        }
        // Populate the calendar with day numbers
        for (Day ap : calendarDays) {
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0);
            }
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(txt, 5.0);
            ap.setLeftAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            calendarDate = calendarDate.plusDays(1);
        }
        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    /**
     * Move the month back by one. Repopulate the calendar with the correct dates.
     */
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    /**
     * Move the month forward by one. Repopulate the calendar with the correct dates.
     */
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }
    public static void initializeWeekdays(){
        int weekdays = 7;

    }

    public VBox getView() {
        return view;
    }

    public ArrayList<Day> getAllCalendarDays() {
        return calendarDays;
    }

    public void setAllCalendarDays(ArrayList<Day> allCalendarDays) {
        this.calendarDays = allCalendarDays;
    }

}
