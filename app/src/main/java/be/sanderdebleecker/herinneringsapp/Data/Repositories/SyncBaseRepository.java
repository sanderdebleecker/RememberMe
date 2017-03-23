package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.SyncDbHelper;

public class SyncBaseRepository {
    protected SQLiteDatabase db;
    protected SyncDbHelper dbh;

    public SyncBaseRepository(Context context) {
        dbh = SyncDbHelper.getInstance(context);
    }

    public void open() throws SQLException {
        db = dbh.getWritableDatabase();
    }
    public void close() {
        dbh.close();
    }
}
