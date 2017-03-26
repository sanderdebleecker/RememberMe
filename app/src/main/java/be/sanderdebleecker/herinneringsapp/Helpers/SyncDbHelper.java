package be.sanderdebleecker.herinneringsapp.Helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sander De Bleecker on 22/03/2017.
 *
 * A local database for keeping track of offline changes
 * Every uuid & type of an entity that changed & will have to be update,
 * is kept in this database.
 *
 * As soon as the server receives the changes the entries are removed
 */

public class SyncDbHelper extends SQLiteOpenHelper {
    //fields
    private static SyncDbHelper mInstance = null;
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "changes.db";
    public final String TBL_ENTITY ="tbl_entity";
    //column enums
    public enum EntityColumns {
        EUuid,
        EType,
    }
    public SyncDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public static synchronized SyncDbHelper getInstance(Context context) {
        if(mInstance==null){
            mInstance = new SyncDbHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableEntity(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDb(db);
    }
    private void createTableEntity(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_ENTITY+" ( "+
                EntityColumns.EUuid +" BLOB, "+
                EntityColumns.EType + " INTEGER, "+
                ")" );
    }
    public void recreateDb(SQLiteDatabase db){
        dropDb(db);
        try {
            onCreate(db);
        }catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void dropDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TBL_ENTITY);

    }


}
