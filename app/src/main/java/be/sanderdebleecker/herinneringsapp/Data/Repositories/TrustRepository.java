package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;

public class TrustRepository extends BaseRepository {
    public TrustRepository(Context context) {
        super(context);
    }


    public Cursor getAllC(int id) {
        Cursor res = null;
        try{
            String selection = MemoriesDbHelper.TrustColumns.TrustSource +"=? OR "+ MemoriesDbHelper.TrustColumns.TrustDestination+"=?";
            String[] selectionArgs = new String[]{""+id};
            res = db.query(dbh.TBL_TRUSTS, null, selection, selectionArgs, null, null, null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
