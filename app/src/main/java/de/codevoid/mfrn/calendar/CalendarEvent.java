package de.codevoid.mfrn.calendar;

import java.time.OffsetDateTime;
import java.util.List;

public class CalendarEvent {
    public final int id;
    public final String title;
    public final String url;
    public final String dateLabel;       // German display string from list page
    public final String author;

    // Populated on detail fetch
    public OffsetDateTime startDate;
    public OffsetDateTime endDate;
    public String descriptionHtml;       // raw HTML from JSON-LD
    public int participantCount;
    public List<String> participants;
    public String location;  // null if no physical location set

    public CalendarEvent(int id, String title, String url, String dateLabel, String author) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.dateLabel = dateLabel;
        this.author = author;
    }
}
