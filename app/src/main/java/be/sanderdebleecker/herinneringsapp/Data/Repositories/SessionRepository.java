package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class SessionRepository extends BaseRepository {
    public SessionRepository(Context context) {
        super(context);
    }

    protected int insertSession(Session newSession) {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.SessionColumns.SessionName.toString(),newSession.getName());
        cv.put(DbHelper.SessionColumns.SessionDate.toString(),newSession.getDate());
        cv.put(DbHelper.SessionColumns.SessionDuration.toString(),newSession.getDuration());
        cv.put(DbHelper.SessionColumns.SessionCount.toString(),newSession.getCount());
        cv.put(DbHelper.SessionColumns.SessionIsFinished.toString(),(newSession.getSessionIsFinished() ? 1 : 0));
        cv.put(DbHelper.SessionColumns.SessionAuthor.toString(),newSession.getAuthor());
        try{
            int result = (int) db.insert(dbh.TBL_SESSIONS,null,cv);
            return result;
        }catch(Exception e) {
            return -1;
        }
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
        boolean result;
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_RESOURCES+" VALUES(NULL,?,?)");
        stmt.bindLong(1, session);
        stmt.bindLong(2, album);
        try {
            stmt.execute();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
