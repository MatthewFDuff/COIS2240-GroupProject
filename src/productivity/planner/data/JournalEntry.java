package productivity.planner.data;

import java.time.LocalDate;

public class JournalEntry {
    private String text;
    private LocalDate date;

    public JournalEntry(String t, LocalDate d){
        this.text = t;
        this.date = d;
    }

    public String getText() {
        return text;
    }

    public LocalDate getDate() {
        return date;
    }
}
