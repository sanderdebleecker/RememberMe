package be.sanderdebleecker.herinneringsapp.Helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//TODO REMEMBERME is not checked , every use is remembered even if never logged in !!!

//TODO catch exceptions once you re completed with all development/testing on SQLite
//TODO modern error frame
//TODO deletememoryfromttimeline() must take user as argument once multi-user
//TODO prevent TBL_TIMELINE already EXISTS on recreateDb()

//TODO CHANGE RESOURCES TO SESSIONSALBUMS FOR CLARITY SAKE
/*
* Creation and structure of the app's database
 */

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper mInstance = null;
    private static final int DB_VERSION = 6;
    private static final String DB_NAME="HerinneringsApp.db";
    public final String TBL_USERS="tbl_users";
    public final String TBL_MEMORIES="tbl_memories";
    public final String TBL_ALBUMS_MEMORIES="tbl_albums_memories";
    public final String TBL_SESSIONS_TESTS="tbl_sessions_tests";
    public final String TBL_TIMELINE="tbl_timeline";
    public final String TBL_TRUSTS="tbl_trustees";
    public final String TBL_COLLECTIONS="tbl_collections";
    public final String TBL_ALBUMS="tbl_albums";
    public final String TBL_SESSIONS="tbl_sessions";
    public final String TBL_RESOURCES ="tbl_resources";
    public final String TBL_GWQTEST = "tbl_gwqtest";

    public enum TimelineColumns {
        TimelineId,
        TimelineMemory,
        TimelineUser;
    }
    public enum UserColumns {
        UserId,
        UserFirstName,
        UserLastName,
        UserName,
        UserPassword,
        UserQuestion1,
        UserQuestion2,
        UserAnswer1,
        UserAnswer2;
        static String[] getColumns() {
            Object[] users = values();
            String[] res = new String[users.length];
            for (int i = 0; i < users.length; i++) {
                res[i] = users[i].toString();
            }
            return res;
        }
    }
    public enum MemoryColumns {
        MemoryId,
        MemoryTitle,
        MemoryDescription,
        MemoryDateTime,
        MemoryLocationLat,
        MemoryLocationLong,
        MemoryLocationName,
        MemoryCreator,
        MemoryPath,
        MemoryType;
        public static String[] getColumns() {
            Object[] memories = values();
            String[] res = new String[memories.length];
            for (int i = 0; i < memories.length; i++) {
                res[i] = memories[i].toString();
            }
            return res;
        }
    }
    public enum AlbumColumns {
        AlbumId,
        AlbumTitle,
        AlbumCreator,
        AlbumThumbnail;
        static String[] getColumns() {
            Object[] albums = values();
            String[] res = new String[albums.length];
            for (int i = 0; i < albums.length; i++) {
                res[i] = albums[i].toString();
            }
            return res;
        }
    }
    public enum AlbumsMemoriesColumns {
        AMId,
        AMAlbum,
        AMMemory,
    }
    private enum CollectionColumns {
        CollectionId,
        CollectionMemory,
        CollectionAlbum
    }
    public enum SessionColumns {
        SessionId,
        SessionName,
        SessionDate,
        SessionDuration,
        SessionCount,
        SessionIsFinished,
        SessionAuthor,
    }
    public enum ResourceColumns {
        ResourceId,
        ResourceSession,
        ResourceAlbum,
        }
    public enum TrustColumns {
        TrustId,
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

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public static synchronized DbHelper getInstance(Context context) {
        if(mInstance==null){
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        createTblUsers(db);
        createTblMemories(db);
        createTblAlbums(db);
        createTblSessions(db);
        createTblTrustees(db);
        createTblCollections(db);
        createTblResources(db);
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
        db.execSQL("DROP TABLE IF EXISTS "+TBL_COLLECTIONS);
        db.execSQL("DROP TABLE IF EXISTS "+TBL_RESOURCES);
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
            //TBL already exists...
        }
    }

    private void createTblSessionsTests(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_SESSIONS_TESTS+" ( "+
                SessionsTestsColumns.STId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SessionsTestsColumns.STSession + " INTEGER, "+
                SessionsTestsColumns.STTest + " INTEGER, "+
                SessionsTestsColumns.STTestType + " INTEGER, "+
                "FOREIGN KEY("+ SessionsTestsColumns.STSession +") REFERENCES "+TBL_SESSIONS+"("+ SessionColumns.SessionId +") "+
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
    private void createTblUsers(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_USERS+" ( "+
                UserColumns.UserId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                UserColumns.UserFirstName + " VARCHAR(100), "+
                UserColumns.UserLastName + " VARCHAR(100), "+
                UserColumns.UserName+ " VARCHAR(100), "+
                UserColumns.UserPassword + " VARCHAR(30), "+
                UserColumns.UserQuestion1 +" VARCHAR(100), "+
                UserColumns.UserQuestion2 +" VARCHAR(100), "+
                UserColumns.UserAnswer1 +" VARCHAR(100), "+
                UserColumns.UserAnswer2 +" VARCHAR(100) "+
                ")" );
    }
    private void createTblTimeline(SQLiteDatabase db ) {
        db.execSQL("CREATE TABLE "+TBL_TIMELINE+" ( "+
                TimelineColumns.TimelineId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TimelineColumns.TimelineMemory +" INTEGER, "+
                TimelineColumns.TimelineUser +" INTEGER, "+
                "FOREIGN KEY("+ TimelineColumns.TimelineMemory +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryId +"), "+
                "FOREIGN KEY("+ TimelineColumns.TimelineUser +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserId +") "+
                "UNIQUE ("+TimelineColumns.TimelineMemory +", "+TimelineColumns.TimelineUser +")"+
                ")" );
    }
    private void createTblMemories(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TBL_MEMORIES+" ( "+
                MemoryColumns.MemoryId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MemoryColumns.MemoryTitle + " VARCHAR(100), "+
                MemoryColumns.MemoryDescription + " VARCHAR(600), "+
                MemoryColumns.MemoryDateTime + " VARCHAR(50), "+
                MemoryColumns.MemoryLocationLat + " DOUBLE, "+
                MemoryColumns.MemoryLocationLong + " DOUBLE, "+
                MemoryColumns.MemoryLocationName + " VARCHAR(100), "+
                MemoryColumns.MemoryCreator + " INTEGER, "+
                MemoryColumns.MemoryPath + " VARCHAR(500), "+
                MemoryColumns.MemoryType + " VARCHAR(50), "+
                "FOREIGN KEY("+ MemoryColumns.MemoryCreator +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserId +")"+
                ")" );
    }
    private void createTblTrustees(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_TRUSTS +" ( "+
                TrustColumns.TrustId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TrustColumns.TrustSource + " INTEGER, "+
                TrustColumns.TrustDestination + " INTEGER"+
                ")" );
    }
    private void createTblAlbums(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_ALBUMS +" ( "+
                AlbumColumns.AlbumId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                AlbumColumns.AlbumTitle + " VARCHAR(100), "+
                AlbumColumns.AlbumCreator + " INTEGER, "+
                AlbumColumns.AlbumThumbnail + " INTEGER, "+
                "FOREIGN KEY("+ AlbumColumns.AlbumCreator +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserId +"), "+
                "FOREIGN KEY("+ AlbumColumns.AlbumThumbnail +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryId +")"+
                ")" );
    }
    private void createTblAlbumsMemories(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_ALBUMS_MEMORIES +" ( "+
                AlbumsMemoriesColumns.AMId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                AlbumsMemoriesColumns.AMAlbum + " INTEGER, "+
                AlbumsMemoriesColumns.AMMemory + " INTEGER, "+
                "FOREIGN KEY("+ AlbumsMemoriesColumns.AMAlbum +") REFERENCES "+TBL_ALBUMS+"("+AlbumColumns.AlbumId +"), "+
                "FOREIGN KEY("+ AlbumsMemoriesColumns.AMMemory +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryId +")"+
                ")" );
    }
    private void createTblCollections(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBL_COLLECTIONS +" ( "+
                CollectionColumns.CollectionId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                CollectionColumns.CollectionMemory + " INTEGER, "+
                CollectionColumns.CollectionAlbum + " INTEGER, "+
                "FOREIGN KEY("+ CollectionColumns.CollectionMemory +") REFERENCES "+TBL_MEMORIES+"("+ MemoryColumns.MemoryId +"), "+
                "FOREIGN KEY("+ CollectionColumns.CollectionAlbum +") REFERENCES "+TBL_ALBUMS+"("+ AlbumColumns.AlbumId +")"+
                ")" );
    }
    private void createTblSessions(SQLiteDatabase db)  {
        db.execSQL("CREATE TABLE "+ TBL_SESSIONS + " ( "+
                SessionColumns.SessionId +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SessionColumns.SessionName+ " VARCHAR(100), "+
                SessionColumns.SessionDate +" INTEGER, "+
                SessionColumns.SessionDuration +" INTEGER, "+
                SessionColumns.SessionCount +" INTEGER, "+
                SessionColumns.SessionIsFinished +" INTEGER, "+
                SessionColumns.SessionAuthor + " INTEGER, "+
                "FOREIGN KEY("+ SessionColumns.SessionAuthor +") REFERENCES "+TBL_USERS+"("+ UserColumns.UserId +")"+
                ")" );
    }
    private void createTblResources(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL_RESOURCES + " ( " +
                ResourceColumns.ResourceId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ResourceColumns.ResourceSession + " INTEGER, " +
                ResourceColumns.ResourceAlbum +" INTEGER, "+
                "FOREIGN KEY("+ ResourceColumns.ResourceSession +") REFERENCES "+TBL_SESSIONS+"("+ SessionColumns.SessionId +"), "+
                "FOREIGN KEY("+ ResourceColumns.ResourceAlbum +") REFERENCES "+TBL_ALBUMS+"("+ AlbumColumns.AlbumId +")"+
                ")" );
    }


}
