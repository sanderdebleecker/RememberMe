package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.SessionRepository;
import be.sanderdebleecker.herinneringsapp.Models.Session;

/**
 * Sander De Bleecker
 */

/**
 * Provides methods to access TBL_SESSIONS in the local database
 */
public class SessionDA extends SessionRepository {

    public SessionDA(Context context) {
        super(context);
    }

    /**
     * Inserts a new session into the local database
     * @param session Session to be inserted
     * @param albums List<String> albums of the session
     * @return
     */
    public String insert(Session session,List<String> albums) {
        if(session==null) return "";
        db.beginTransaction();
        String sessionIdentifier = insert(session);
        if(!sessionIdentifier.equals("")) {
            if(insertSessionAlbums(sessionIdentifier,albums)){
                db.setTransactionSuccessful();
            }else{
                return"";
            }
        }
        db.endTransaction();
        return sessionIdentifier;
    }

}
