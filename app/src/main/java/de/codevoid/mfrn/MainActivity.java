package de.codevoid.mfrn;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.codevoid.mfrn.net.MfrnClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MfrnClient client;

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

        client = new MfrnClient();
        // TODO: show login screen, pass credentials from user input

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
