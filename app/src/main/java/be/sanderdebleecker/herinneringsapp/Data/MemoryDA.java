package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.MemoryRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.MappedMemory;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;

/**
 * Sander De Bleecker
 */

/**
 * Provides methods to access TBL_MEMORIES in the local database
 */
public class MemoryDA extends MemoryRepository {

    public MemoryDA(Context context) {
        super(context);
    }

    /**
     * Queries the local database for a memory
     * @param identifier String uuid of the memory
     * @return Memory datamodel
     */
    public Memory get(String identifier) {
        Cursor cursor = getByCursor(identifier) ;
        Memory memory;
        try {
            cursor.moveToNext();
            memory = from(cursor);
            db.close();
        }catch(Exception e) {
            return null;
        }
        return memory;
    }

    /**
     * Queries the local database for a memory
     * @param name String name of the memory
     * @return Memory datamodel
     */
    public Memory getByTitle(String name) {
        Cursor cursor = (Cursor) getCursorByTitle(name) ;
        Memory memory;
        try {
            cursor.moveToNext();
            memory = from(cursor);
            db.close();
        }catch(Exception e) {
            return null;
        }
        return memory;
    }

    /**
     * Queries the local database for all memories of a user
     * @param userIdentifier String uuid of the user
     * @return List<Memory> a list of memories of the user
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
     * Queries the local database for memories from albums
     * @param albumIdentifiers List<String> of uuid's of albums
     * @return List<Memory> list of memories from the albums
     */
    public List<Memory> getAllFromAlbums(List<String> albumIdentifiers ) {
        if(albumIdentifiers.isEmpty()) return null;
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = getAllCFromAlbums(albumIdentifiers);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        return mems;
    }

    /**
     * Inserts a new memory into the local database
     * @param newMemory Memory to insert
     * @return boolean success
     */
    public boolean insert(Memory newMemory) {
        if(newMemory==null) return false;
        if(newMemory.getLocation()==null){
            return insertMemory(newMemory);
        }else{
            return insertMemoryWithLocation(newMemory);
        }
    }

    /**
     * Updates a memory from the local database
     * @param memory to be updated
     * @return boolean success
     */
    public boolean update(Memory memory) {
        if(memory==null) return false;
        if(memory.getLocation()==null){
            return updateMemory(memory);
        }else{
            return updateMemoryWithLocation(memory);
        }
    }

    /**
     * Deletes a memory from the local database
     * @param memoryUuid String uuid
     * @return success
     */
    public boolean delete(String memoryUuid) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_MEMORIES+" WHERE "+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"=?");
        stmt.bindString(1, memoryUuid);
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }


    /**
     * Queries the local database for memories with filter
     * @param username of the user
     * @param filter on title,location,user,first & lastname
     * @return List<Memory> memories
     */
    public List<Memory> getFiltered(String username, String filter) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = super.getFilteredC(username, filter);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        cursor.close();
        return mems;
    }

    /**
     * Queries the local database for memories with locations (MappedMemory).
     * MappedMemory is a datamodel for showing memories on a geomap.
     * @return List<MappedMemory> memories
     */
    public List<MappedMemory> getMapped() {
        ArrayList<MappedMemory> memories = new ArrayList<>();
        Cursor cursor = getMappedC();
        try{
            while(cursor.moveToNext()) {
                String index = cursor.getString(0);
                String title = cursor.getString(1);
                double lng = cursor.getDouble(2);
                double lat = cursor.getDouble(3);
                memories.add(new MappedMemory(index,title,lng,lat));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            cursor.close();
        }
        return memories;
    }

    /**
     * Queries the local database for memories
     * Maps them tot SelectableMemory a viewmodel enabling selection
     * @param userIdentifier the identifier of the user
     * @return List<SelectableMemory> memories
     */
    public List<SelectableMemory> getSelectabl(String userIdentifier) {
        ArrayList<SelectableMemory> mems = new ArrayList<>();
        Cursor cursor = getAllC(userIdentifier);
        while(cursor.moveToNext()) {
            mems.add(new SelectableMemory(from(cursor)));
        }
        cursor.close();
        return mems;
    }

    /**
     * Maps cursor containing memory to the Memory datamodel
     * @param cursor containing memory
     * @return Memory memory
     */
    private Memory from(Cursor cursor) {
        Memory mem = new Memory();
        mem.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryUuid.toString())));
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
