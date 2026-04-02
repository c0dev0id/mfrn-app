package de.codevoid.mfrn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import de.codevoid.mfrn.calendar.CalendarFragment;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if      (id == R.id.nav_calendar) f = new CalendarFragment();
            else if (id == R.id.nav_messages) f = new PlaceholderFragment("Nachrichten");
            else if (id == R.id.nav_filebase) f = new PlaceholderFragment("Touren");
            if (f == null) return false;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f).commit();
            return true;
        });

        // Start on calendar
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_calendar);
        }
    }
}
