package de.codevoid.mfrn;

import android.app.Application;
import de.codevoid.mfrn.net.MfrnClient;

public class App extends Application {

    private MfrnClient client;

    public MfrnClient getClient() {
        return client;
    }

    public void setClient(MfrnClient client) {
        this.client = client;
    }
}
