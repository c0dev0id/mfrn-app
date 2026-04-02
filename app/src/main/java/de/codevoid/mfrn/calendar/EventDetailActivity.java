package de.codevoid.mfrn.calendar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.codevoid.mfrn.App;
import de.codevoid.mfrn.R;

public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID    = "event_id";
    public static final String EXTRA_EVENT_URL   = "event_url";
    public static final String EXTRA_EVENT_TITLE = "event_title";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_EVENT_TITLE));
        }

        TextView tvTitle        = findViewById(R.id.tv_title);
        TextView tvDate         = findViewById(R.id.tv_date);
        TextView tvParticipants = findViewById(R.id.tv_participants);
        TextView tvDescription  = findViewById(R.id.tv_description);

        String url   = getIntent().getStringExtra(EXTRA_EVENT_URL);
        int    id    = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        String title = getIntent().getStringExtra(EXTRA_EVENT_TITLE);

        tvTitle.setText(title);

        CalendarEvent event = new CalendarEvent(id, title, url, "", "");
        App app = (App) getApplication();
        CalendarRepository repo = new CalendarRepository(app.getClient());

        executor.execute(() -> {
            try {
                repo.loadDetail(event);
                runOnUiThread(() -> {
                    DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
                    if (event.startDate != null) {
                        String dateStr = event.startDate.format(fmt);
                        if (event.endDate != null) dateStr += " – " + event.endDate.format(fmt);
                        tvDate.setText(dateStr);
                    }
                    if (event.participantCount > 0) {
                        tvParticipants.setText(event.participantCount + " Teilnehmer");
                    }
                    tvDescription.setText(event.description);
                });
            } catch (Exception e) {
                runOnUiThread(() -> tvDescription.setText(e.getMessage()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
