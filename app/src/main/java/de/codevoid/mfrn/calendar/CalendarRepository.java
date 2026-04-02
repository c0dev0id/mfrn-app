package de.codevoid.mfrn.calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import de.codevoid.mfrn.net.MfrnClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarRepository {

    private static final String UPCOMING_URL =
            "https://motorradfreunde-rheinneckar.de/calendar/upcoming-event-list/";

    private final MfrnClient client;

    public CalendarRepository(MfrnClient client) {
        this.client = client;
    }

    public List<CalendarEvent> getUpcomingEvents() throws IOException {
        String html = fetch(UPCOMING_URL);
        Document doc = Jsoup.parse(html);
        List<CalendarEvent> events = new ArrayList<>();

        for (Element item : doc.select("li.calendarEventListItem, article.calendarEvent, li:has(a.calendarEventLink)")) {
            Element link = item.selectFirst("a.calendarEventLink");
            if (link == null) continue;

            int id = parseInt(link.attr("data-object-id"));
            String title = link.text().trim();
            String url = link.absUrl("href");

            // Date is in the <p> immediately after the h3
            String dateLabel = "";
            Element p = item.selectFirst("h3 + p");
            if (p != null) dateLabel = p.text().trim();

            // Author is the first userLink in the item
            String author = "";
            Element authorEl = item.selectFirst("a.userLink");
            if (authorEl != null) author = authorEl.text().trim();

            events.add(new CalendarEvent(id, title, url, dateLabel, author));
        }
        return events;
    }

    public void loadDetail(CalendarEvent event) throws IOException {
        String html = fetch(event.url);
        Document doc = Jsoup.parse(html);

        // JSON-LD gives us clean ISO dates, description, and location
        for (Element script : doc.select("script[type=application/ld+json]")) {
            try {
                JSONObject ld = new JSONObject(script.html());
                if (!"Event".equals(ld.optString("@type"))) continue;

                String start = ld.optString("startDate", null);
                String end   = ld.optString("endDate", null);
                if (start != null) event.startDate = OffsetDateTime.parse(start);
                if (end   != null) event.endDate   = OffsetDateTime.parse(end);

                // Description may contain HTML — strip tags
                String desc = ld.optString("description", "");
                event.description = Jsoup.parse(desc).text();

                // Location: only set for Place, not VirtualLocation
                JSONObject loc = ld.optJSONObject("location");
                if (loc != null && "Place".equals(loc.optString("@type"))) {
                    String addr = loc.optString("address", "").trim();
                    if (!addr.isEmpty()) event.location = addr;
                }
                break;
            } catch (Exception ignored) {}
        }

        // Participant count from dl/dt/dd structure — more reliable than full-text search
        Elements dts = doc.select("dt");
        for (Element dt : dts) {
            if (dt.text().trim().equals("Teilnehmer")) {
                Element dd = dt.nextElementSibling();
                if (dd != null && dd.tagName().equals("dd")) {
                    event.participantCount = parseInt(dd.text().trim());
                }
                break;
            }
        }
    }

    private String fetch(String url) throws IOException {
        Request req = new Request.Builder().url(url).build();
        try (Response resp = client.getHttpClient().newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new IOException("HTTP " + resp.code() + " for " + url);
            return resp.body().string();
        }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }
}
