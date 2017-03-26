package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;

public class TimelineRepository extends BaseRepository {

    public TimelineRepository(Context context) {
        super(context);
    }

    /**
     * Queries the local database for timeline items
     * @param userIdentifier String uuid of the user
     * @return Cursor with timeline items
     */
    public Cursor getAllC(String userIdentifier) {
        Cursor res = null;
        String sql = "SELECT * FROM "+dbh.TBL_MEMORIES+" m INNER JOIN "+dbh.TBL_TIMELINE+" t"+
                " ON m."+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"="+"t."+ MemoriesDbHelper.TimelineColumns.TimelineMemory +" WHERE"+
                " t."+ MemoriesDbHelper.TimelineColumns.TimelineUser +"=? ORDER BY date(m."+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +") DESC";
        try{
            res =  db.rawQuery(sql,new String[]{""+userIdentifier});
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
