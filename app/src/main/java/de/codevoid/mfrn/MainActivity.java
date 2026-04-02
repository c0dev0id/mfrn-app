package de.codevoid.mfrn;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        TextView tv = findViewById(R.id.tv_welcome);
        tv.setText(getString(R.string.welcome, username));
    }
}
