package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Session;
import be.sanderdebleecker.herinneringsapp.Models.View.SessionVM;

public class SessionRepository extends BaseRepository {
    public SessionRepository(Context context) {
        super(context);
    }

    protected int insertSession(Session newSession) {
        int result=-1;
        ContentValues cv = new ContentValues();
        cv.put(MemoriesDbHelper.SessionColumns.SessionName.toString(),newSession.getName());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDate.toString(),newSession.getDate());
        cv.put(MemoriesDbHelper.SessionColumns.SessionNotes.toString(),newSession.getNotes());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDuration.toString(),newSession.getDuration());
        cv.put(MemoriesDbHelper.SessionColumns.SessionCount.toString(),newSession.getCount());
        cv.put(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString(), (newSession.isFinished() ? 1 : 0));
        cv.put(MemoriesDbHelper.SessionColumns.SessionAuthor.toString(),newSession.getAuthor());
        try{
            result = (int) db.insert(dbh.TBL_SESSIONS,null,cv);
        } catch(SQLException e) {
            e.printStackTrace();
            result = -1;
        } catch(Exception e) {
            result = -1;
        }
        return result;
    }
    protected boolean insertSessionAlbums(int session,List<Integer> albums) {
        boolean result = true;
        for(Integer album : albums) {
            if(!insertSessionAlbum(session,album)){
                result = false;
            }
        }
        return result;
    }
    protected boolean insertSessionAlbum(int session,int album) {
        boolean result = false;
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_RESOURCES+" VALUES(NULL,?,?)");
        stmt.bindLong(1, session);
        stmt.bindLong(2, album);
        try {
            stmt.execute();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    protected List<Integer> getAlbumIds(int session) {
        List<Integer> albums = new ArrayList<>();
        Cursor cursor = null;
        String sql = "SELECT "+ MemoriesDbHelper.ResourceColumns.ResourceAlbum +
                " FROM "+dbh.TBL_RESOURCES+
                " WHERE "+ MemoriesDbHelper.ResourceColumns.ResourceSession +"=?";
        try{
            cursor =  db.rawQuery(sql,new String[]{ ""+session });
            while(cursor.moveToNext()) {
                albums.add(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.ResourceColumns.ResourceAlbum.toString())));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        } finally {
            cursor.close();
        }
        return albums;
    }

    //TODO getauthorname from tblusers
    protected Session getSession(int id,int identity) {
        Session session = null;
        Cursor cursor;
        String sql = "SELECT *"+
                " FROM " + dbh.TBL_SESSIONS+
                " WHERE " + MemoriesDbHelper.SessionColumns.SessionId +"=?"+
                " AND " + MemoriesDbHelper.SessionColumns.SessionAuthor + "=?" ;
        try{
            cursor =  db.rawQuery(sql,new String[]{ ""+id,""+identity });
            while(cursor.moveToNext()) {
                session = from(cursor);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return session;
    }
    protected List<SessionVM> getSessions(int identity) {
        List<SessionVM> sessions = new ArrayList<>();
        Cursor cursor;
        String sql = " SELECT s."+ MemoriesDbHelper.SessionColumns.SessionId +
                ", s." + MemoriesDbHelper.SessionColumns.SessionName +
                ", u." + MemoriesDbHelper.UserColumns.UserName +
                ", s." + MemoriesDbHelper.SessionColumns.SessionDuration +
                ", s." + MemoriesDbHelper.SessionColumns.SessionDate +
                " FROM " + dbh.TBL_SESSIONS + " s"+
                " LEFT OUTER JOIN " + dbh.TBL_USERS + " u"+
                " ON s."+ MemoriesDbHelper.SessionColumns.SessionAuthor+" = u."+ MemoriesDbHelper.UserColumns.UserId+
                " WHERE " + MemoriesDbHelper.SessionColumns.SessionAuthor + "=?";
        try{
            cursor =  db.rawQuery(sql,new String[]{ ""+identity });
            while(cursor.moveToNext()) {
                sessions.add(viewModelFrom(cursor));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return sessions;

    }
    protected boolean updateSession(Session session) {
        int rowsAffected = -1;
        ContentValues cv = new ContentValues();
        cv.put(MemoriesDbHelper.SessionColumns.SessionId.toString(),session.getId());
        cv.put(MemoriesDbHelper.SessionColumns.SessionName.toString(),session.getName());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDate.toString(),session.getDate());
        cv.put(MemoriesDbHelper.SessionColumns.SessionNotes.toString(),session.getNotes());
        cv.put(MemoriesDbHelper.SessionColumns.SessionDuration.toString(),session.getDuration());
        cv.put(MemoriesDbHelper.SessionColumns.SessionCount.toString(),session.getCount());
        cv.put(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString(),session.isFinished());
        cv.put(MemoriesDbHelper.SessionColumns.SessionAuthor.toString(),session.getAuthor());
        String where = String.format("%s=?", MemoriesDbHelper.SessionColumns.SessionId.toString());
        String[] args = new String[] { ""+session.getId() };
        try{
            rowsAffected = db.update(dbh.TBL_SESSIONS,cv,where,args);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return rowsAffected>0;
    }

    private Session from(Cursor c) {
        Session session = new Session();
        session.setId(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionId.toString())));
        session.setName(c.getString(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionName.toString())));
        session.setDate(c.getString(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDate.toString())));
        session.setNotes(c.getString(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionNotes.toString())));
        session.setDuration(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDuration.toString())));
        session.setCount(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionCount.toString())));
        session.setAuthor(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionAuthor.toString())));
        session.setFinished(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionIsFinished.toString()))==1);
        return session;
    }
    private SessionVM viewModelFrom(Cursor c) {
        SessionVM session = new SessionVM();
        session.setId(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionId.toString())));
        session.setName(c.getString(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionName.toString())));
        session.setDate(c.getString(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDate.toString())));
        session.setDuration(c.getInt(c.getColumnIndex(MemoriesDbHelper.SessionColumns.SessionDuration.toString())));
        session.setAuthor(c.getString(c.getColumnIndex(MemoriesDbHelper.UserColumns.UserName.toString())));
        return session;
    }

}
