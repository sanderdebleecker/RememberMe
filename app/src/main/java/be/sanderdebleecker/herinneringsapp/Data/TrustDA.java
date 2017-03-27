package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.TrustRepository;
import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Trust;

/**
 * Sander De Bleecker
 */

/**
 * Provides methods to access TBL_TRUSTS in the local database
 */
public class TrustDA extends TrustRepository {
    public TrustDA(Context context) {
        super(context);
    }

    /**
     * Queries the local database for all trusts from a certain user
     * @param userIdentifier String uuid user
     * @return List<Trust> Trusts of user
     */
    public List<Trust> getTrustList(String userIdentifier) {
        ArrayList<Trust> trusts = new ArrayList<>();
        Cursor cursor = getAllC(userIdentifier);
        while(cursor.moveToNext()) {
            Trust t = from(cursor);
            trusts.add(t);
        }
        cursor.close();
        return trusts;
    }

    /**
     * Derives Trust from a cursor loaded with a trust
     * @param cursor Cursor trust
     * @return Trust data model
     */
    public Trust from(Cursor cursor) {
        Trust t = new Trust();
        t.setA(new Trust.Party(
                cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.TrustColumns.TrustSource.toString()))
        ));
        t.setB(new Trust.Party(
                cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.TrustColumns.TrustDestination.toString()))
        ));
        return t;
    }

}
