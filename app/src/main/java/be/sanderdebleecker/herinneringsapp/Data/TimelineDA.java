package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.TimelineRepository;
import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
/**
 * Sander De Bleecker
 */

/**
 * Provides methods to access TBL_TIMELINE in the local database
 */
public class TimelineDA extends TimelineRepository {
    public TimelineDA(Context context) {
        super(context);
    }

    /**
     * Inserts a timelineItem into the local database
     * @param memoryIdentifier String uuid
     * @param userIdentifier String uuid
     * @return boolean success
     */
    public boolean insert(String memoryIdentifier, String userIdentifier) {
        boolean success=true;
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_TIMELINE+" VALUES(NULL,?,?)");
        stmt.bindString(1,memoryIdentifier);
        stmt.bindString(2,userIdentifier);
        try {
            stmt.execute();
        }catch(SQLException ex){
            success=false;
        }catch(Exception e) {
            e.printStackTrace();
            success=false;
        }
        return success;
    }

    /**
     * Deletes a timeline item from the timeline in the local database
     * @param memoryIdentifier
     * @param userIdentifier
     * @return boolean success
     */
    public boolean delete(String memoryIdentifier, String userIdentifier) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_TIMELINE+" WHERE "+ MemoriesDbHelper.TimelineColumns.TimelineMemory +"=? AND "+ MemoriesDbHelper.TimelineColumns.TimelineUser +"=?");
        stmt.bindString(1, memoryIdentifier);
        stmt.bindString(2, userIdentifier);
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Queries the local database for timeline items
     * @param userIdentifier String uuid of user
     * @return List<Memory> timeline items
     */
    public List<Memory> getAll(String userIdentifier) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = getAllC(userIdentifier);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        cursor.close();
        return mems;
    }

    /**
     * Derives a Memory for timeline from a Cursor
     * @param cursor with memory
     * @return Memory
     */
    private Memory from(Cursor cursor) {
        Memory mem = new Memory();
        mem.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryUuid.toString())));
        mem.setTitle(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryTitle.toString())));
        mem.setDescription(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryDescription.toString())));
        mem.setDate(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString())));
        mem.setCreator(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryCreator.toString())));
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
