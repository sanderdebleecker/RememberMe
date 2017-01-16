package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class SessionRepository extends BaseRepository {
    public SessionRepository(Context context) {
        super(context);
    }

    protected int insertSession(Session newSession) {
        int result=-1;
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.SessionColumns.SessionName.toString(),newSession.getName());
        cv.put(DbHelper.SessionColumns.SessionDate.toString(),newSession.getDate());
        cv.put(DbHelper.SessionColumns.SessionDuration.toString(),newSession.getDuration());
        cv.put(DbHelper.SessionColumns.SessionCount.toString(),newSession.getCount());
        cv.put(DbHelper.SessionColumns.SessionIsFinished.toString(), (newSession.getSessionIsFinished() ? 1 : 0));
        cv.put(DbHelper.SessionColumns.SessionAuthor.toString(),newSession.getAuthor());
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
        String sql = "SELECT "+ DbHelper.ResourceColumns.ResourceAlbum +
                " FROM "+dbh.TBL_RESOURCES+
                " WHERE "+ DbHelper.ResourceColumns.ResourceSession +"=?";
        try{
            cursor =  db.rawQuery(sql,new String[]{ ""+session });
            while(cursor.moveToNext()) {
                albums.add(cursor.getInt(cursor.getColumnIndex(DbHelper.ResourceColumns.ResourceAlbum.toString())));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return albums;
    }

}
