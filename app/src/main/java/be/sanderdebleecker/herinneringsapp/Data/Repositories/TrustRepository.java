package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;

public class TrustRepository extends BaseRepository {
    public TrustRepository(Context context) {
        super(context);
    }

    /**
     * Queries the local database for all trusts from a user
     * @param userIdentifier
     * @return Cursor with trusts
     */
    public Cursor getAllC(String userIdentifier) {
        Cursor res = null;
        try{
            String selection = MemoriesDbHelper.TrustColumns.TrustSource +"=? OR "+ MemoriesDbHelper.TrustColumns.TrustDestination+"=?";
            String[] selectionArgs = new String[]{""+userIdentifier};
            res = db.query(dbh.TBL_TRUSTS, null, selection, selectionArgs, null, null, null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
