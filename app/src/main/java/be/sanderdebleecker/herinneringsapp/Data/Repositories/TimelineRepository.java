package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;

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
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] projection = new String []{ MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDescription.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryCreator.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationLat.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationLong.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationName.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryCreator.toString()
                };
        String selection = MemoriesDbHelper.TimelineColumns.TimelineUser +"=?";
        String orderBy = "date(m."+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +") DESC";
        String join = dbh.TBL_MEMORIES+" m INNER JOIN "+dbh.TBL_TIMELINE+" t"+
                " ON "+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"="+ MemoriesDbHelper.TimelineColumns.TimelineMemory;
        try{
            qb.setTables(join);
            res =  qb.query(db,projection,selection,new String []{ userIdentifier},null,null,orderBy);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
