package be.sanderdebleecker.herinneringsapp.Data;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.AlbumRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;

public class AlbumDA extends AlbumRepository {
    public AlbumDA(Context context) {
        super(context);
    }

    public Album get(int albumId) {
        Cursor cursor = getC(albumId);
        cursor.moveToNext();
        Album a = from(cursor);
        cursor.close();
        return a;
    }
    public ArrayList<Album> getAll(int userId) {
        ArrayList<Album> albums = new ArrayList<>();
        Cursor cursor = getAllC(userId);
        while(cursor.moveToNext()) {
            Album a = partiallyFrom(cursor);
            albums.add(a);
        }
        cursor.close();
        return albums;
    }
    public ArrayList<SelectableAlbum> getSelectabl(int userId) {
        ArrayList<SelectableAlbum> albums = new ArrayList<>();
        Cursor cursor = getAllC(userId);
        while(cursor.moveToNext()) {
            albums.add(new SelectableAlbum(from(cursor)));
        }
        cursor.close();
        return albums;
    }
    public boolean insert(Album newAlbum, List<Integer> selectedMemories) {
        boolean success = false;
        // !TRANSACTION
        db.beginTransaction();
        int id = insertAlbum(newAlbum);
        if(id!=-1) {
            if(insertAlbumMemories(id,selectedMemories)) {
                db.setTransactionSuccessful();
                success = true; // only happy if both work
            }
        }
        db.endTransaction();
        return success;
    }
    public boolean update(Album a, List<Integer> selectedMemories) {
        boolean result = false;
        db.beginTransaction();
        result = updateAlbum(a);
        result = result & updateAlbumContents(a.getId(),selectedMemories,getSelectedMemories(a.getId()));
        if(result)
            db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }
    public boolean delete(int albumId) {
        boolean result = false;
        //extract
        db.beginTransaction();
        result = deleteAlbum(albumId);
        result = result & deleteAlbumsCollection(albumId);
        if(result)
            db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    private boolean deleteAlbum(int albumId) {
        boolean result=true;
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS+" WHERE "+ DbHelper.AlbumColumns.AlbumId +"=?");
        stmt.bindLong(1, albumId);
        try{
            stmt.execute();
        }catch(SQLException e) {
            result=false;
        }
        return result;
    }
    private boolean deleteAlbumsCollection(int albumId) {
        boolean result=true;
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS_MEMORIES+" WHERE "+ DbHelper.AlbumsMemoriesColumns.AMId +"=?");
        stmt.bindLong(1, albumId);
        try{
            stmt.execute();
        }catch(SQLException e) {
            result=false;
        }
        return result;
    }

    //others
    public List<Integer> getSelectedMemories(int albumId) {
        List<Integer> selectedMems = new ArrayList<Integer>();
        Cursor cursor = getSelectedMemoriesC(albumId);
        while(cursor.moveToNext()) {
            selectedMems.add((int)cursor.getLong(cursor.getColumnIndex(DbHelper.AlbumsMemoriesColumns.AMMemory.toString())));
        }
        cursor.close();
        return selectedMems;
    }
    // display parts in list
    public Album partiallyFrom(Cursor cursor) {
        Album a = new Album();
        a.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.AlbumColumns.AlbumId.toString())));
        a.setName(cursor.getString(cursor.getColumnIndex(DbHelper.AlbumColumns.AlbumTitle.toString())));
        Memory thumbnail = new Memory();
        thumbnail.setPath(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryPath.toString())));
        thumbnail.setType(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryType.toString())));
        a.setThumbnail(thumbnail);
        return a;
    }
    // fully display
    public Album from(Cursor cursor) {
        Album a = new Album();
        a.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.AlbumColumns.AlbumId.toString())));
        a.setName(cursor.getString(cursor.getColumnIndex(DbHelper.AlbumColumns.AlbumTitle.toString())));
        a.setAuthor(cursor.getString(cursor.getColumnIndex(DbHelper.AlbumColumns.AlbumCreator.toString())));
        Memory thumbnail = new Memory();
        thumbnail.setPath(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryPath.toString())));
        thumbnail.setType(cursor.getString(cursor.getColumnIndex(DbHelper.MemoryColumns.MemoryType.toString())));
        a.setThumbnail(thumbnail);
        return a;
        //a.setName(cursor.getString(cursor.getColumnIndex(DbHelper.AlbumColumns.MemoryCreator.toString())));
    }

}
