package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;

public class TimelineRepository extends BaseRepository {

    public TimelineRepository(Context context) {
        super(context);
    }

    public Cursor getAllC(int userId) {
        Cursor res = null;
        String sql = "SELECT * FROM "+dbh.TBL_MEMORIES+" m INNER JOIN "+dbh.TBL_TIMELINE+" t"+
                " ON m."+ DbHelper.MemoryColumns.MemoryId +"="+"t."+ DbHelper.TimelineColumns.TimelineMemory +" WHERE"+
                " t."+ DbHelper.TimelineColumns.TimelineUser +"=? ORDER BY date(m."+ DbHelper.MemoryColumns.MemoryDateTime +") DESC";
        try{
            res =  db.rawQuery(sql,new String[]{""+userId});
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
