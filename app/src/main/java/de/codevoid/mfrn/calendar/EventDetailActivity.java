package de.codevoid.mfrn.calendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

        TextView     tvTitle              = findViewById(R.id.tv_title);
        TextView     tvDate               = findViewById(R.id.tv_date);
        TextView     tvLocation           = findViewById(R.id.tv_location);
        TextView     tvDescription        = findViewById(R.id.tv_description);
        LinearLayout sectionParticipants  = findViewById(R.id.section_participants);
        TextView     tvParticipantsHeader = findViewById(R.id.tv_participants_header);
        TextView     tvParticipantsMeta   = findViewById(R.id.tv_participants_meta);
        LinearLayout participantList      = findViewById(R.id.participant_list);

        String url   = getIntent().getStringExtra(EXTRA_EVENT_URL);
        int    id    = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        String title = getIntent().getStringExtra(EXTRA_EVENT_TITLE);

        tvTitle.setText(title);
        tvDescription.setMovementMethod(LinkMovementMethod.getInstance());

        CalendarEvent event = new CalendarEvent(id, title, url, "", "");
        CalendarRepository repo = new CalendarRepository(((App) getApplication()).getClient());

        executor.execute(() -> {
            try {
                repo.loadDetail(event);
                runOnUiThread(() -> {
                    // Date
                    if (event.startDate != null) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(
                                FormatStyle.MEDIUM, FormatStyle.SHORT);
                        String dateStr = event.startDate.format(fmt);
                        if (event.endDate != null) dateStr += " – " + event.endDate.format(fmt);
                        tvDate.setText(dateStr);
                    }

                    // Location
                    if (event.location != null) {
                        tvLocation.setText(event.location);
                        tvLocation.setVisibility(View.VISIBLE);
                        tvLocation.setOnClickListener(v -> openInMaps(event.location));
                    }

                    // Description — render HTML with links and newlines
                    if (event.descriptionHtml != null && !event.descriptionHtml.isEmpty()) {
                        tvDescription.setText(Html.fromHtml(
                                event.descriptionHtml, Html.FROM_HTML_MODE_COMPACT));
                    }

                    // Participants section
                    if (event.participants != null && !event.participants.isEmpty()) {
                        String header = event.participantCount + " Teilnehmer";
                        if (event.maxParticipants > 0)
                            header += " / " + event.maxParticipants;
                        tvParticipantsHeader.setText(header);

                        if (event.registrationDeadline != null) {
                            tvParticipantsMeta.setText("Anmeldeschluss: " + event.registrationDeadline);
                            tvParticipantsMeta.setVisibility(View.VISIBLE);
                        }

                        for (String name : event.participants) {
                            TextView tv = new TextView(this);
                            tv.setText(name);
                            tv.setTextSize(14);
                            tv.setPadding(0, 6, 0, 6);
                            participantList.addView(tv);
                        }
                        sectionParticipants.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> tvDescription.setText(e.getMessage()));
            }
        });
    }

    private void openInMaps(String address) {
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
