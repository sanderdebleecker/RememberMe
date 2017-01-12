package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.SessionRepository;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class SessionDA extends SessionRepository {

    public SessionDA(Context context) {
        super(context);
    }

    public boolean insert(Session newSession,List<Integer> albums) {
        if(newSession==null) return false;
        boolean success = false;
        db.beginTransaction();
        int sessionId = insertSession(newSession);
        if(sessionId>-1) {
            if(insertSessionAlbums(sessionId,albums)){
                db.setTransactionSuccessful();
                success = true;
            }
        }
        db.endTransaction();
        return success;
    }
}
