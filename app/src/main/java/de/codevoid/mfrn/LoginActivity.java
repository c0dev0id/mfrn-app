package de.codevoid.mfrn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.codevoid.mfrn.net.MfrnClient;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private CredentialStore credentialStore;

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvError;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin   = findViewById(R.id.btn_login);
        tvError    = findViewById(R.id.tv_error);
        progress   = findViewById(R.id.progress);

        try {
            credentialStore = new CredentialStore(this);
        } catch (Exception e) {
            // Keystore unavailable — fall through to manual login
        }

        btnLogin.setOnClickListener(v -> attemptLogin(
                etUsername.getText() != null ? etUsername.getText().toString().trim() : "",
                etPassword.getText() != null ? etPassword.getText().toString() : "",
                true
        ));
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnLogin.performClick();
                return true;
            }
            return false;
        });

        if (credentialStore != null && credentialStore.hasCredentials()) {
            setFormVisible(false);
            progress.setVisibility(View.VISIBLE);
            attemptLogin(credentialStore.getUsername(), credentialStore.getPassword(), false);
        }
    }

    private void attemptLogin(String username, String password, boolean saveOnSuccess) {
        if (username.isEmpty() || password.isEmpty()) return;

        setFormEnabled(false);
        tvError.setVisibility(View.GONE);

        MfrnClient client = new MfrnClient();
        executor.execute(() -> {
            try {
                boolean ok = client.login(username, password);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    if (ok) {
                        if (saveOnSuccess && credentialStore != null) {
                            credentialStore.save(username, password);
                        }
                        ((App) getApplication()).setClient(client);
                        startActivity(new Intent(this, MainActivity.class)
                                .putExtra(MainActivity.EXTRA_USERNAME, username));
                        finish();
                    } else {
                        if (credentialStore != null) credentialStore.clear();
                        setFormVisible(true);
                        setFormEnabled(true);
                        tvError.setText(R.string.error_login_failed);
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    setFormVisible(true);
                    setFormEnabled(true);
                    tvError.setText(e.getMessage());
                    tvError.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void setFormEnabled(boolean enabled) {
        etUsername.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
    }

    private void setFormVisible(boolean visible) {
        int v = visible ? View.VISIBLE : View.GONE;
        etUsername.setVisibility(v);
        etPassword.setVisibility(v);
        btnLogin.setVisibility(v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
