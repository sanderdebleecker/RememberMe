package be.sanderdebleecker.herinneringsapp.Helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//TODO deletememoryfromttimeline() must take user as argument once multi-user

/*
* Creation and structure of the app's database
 */

public class MemoriesDbHelper extends SQLiteOpenHelper {
    private static MemoriesDbHelper mInstance = null;
    private static final int DB_VERSION = 8;
    private static final String DB_NAME="Memories.db";
    public final String TBL_USERS="tbl_users";
    public final String TBL_MEMORIES="tbl_memories";
    public final String TBL_ALBUMS_MEMORIES="tbl_albums_memories";
    public final String TBL_SESSIONS_TESTS="tbl_sessions_tests";
    public final String TBL_SESSIONS_ALBUMS ="tbl_sessions_albums";
    public final String TBL_TIMELINE="tbl_timeline";
    public final String TBL_TRUSTS="tbl_trustees";
    public final String TBL_ALBUMS="tbl_albums";
    public final String TBL_SESSIONS="tbl_sessions";
    public final String TBL_GWQTEST = "tbl_gwqtest";

    public enum TimelineColumns {
        TimelineUuid,
        TimelineMemory,
        TimelineUser;
    }
    public enum UserColumns {
        UserUuid,
        UserFirstName,
        UserLastName,
        UserName,
        UserPassword,
        UserQuestion1,
        UserQuestion2,
        UserAnswer1,
        UserAnswer2;
    }
    public enum MemoryColumns {
        MemoryUuid,
        MemoryTitle,
        MemoryDescription,
        MemoryDateTime,
        MemoryLocationLat,
        MemoryLocationLong,
        MemoryLocationName,
        MemoryCreator,
        MemoryPath,
        MemoryType;
    }
    public enum AlbumColumns {
        AlbumUuid,
        AlbumTitle,
        AlbumCreator,
        AlbumThumbnail;
    }
    public enum AlbumsMemoriesColumns {
        AMId,
        AMAlbum,
        AMMemory,
    }
    public enum SessionColumns {
        SessionUuid,
        SessionName,
        SessionDate,
        SessionNotes,
        SessionDuration,
        SessionCount,
        SessionIsFinished,
        SessionAuthor,
    }
    public enum SessionsAlbumsColumns {
        SAId,
        SASession,
        SAAlbum,
        }
    public enum TrustColumns {
        TrustUuid,
        TrustSource,
        TrustDestination,
    }
    public enum SessionsTestsColumns {
        STId,
        STSession,
        STTest,
        STTestType,
    }
    private enum GWQTestColumns {
        GWQId,
        GWQHappiness,
        GWQEngagement,
        GWQComfortableness,
        GWQSafety,
        GWQSociableness,
        GWQTalkativeness,
    }
    public enum TestType {
        GWQ;
    }

    private MemoriesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public static synchronized MemoriesDbHelper getInstance(Context context) {
        if(mInstance==null){
            mInstance = new MemoriesDbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        createTblUsers(db);
        createTblMemories(db);
        createTblAlbums(db);
        createTblSessions(db);
        createTblTrustees(db);
        createTblSessionsAlbums(db);
        createTblTimeline(db);
        createTblAlbumsMemories(db);
        createTblSessionsTests(db);
        createTblGWQTest(db);

        createDummyData();
    }
    private void createDummyData() {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDb(db);
    }
    public void dropDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TBL_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_MEMORIES);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_SESSIONS_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_TRUSTS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_ALBUMS_MEMORIES);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_SESSIONS_TESTS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_TIMELINE);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_GWQTEST);
    }
    public void recreateDb(SQLiteDatabase db){
        dropDb(db);
        try {
            onCreate(db);
        }catch(SQLException ex) {
            ex.printStackTrace();
            //TBL already getIdentifier...
        }
    }

    private void createTblUsers(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_USERS+" ( "+
                UserColumns.UserUuid +" BLOB PRIMARY KEY, "+
                UserColumns.UserFirstName + " VARCHAR(100), "+
                UserColumns.UserLastName + " VARCHAR(100), "+
                UserColumns.UserName+ " VARCHAR(100), "+
                UserColumns.UserPassword + " VARCHAR(32), "+
                UserColumns.UserQuestion1 +" VARCHAR(100), "+
                UserColumns.UserQuestion2 +" VARCHAR(100), "+
                UserColumns.UserAnswer1 +" VARCHAR(100), "+
                UserColumns.UserAnswer2 +" VARCHAR(100) "+
                ")" );
    }
    private void createTblTrustees(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_TRUSTS +" ( "+
                TrustColumns.TrustUuid +" BLOB PRIMARY KEY, "+
                TrustColumns.TrustSource + " BLOB, "+
                TrustColumns.TrustDestination + " BLOB"+
                ")" );
    }
    private void createTblMemories(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_MEMORIES+" ( "+
                MemoryColumns.MemoryUuid +" BLOB PRIMARY KEY, "+
                MemoryColumns.MemoryTitle + " VARCHAR(100), "+
                MemoryColumns.MemoryDescription + " VARCHAR(600), "+
                MemoryColumns.MemoryDateTime + " VARCHAR(50), "+
                MemoryColumns.MemoryLocationLat + " DOUBLE, "+
                MemoryColumns.MemoryLocationLong + " DOUBLE, "+
                MemoryColumns.MemoryLocationName + " VARCHAR(100), "+
                MemoryColumns.MemoryCreator + " BLOB, "+
                MemoryColumns.MemoryPath + " VARCHAR(500), "+
                MemoryColumns.MemoryType + " VARCHAR(50), "+
                "FOREIGN KEY("+ MemoryColumns.MemoryCreator +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserUuid +")"+
                ")" );
    }
    private void createTblTimeline(SQLiteDatabase db ) {
        db.execSQL("CREATE TABLE "+TBL_TIMELINE+" ( "+
                TimelineColumns.TimelineUuid +" BLOB PRIMARY KEY, "+
                TimelineColumns.TimelineMemory +" BLOB, "+
                TimelineColumns.TimelineUser +" BLOB, "+
                "FOREIGN KEY("+ TimelineColumns.TimelineMemory +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryUuid +"), "+
                "FOREIGN KEY("+ TimelineColumns.TimelineUser +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserUuid +") "+
                "UNIQUE ("+TimelineColumns.TimelineMemory +", "+TimelineColumns.TimelineUser +")"+
                ")" );
    }
    private void createTblAlbums(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_ALBUMS +" ( "+
                AlbumColumns.AlbumUuid +" BLOB PRIMARY KEY, "+
                AlbumColumns.AlbumTitle + " VARCHAR(100), "+
                AlbumColumns.AlbumCreator + " BLOB, "+
                AlbumColumns.AlbumThumbnail + " BLOB, "+
                "FOREIGN KEY("+ AlbumColumns.AlbumCreator +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserUuid +"), "+
                "FOREIGN KEY("+ AlbumColumns.AlbumThumbnail +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryUuid +")"+
                ")" );
    }
    private void createTblSessions(SQLiteDatabase db)  {
        db.execSQL("CREATE TABLE "+ TBL_SESSIONS + " ( "+
                SessionColumns.SessionUuid +" BLOB PRIMARY KEY, "+
                SessionColumns.SessionName+ " VARCHAR(100), "+
                SessionColumns.SessionDate +" INTEGER, "+
                SessionColumns.SessionNotes+" VARCHAR(500), "+
                SessionColumns.SessionDuration +" INTEGER, "+
                SessionColumns.SessionCount +" INTEGER, "+
                SessionColumns.SessionIsFinished +" INTEGER, "+
                SessionColumns.SessionAuthor + " BLOB, "+
                "FOREIGN KEY("+ SessionColumns.SessionAuthor +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserUuid +")"+
                ")" );
    }
    private void createTblAlbumsMemories(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_ALBUMS_MEMORIES +" ( "+
                AlbumsMemoriesColumns.AMId +" BLOB PRIMARY KEY, "+
                AlbumsMemoriesColumns.AMAlbum + " BLOB, "+
                AlbumsMemoriesColumns.AMMemory + " BLOB, "+
                "FOREIGN KEY("+ AlbumsMemoriesColumns.AMAlbum +") REFERENCES "+TBL_ALBUMS+"("+AlbumColumns.AlbumUuid +"), "+
                "FOREIGN KEY("+ AlbumsMemoriesColumns.AMMemory +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryUuid +")"+
                ")" );
    }
    private void createTblSessionsAlbums(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL_SESSIONS_ALBUMS + " ( " +
                SessionsAlbumsColumns.SAId + " BLOB PRIMARY KEY, " +
                SessionsAlbumsColumns.SASession + " BLOB, " +
                SessionsAlbumsColumns.SAAlbum +" BLOB, "+
                "FOREIGN KEY("+ SessionsAlbumsColumns.SASession +") REFERENCES "+TBL_SESSIONS+"("+ SessionColumns.SessionUuid +"), "+
                "FOREIGN KEY("+ SessionsAlbumsColumns.SAAlbum +") REFERENCES "+TBL_ALBUMS+"("+ AlbumColumns.AlbumUuid +")"+
                ")" );
    }
    private void createTblSessionsTests(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_SESSIONS_TESTS+" ( "+
                SessionsTestsColumns.STId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SessionsTestsColumns.STSession + " BLOB, "+
                SessionsTestsColumns.STTest + " INTEGER, "+
                SessionsTestsColumns.STTestType + " INTEGER, "+
                "FOREIGN KEY("+ SessionsTestsColumns.STSession +") REFERENCES "+TBL_SESSIONS+"("+ SessionColumns.SessionUuid +") "+
                ")" );
    }
    private void createTblGWQTest(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_GWQTEST+" ( "+
                GWQTestColumns.GWQId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                GWQTestColumns.GWQHappiness + " INTEGER, "+
                GWQTestColumns.GWQEngagement + " INTEGER, "+
                GWQTestColumns.GWQComfortableness + " INTEGER, "+
                GWQTestColumns.GWQSafety + " INTEGER, "+
                GWQTestColumns.GWQSociableness + " INTEGER, "+
                GWQTestColumns.GWQTalkativeness + " INTEGER"+
                ")" );
    }


}
