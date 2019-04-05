package productivityplanner.data;

import java.time.LocalDate;

// Represents a single journal entry which contains the entry's date and text.
public class JournalEntry {
    private String text;                            // The main body of the journal
    private LocalDate date;                         // The date which the Journal is on

    // Constructor for all variables
    public JournalEntry(String t, LocalDate d){
        this.text = t;
        this.date = d;
    }

    // Getters for the variables
    public String getText() {
        return text;
    }
    public LocalDate getDate() {
        return date;
    }
}
