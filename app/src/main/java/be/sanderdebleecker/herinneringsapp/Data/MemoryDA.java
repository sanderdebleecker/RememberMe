package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.MemoryRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.MappedMemory;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;

/*
* memory data-access
* returns data objects
* */

public class MemoryDA extends MemoryRepository {

    public MemoryDA(Context context) {
        super(context);
    }
    //BASIC
    public Memory get(int id) {
        Cursor cursor = (Cursor) getC(id) ;
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
    public Memory get(String name) {
        Cursor cursor = (Cursor) getC(name) ;
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
    public ArrayList<Memory> getAll(int userId) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = getAllC(userId);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        cursor.close();
        return mems;
    }
    public ArrayList<Memory> getAllFromAlbums(List<Integer> albumIds ) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = getAllCFromAlbums(albumIds);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        return mems;
    }
    public boolean insert(Memory newMemory) {
        if(newMemory==null) return false;
        if(newMemory.getLocation()==null){
            return insertMemory(newMemory);
        }else{
            return insertMemoryWithLocation(newMemory);
        }
    }
    public boolean update(Memory newMemory) {
        if(newMemory==null) return false;
        if(newMemory.getLocation()==null){
            return updateMemory(newMemory);
        }else{
            return updateMemoryWithLocation(newMemory);
        }
    }
    public boolean delete(int memoryId) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_MEMORIES+" WHERE "+ DbHelper.MemoryColumns.MemoryId +"=?");
        stmt.bindLong(1, memoryId);
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }


    //SPEC
    public ArrayList<Memory> getFiltered(String username, String filter) {
        ArrayList<Memory> mems = new ArrayList<>();
        Cursor cursor = super.getFilteredC(username, filter);
        while(cursor.moveToNext()) {
            mems.add(from(cursor));
        }
        cursor.close();
        return mems;
    }
    public ArrayList<MappedMemory> getMapped() {
        ArrayList<MappedMemory> memories = new ArrayList<>();
        Cursor cursor = getMappedC();
        try{
            while(cursor.moveToNext()) {
                int index = cursor.getInt(0);
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
    public ArrayList<SelectableMemory> getSelectabl(int userId) {
        ArrayList<SelectableMemory> mems = new ArrayList<>();
        Cursor cursor = getAllC(userId);
        while(cursor.moveToNext()) {
            mems.add(new SelectableMemory(from(cursor)));
        }
        cursor.close();
        return mems;
    }


    private Memory from(Cursor cursor) {
        Memory mem = new Memory();
        mem.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryId.toString())));
        mem.setTitle(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryTitle.toString())));
        mem.setDescription(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryDescription.toString())));
        mem.setDate(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryDateTime.toString())));
        mem.setCreator(cursor.getInt(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryCreator.toString())));
        mem.setPath(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryPath.toString())));
        mem.setType(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryType.toString())));
        Location loc = new Location(
                cursor.getDouble(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryLocationLat.toString())),
                cursor.getDouble(cursor .getColumnIndex(DbHelper.MemoryColumns.MemoryLocationLong.toString())),
                cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryLocationName.toString()))
        );
        mem.setLocation(loc);
        return mem;
    }

}
