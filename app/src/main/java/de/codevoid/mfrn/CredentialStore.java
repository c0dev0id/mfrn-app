package de.codevoid.mfrn;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class CredentialStore {

    private static final String FILE_NAME = "credentials";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences prefs;

    public CredentialStore(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        prefs = EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void save(String username, String password) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public String getUsername() { return prefs.getString(KEY_USERNAME, null); }
    public String getPassword() { return prefs.getString(KEY_PASSWORD, null); }
    public boolean hasCredentials() { return getUsername() != null && getPassword() != null; }

    public void clear() {
        prefs.edit().remove(KEY_USERNAME).remove(KEY_PASSWORD).apply();
    }
}
