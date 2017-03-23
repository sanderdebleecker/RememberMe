package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.TimelineRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

public class TimelineDA extends TimelineRepository {
    public TimelineDA(Context context) {
        super(context);
    }

    public boolean insert(int memoryId, int userId) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_TIMELINE+" VALUES(NULL,?,?)");
        stmt.bindLong(1,memoryId);
        stmt.bindLong(2,userId);
        try {
            stmt.execute();
        }catch(SQLException ex){
            Exception evalException = ex;
            String msg = evalException.getMessage();
            return false;
        }catch(Exception e) {
            Exception evalException = e;
            String msg = e.getMessage();
            e.printStackTrace();
            return false;
        }finally {
            return true;
        }
    }
    public boolean delete(int memoryId, int userId) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_TIMELINE+" WHERE "+ MemoriesDbHelper.TimelineColumns.TimelineMemory +"=? AND "+ MemoriesDbHelper.TimelineColumns.TimelineUser +"=?");
        stmt.bindLong(1, memoryId);
        stmt.bindLong(1, userId);
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    public ArrayList<Memory> getAll(int userId) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = getAllC(userId);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        cursor.close();
        return mems;
    }

    private Memory from(Cursor cursor) {
        Memory mem = new Memory();
        mem.setId(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryId.toString())));
        mem.setTitle(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryTitle.toString())));
        mem.setDescription(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryDescription.toString())));
        mem.setDate(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString())));
        mem.setCreator(cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryCreator.toString())));
        mem.setPath(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryPath.toString())));
        mem.setType(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryType.toString())));
        Location loc = new Location(
                cursor.getDouble(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryLocationLat.toString())),
                cursor.getDouble(cursor .getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryLocationLong.toString())),
                cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryLocationName.toString()))
        );
        mem.setLocation(loc);
        return mem;
    }

}
