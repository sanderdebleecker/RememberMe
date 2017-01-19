package be.sanderdebleecker.herinneringsapp.Data;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.BaseRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.Crypto;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.User;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class DummyDA extends BaseRepository {
    //protected final String DATEFORMAT = "yyyy-MM-dd";

    public DummyDA(Context context) {
        super(context);
        createDummyData();
    }
    private void createDummyData() {
        List<User> dummyUsers = createUsers();
        List<Memory> dummyMemories = createMemories();
        List<Album> dummyAlbums = createAlbums();
        List<Session> dummySessions = createSessions();
        open();
        insertUsers(dummyUsers);
        insertMemories(dummyMemories);
        insertAlbums(dummyAlbums);
        insertSessions(dummySessions);
        close();
    }

    private void insertUsers(List<User> dummyUsers) {
        for(User dummy : dummyUsers) {
            insertUser(dummy);
        }
    }
    private int insertUser(User newUser) {
        //SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_USERS+" VALUES(NULL,?,?,?,?,?,?,?,?)");
        ContentValues values = new ContentValues();
        values.put(DbHelper.UserColumns.UserFirstName.toString(), newUser.getFirstName());
        values.put(DbHelper.UserColumns.UserLastName.toString(), newUser.getLastName());
        values.put(DbHelper.UserColumns.UserPassword.toString(), Crypto.md5(newUser.getPassword()));
        values.put(DbHelper.UserColumns.UserQuestion1.toString(), newUser.getQ1());
        values.put(DbHelper.UserColumns.UserQuestion2.toString(), newUser.getQ2());
        values.put(DbHelper.UserColumns.UserAnswer1.toString(), newUser.getA1());
        values.put(DbHelper.UserColumns.UserAnswer2.toString(), newUser.getA2());
        values.put(DbHelper.UserColumns.UserName.toString(), newUser.getUsername());
        int id = -1;
        try{
            id = (int) db.insert(dbh.TBL_USERS,"",values);
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return id;

    }
    private void insertMemories(List<Memory> dummyMemories) {
        for(Memory dummy : dummyMemories) {
            insertMemory(dummy);
        }
    }
    private boolean insertMemory(Memory newMemory) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_MEMORIES+" VALUES(NULL,?,?,?,NULL,NULL,NULL,?,?,?)");
        stmt.bindString(1, newMemory.getTitle());
        stmt.bindString(2, newMemory.getDescription());
        stmt.bindString(3, newMemory.getDate());
        stmt.bindLong(  4, newMemory.getCreator());
        stmt.bindString(5, newMemory.getPath());
        stmt.bindString(6, newMemory.getType());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    private void insertAlbums(List<Album> dummyAlbums) {
        insertCompleteAlbum(dummyAlbums.get(0),1,2);
        insertCompleteAlbum(dummyAlbums.get(1),3);
        insertCompleteAlbum(dummyAlbums.get(2),4);

    }
    private int insertCompleteAlbum(Album newAlbum, int... selMems) {
        db.beginTransaction();
        int album = insertAlbum(newAlbum);
        if(album!=-1) {
            if(insertAlbumMemories(album,selMems)) {
                db.setTransactionSuccessful();
            }
        }
        db.endTransaction();
        return album;
    }
    private int insertAlbum(Album newAlbum) {
        int id = -1;
        try{
            ContentValues cv = new ContentValues();
            cv.put(DbHelper.AlbumColumns.AlbumTitle.toString(), newAlbum.getName());
            cv.put(DbHelper.AlbumColumns.AlbumCreator.toString(), newAlbum.getAuthorId());
            cv.put(DbHelper.AlbumColumns.AlbumThumbnail.toString(), newAlbum.getThumbnail().getId());
            //execute
            id = (int) db.insert(dbh.TBL_ALBUMS,null,cv);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return id;
    }
    private boolean insertAlbumMemories(int albumId,int... memories) {
        boolean result = true;
        try{
            if(albumId==-1) {
                throw new SQLException("AMAlbum '"+albumId+"' does not exist yet, so no memories can be inserted (AlbumRepository.insertAlbumMemories)");
            }
            for(Integer mem : memories) {
                insertAlbumMemory(albumId, mem);
            }
        }catch(SQLException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    private void insertAlbumMemory(int albumId, Integer mem) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_ALBUMS_MEMORIES+" VALUES(NULL,?,?)");
        stmt.bindLong(1, albumId);
        stmt.bindLong(2, mem);
        stmt.execute();
    }
    private void insertSessions(List<Session> sessions) {
        for(Session dummy : sessions) {
            insertSession(dummy);
        }
    }
    private int insertSession(Session newSession) {
        int result;
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.SessionColumns.SessionName.toString(),newSession.getName());
        cv.put(DbHelper.SessionColumns.SessionDate.toString(),newSession.getDate());
        cv.put(DbHelper.SessionColumns.SessionDuration.toString(),newSession.getDuration());
        cv.put(DbHelper.SessionColumns.SessionCount.toString(),newSession.getCount());
        cv.put(DbHelper.SessionColumns.SessionIsFinished.toString(),(newSession.isFinished() ? 1 : 0));
        cv.put(DbHelper.SessionColumns.SessionAuthor.toString(),newSession.getAuthor());
        try{
            result = (int) db.insert(dbh.TBL_SESSIONS,null,cv);
        }catch(Exception e) {
            result = -1;
        }
        return result;

    }
    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        User u1 = new User();
        u1.setId(1);
        u1.setFirstName("Jeffrey");
        u1.setLastName("De Vries");
        u1.setQ1("Wat is je fav dier?");
        u1.setA1("Dolfijn");
        u1.setQ2("Wat is je lievelingsband?");
        u1.setA2("Coldplay");
        u1.setUsername("JDeVries");
        u1.setPassword("qwerty123");
        User u2 = new User();
        u2.setId(2);
        u2.setFirstName("Mia");
        u2.setLastName("Peeters");
        u2.setQ1("Geboorteplaats?");
        u2.setA1("Luik");
        u2.setQ2("Sterrenbeeld?");
        u2.setA2("Steenbook");
        u2.setUsername("MPeeters");
        u2.setPassword("qwerty123");
        User u3 = new User();
        u3.setId(2);
        u3.setFirstName("Sander");
        u3.setLastName("De Bleecker");
        u3.setQ1("azerty?");
        u3.setA1("fqsf465486");
        u3.setQ2("qwerty?");
        u3.setA2("dsfdsfdsddez89839");
        u3.setUsername("sanderdb");
        u3.setPassword("qwerty123");
        users.add(u1);
        users.add(u2);
        users.add(u3);
        return users;

    }
    private List<Memory> createMemories() {
        List<Memory> memories = new ArrayList<>();
        Memory m1 = new Memory();
        m1.setId(1);
        m1.setTitle("Mijn eerste herinnering");
        m1.setCreator(3);
        m1.setDate("1999-09-28");
        m1.setDescription("Details eerste herinnering...");
        m1.setLocation(new Location(53.5f,17.41f,"KBO Bevere"));
        Memory m2 = new Memory();
        m2.setId(2);
        m2.setTitle("Bevere");
        m2.setCreator(3);
        m2.setDate("2003-04-14");
        m2.setDescription("Chiro Jeugd");
        m2.setLocation(new Location(53.501f,17.412f,"Bevere"));
        Memory m3 = new Memory();
        m3.setId(3);
        m3.setTitle("Gent");
        m3.setCreator(3);
        m3.setDate("2006-02-23");
        m3.setDescription("Familie uitstap");
        m3.setLocation(new Location(53.54f,17.4f,"Gent"));
        Memory m4 = new Memory();
        m4.setId(4);
        m4.setTitle("Andere herinnering");
        m4.setCreator(1);
        m4.setDate("1973-12-16");
        m4.setDescription("Andere herinnering");
        m4.setLocation(new Location(53,17,"Hasselt"));
        memories.add(m1);
        memories.add(m2);
        memories.add(m3);
        memories.add(m4);
        return memories;
    }
    private List<Album> createAlbums() {
        List<Album> albums = new ArrayList<>();
        Album a1 = new Album();
        a1.setName("Album 1");
        a1.setAuthorId(3);
        a1.setId(1);
        Album a2 = new Album();
        a2.setName("Album 2");
        a2.setAuthorId(3);
        a2.setId(2);
        Album a3 = new Album();
        a3.setName("Album 3");
        a3.setAuthorId(1);
        a3.setId(3);

        albums.add(a1);
        albums.add(a2);
        albums.add(a3);
        return albums;

    }
    private List<Session> createSessions() {
        List<Session> sessions = new ArrayList<>();
        Session s1 = new Session("NewSession 1","2016-10-27",3);
        Session s2 = new Session("NewSession 2","2016-10-28",3);
        Session s3 = new Session("NewSession 3","2016-12-05",1);
        sessions.add(s1);
        sessions.add(s2);
        sessions.add(s3);
        return sessions;
    }
}
