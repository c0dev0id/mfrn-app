package de.codevoid.mfrn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_calendar) {
                // TODO: show calendar fragment
                return true;
            } else if (id == R.id.nav_messages) {
                // TODO: show messages fragment
                return true;
            } else if (id == R.id.nav_filebase) {
                // TODO: show filebase fragment
                return true;
            }
            return false;
        });
    }
}
