package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Session;
import be.sanderdebleecker.herinneringsapp.Models.View.SessionVM;

/**
 * Sander De Bleecker
 */

/**
 * Provides base methods to access TBL_SESSIONS in the local database
 */

public class SessionRepository extends BaseRepository {
    public SessionRepository(Context context) {
        super(context);
    }

    /**
     * Inserts a new session into the local database
     * @param session Session to be inserted
     * @return String uuid of the new session
     */
    public String insert(Session session) {
        String identifier= UUID.randomUUID().toString().replaceAll("-","");
        if (session == null) return "";
        ContentValues cv = new ContentValues();
        cv.put(MemoriesDbHelper.SessionColumns.SessionUuid.toString(),"X'"+identifier+"'");
        cv.put(MemoriesDbHelper.SessionColumns.SessionName.toString(),session.getName());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDate.toString(),session.getDate());
        cv.put(MemoriesDbHelper.SessionColumns.SessionNotes.toString(),session.getNotes());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDuration.toString(),session.getDuration());
        cv.put(MemoriesDbHelper.SessionColumns.SessionCount.toString(),session.getCount());
        cv.put(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString(), (session.isFinished() ? 1 : 0));
        cv.put(MemoriesDbHelper.SessionColumns.SessionAuthor.toString(),session.getAuthor());
        try{
            db.insert(dbh.TBL_SESSIONS,null,cv);
        } catch(SQLException e) {
            e.printStackTrace();
            identifier = "";
        } catch(Exception e) {
            identifier = "";
        }
        return identifier;
    }

    /**
     * Associates session with albums in the local database
     * @param sessionIdentifier uuid of the session
     * @param albums List<String> albums to be associated
     * @return boolean success
     */
    protected boolean insertSessionAlbums(String sessionIdentifier,List<String> albums) {
        boolean result = true;
        for(String album : albums) {
            if(!insertSessionAlbum(sessionIdentifier,album)){
                result = false;
            }
        }
        return result;
    }

    /**
     * Associates a session with an album in the local database
     * @param sessionIdentifier uuid of the session
     * @param albumIdentifier uuid of the album
     * @return boolean success
     */
    protected boolean insertSessionAlbum(String sessionIdentifier,String albumIdentifier) {
        boolean result = false;
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_SESSIONS_ALBUMS+" VALUES(NULL,?,?)");
        stmt.bindString(1, sessionIdentifier);
        stmt.bindString(2, albumIdentifier);
        try {
            stmt.execute();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Queries the local database for albums from a session
     * @param sessionIdentifier String uuid of session
     * @return List<String> uuids of corresponding albums
     */
    public List<String> getAlbums(String sessionIdentifier) {
        List<String> albums = new ArrayList<>();
        Cursor cursor = null;
        String sql = "SELECT "+ MemoriesDbHelper.SessionsAlbumsColumns.SAAlbum +
                " FROM "+dbh.TBL_SESSIONS_ALBUMS+
                " WHERE "+ MemoriesDbHelper.SessionsAlbumsColumns.SASession +"=?";
        try{
            cursor =  db.rawQuery(sql,new String[]{ sessionIdentifier });
            while(cursor.moveToNext()) {
                albums.add(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionsAlbumsColumns.SAAlbum.toString())));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        } finally {
            cursor.close();
        }
        return albums;
    }

    /**
     * Queries the local database for a session
     * @param sessionIdentifier String the uuid of the Session
     * @param userIdentifier String the uuid of the User
     * @return Session datamodel
     */
    public Session get(String sessionIdentifier,String userIdentifier) {
        //TODO getauthorname from tblusers
        Session session = null;
        Cursor cursor;
        String sql = "SELECT *"+
                " FROM " + dbh.TBL_SESSIONS+
                " WHERE " + MemoriesDbHelper.SessionColumns.SessionUuid +"=?"+
                " AND " + MemoriesDbHelper.SessionColumns.SessionAuthor + "=?" ;
        try{
            cursor =  db.rawQuery(sql,new String[]{ sessionIdentifier, userIdentifier });
            while(cursor.moveToNext()) {
                session = from(cursor);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return session;
    }

    /**
     * Queries the local database for sessions
     * @param userIdentifier String uuid of user
     * @return List<SessionVM> Session Viewmodels
     */
    protected List<SessionVM> get(String userIdentifier) {
        List<SessionVM> sessions = new ArrayList<>();
        Cursor cursor;
        String sql = " SELECT s."+ MemoriesDbHelper.SessionColumns.SessionUuid +
                ", s." + MemoriesDbHelper.SessionColumns.SessionName +
                ", u." + MemoriesDbHelper.UserColumns.UserName +
                ", s." + MemoriesDbHelper.SessionColumns.SessionDuration +
                ", s." + MemoriesDbHelper.SessionColumns.SessionDate +
                " FROM " + dbh.TBL_SESSIONS + " s"+
                " LEFT OUTER JOIN " + dbh.TBL_USERS + " u"+
                " ON s."+ MemoriesDbHelper.SessionColumns.SessionAuthor+" = u."+ MemoriesDbHelper.UserColumns.UserUuid+
                " WHERE " + MemoriesDbHelper.SessionColumns.SessionAuthor + "=?";
        try{
            cursor =  db.rawQuery(sql,new String[]{ userIdentifier });
            while(cursor.moveToNext()) {
                sessions.add(viewModelFrom(cursor));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return sessions;

    }

    /**
     * Updates a session in the local database
     * @param session Session to be updated
     * @return boolean success
     */
    protected boolean updateSession(Session session) {
        int rowsAffected = -1;
        ContentValues cv = new ContentValues();
        cv.put(MemoriesDbHelper.SessionColumns.SessionUuid.toString(),session.getUuid());
        cv.put(MemoriesDbHelper.SessionColumns.SessionName.toString(),session.getName());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDate.toString(),session.getDate());
        cv.put(MemoriesDbHelper.SessionColumns.SessionNotes.toString(),session.getNotes());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDuration.toString(),session.getDuration());
        cv.put(MemoriesDbHelper.SessionColumns.SessionCount.toString(),session.getCount());
        cv.put(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString(),session.isFinished());
        cv.put(MemoriesDbHelper.SessionColumns.SessionAuthor.toString(),session.getAuthor());
        String where = String.format("%s=?", MemoriesDbHelper.SessionColumns.SessionUuid.toString());
        String[] args = new String[] { ""+session.getUuid() };
        try{
            rowsAffected = db.update(dbh.TBL_SESSIONS,cv,where,args);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return rowsAffected>0;
    }

    /**
     * Derives session from a cursor
     * @param cursor
     * @return Session datamodel
     */
    private Session from(Cursor cursor) {
        Session session = new Session();
        session.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionUuid.toString())));
        session.setName(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionName.toString())));
        session.setDate(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDate.toString())));
        session.setNotes(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionNotes.toString())));
        session.setDuration(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDuration.toString())));
        session.setCount(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionCount.toString())));
        session.setAuthor(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionAuthor.toString())));
        session.setFinished(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString()))==1);
        return session;
    }
    /**
     * Derives session from a cursor
     * @param cursor Cursor
     * @return SessionVM viewmodel
     */
    private SessionVM viewModelFrom(Cursor cursor) {
        SessionVM session = new SessionVM();
        session.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionUuid.toString())));
        session.setName(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionName.toString())));
        session.setDate(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDate.toString())));
        session.setDuration(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDuration.toString())));
        session.setAuthor(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.UserColumns.UserName.toString())));
        return session;
    }

}
