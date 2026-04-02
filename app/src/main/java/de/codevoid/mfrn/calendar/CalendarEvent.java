package de.codevoid.mfrn.calendar;

import java.time.OffsetDateTime;

public class CalendarEvent {
    public final int id;
    public final String title;
    public final String url;
    public final String dateLabel;       // German display string from list page
    public final String author;

    // Populated on detail fetch
    public OffsetDateTime startDate;
    public OffsetDateTime endDate;
    public String description;
    public int participantCount;

    public CalendarEvent(int id, String title, String url, String dateLabel, String author) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.dateLabel = dateLabel;
        this.author = author;
    }
}
