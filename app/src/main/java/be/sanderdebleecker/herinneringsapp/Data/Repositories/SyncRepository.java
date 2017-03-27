package be.sanderdebleecker.herinneringsapp.Data.Repositories;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Databases.SyncDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.SyncEntity;

public class SyncRepository extends SyncBaseRepository {

    public SyncRepository(Context context) {
        super(context);
    }
    public List<SyncEntity> getEntries() {
        List<SyncEntity> entries = new ArrayList<SyncEntity>();
        Cursor cursor = null;
        String sql = "SELECT "+ SyncDbHelper.EntityColumns.EUuid+", "+SyncDbHelper.EntityColumns.EType+
                " FROM "+dbh.TBL_ENTITY;
        try{
            cursor =  db.rawQuery(sql,null);
            while(cursor.moveToNext()) {
                entries.add(from(cursor));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return entries;
    }
    private SyncEntity from(Cursor cursor) {
        SyncEntity entity = new SyncEntity();
        entity.setUuid(cursor.getBlob(cursor.getColumnIndex(SyncDbHelper.EntityColumns.EUuid.toString())));
        entity.setType(cursor.getInt(cursor.getColumnIndex(SyncDbHelper.EntityColumns.EType.toString())));
        return entity;
    }
}
