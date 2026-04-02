package de.codevoid.mfrn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.codevoid.mfrn.net.MfrnClient;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MfrnClient client;

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = new MfrnClient();
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        tvError = findViewById(R.id.tv_error);

        btnLogin.setOnClickListener(v -> attemptLogin());
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (username.isEmpty() || password.isEmpty()) return;

        setFormEnabled(false);
        tvError.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                boolean ok = client.login(username, password);
                runOnUiThread(() -> {
                    if (ok) {
                        ((App) getApplication()).setClient(client);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_USERNAME, username);
                        startActivity(intent);
                        finish();
                    } else {
                        tvError.setText(R.string.error_login_failed);
                        tvError.setVisibility(View.VISIBLE);
                        setFormEnabled(true);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvError.setText(e.getMessage());
                    tvError.setVisibility(View.VISIBLE);
                    setFormEnabled(true);
                });
            }
        });
    }

    private void setFormEnabled(boolean enabled) {
        etUsername.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
