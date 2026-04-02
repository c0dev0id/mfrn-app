package de.codevoid.mfrn.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP client for the MFRN WoltLab forum.
 *
 * Login flow (double-submit CSRF pattern):
 *   1. GET /wcf/login/  → XSRF-TOKEN cookie is set
 *   2. POST /wcf/login/ → send username, password, and t=<XSRF-TOKEN value>
 *   3. 302 redirect     → wcf_user_session cookie is now authenticated
 */
public class MfrnClient {

    private static final String BASE_URL = "https://motorradfreunde-rheinneckar.de";
    private static final String LOGIN_URL = BASE_URL + "/wcf/login/";

    private final OkHttpClient http;
    // Keyed by host → cookie name → cookie. Map-of-maps so each response
    // only updates the cookies it sets, without wiping unrelated ones.
    private final Map<String, Map<String, Cookie>> cookieStore = new HashMap<>();

    public MfrnClient() {
        http = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Map<String, Cookie> jar = cookieStore.computeIfAbsent(
                                url.host(), k -> new HashMap<>());
                        for (Cookie c : cookies) jar.put(c.name(), c);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        Map<String, Cookie> jar = cookieStore.get(url.host());
                        return jar != null ? new ArrayList<>(jar.values()) : new ArrayList<>();
                    }
                })
                .followRedirects(false)
                .build();
    }

    /**
     * Authenticates against the forum. Returns true on success.
     */
    public boolean login(String username, String password) throws IOException {
        // Step 1: GET login page to obtain XSRF-TOKEN cookie
        Request getReq = new Request.Builder().url(LOGIN_URL).get().build();
        try (Response getResp = http.newCall(getReq).execute()) {
            if (!getResp.isSuccessful() && getResp.code() != 302) {
                return false;
            }
        }

        // Step 2: extract XSRF-TOKEN from cookie store
        String xsrfToken = getCookieValue("motorradfreunde-rheinneckar.de", "XSRF-TOKEN");
        if (xsrfToken == null) return false;

        // Step 3: POST login
        // xsrfToken is the raw cookie value (already percent-encoded from Set-Cookie header).
        // addEncoded() avoids double-encoding; the server decodes once and matches its stored token.
        FormBody form = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .addEncoded("t", xsrfToken)
                .build();

        Request postReq = new Request.Builder().url(LOGIN_URL).post(form).build();
        try (Response postResp = http.newCall(postReq).execute()) {
            // WoltLab returns 302 on success
            return postResp.code() == 302;
        }
    }

    /**
     * Returns an authenticated OkHttpClient for use by feature modules.
     * Call login() before using this.
     */
    public OkHttpClient getHttpClient() {
        return http;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }

    private String getCookieValue(String host, String name) {
        Map<String, Cookie> jar = cookieStore.get(host);
        if (jar == null) return null;
        Cookie c = jar.get(name);
        return c != null ? c.value() : null;
    }
}
