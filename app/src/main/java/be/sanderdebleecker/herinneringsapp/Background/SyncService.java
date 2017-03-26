package be.sanderdebleecker.herinneringsapp.Background;

import android.app.IntentService;
import android.content.Intent;

public class SyncService extends IntentService {
    public SyncService() {
        super("SyncService "+System.currentTimeMillis());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        /*
        * What needs to happen?
        * 1. check if MemorySync Exists !=> create
        * 2. getEntries
        * 3. ResolveEntries
         */
        System.out.println("Something");
    }
}
