package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
/*
* Logic that every repository will have
* */
public class BaseRepository {
    protected SQLiteDatabase db;
    protected MemoriesDbHelper dbh;

    public BaseRepository(Context context) {
        dbh = MemoriesDbHelper.getInstance(context);
    }

    public void open() throws SQLException {
        db = dbh.getWritableDatabase();
    }
    public void close() {
        dbh.close();
    }
}
