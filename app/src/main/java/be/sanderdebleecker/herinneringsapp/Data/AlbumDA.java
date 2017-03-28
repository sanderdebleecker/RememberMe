package be.sanderdebleecker.herinneringsapp.Data;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.AlbumRepository;
import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;

/**
 * Sander De Bleecker
 */

/**
 * Provides methods to access TBL_ALBUMS in the local database
 */
public class AlbumDA extends AlbumRepository {
    public AlbumDA(Context context) {
        super(context);
    }

    /**
     * Queries the local database for an album
     * @param albumIdentifier String uuid of the album
     * @return Album album
     */
    public Album get(String albumIdentifier) {
        Cursor cursor = getC(albumIdentifier);
        cursor.moveToNext();
        Album a = from(cursor);
        cursor.close();
        return a;
    }

    /**
     * Queries the local database for albums
     * @param userIdentifier String uuid of the user
     * @return ArrayList<Album> albums of the user
     */
    public List<Album> getAll(String userIdentifier) {
        ArrayList<Album> albums = new ArrayList<>();
        Cursor cursor = getAllC(userIdentifier);
        while(cursor.moveToNext()) {
            Album a = partiallyFrom(cursor);
            albums.add(a);
        }
        cursor.close();
        return albums;
    }

    /**
     * Queries the local database for albums
     * @param userIdentifier String uuid of the user
     * @return List<SelectableAlbum> selectable albums
     */
    public List<SelectableAlbum> getSelectables(String userIdentifier) {
        ArrayList<SelectableAlbum> albums = new ArrayList<>();
        Cursor cursor = getAllC(userIdentifier);
        while(cursor.moveToNext()) {
            albums.add(new SelectableAlbum(from(cursor)));
        }
        cursor.close();
        return albums;
    }

    /**
     * Inserts an album with selected memories into the local database
     * @param album Album to be inserted
     * @param selectedMemories List<String> List of identifiers of memories the album will contain
     * @return boolean success
     */
    public boolean insert(Album album, List<String> selectedMemories) {
        boolean success = false;
        // !TRANSACTION
        db.beginTransaction();
        String identifier = insertAlbum(album);
        if(!identifier.equals("")) {
            if(insertAlbumMemories(identifier,selectedMemories)) {
                db.setTransactionSuccessful();
                success = true;
            }
        }
        db.endTransaction();
        return success;
    }

    /**
     * Updates an album in the local database
     * @param album Album to be updated
     * @param selectedMemories List<String> of corresponding memory identifiers
     * @return boolean success
     */
    public boolean update(Album album, List<String> selectedMemories) {
        boolean result = false;
        db.beginTransaction();
        result = updateAlbum(album);
        result = result & updateAlbumContents(album.getUuid(),selectedMemories,getSelectedMemories(album.getUuid()));
        if(result)
            db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    /**
     * Deletes an album in the local database and disassociates corresponding memories
     * @param albumIdentifier String uuid of album
     * @return boolean success
     */
    public boolean delete(String albumIdentifier) {
        boolean result = false;
        //extract
        db.beginTransaction();
        result = deleteAlbum(albumIdentifier);
        result = result & deleteAlbumsCollection(albumIdentifier);
        if(result)
            db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    /**
     * Deletes an album in the local database
     * @param albumIdentifier String uuid album
     * @return boolean success
     */
    private boolean deleteAlbum(String albumIdentifier) {
        boolean result=true;
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS+" WHERE "+ MemoriesDbHelper.AlbumColumns.AlbumUuid +"=?");
        stmt.bindString(1, albumIdentifier);
        try{
            stmt.execute();
        }catch(SQLException e) {
            result=false;
        }
        return result;
    }

    /**
     * Deletes associations between album and it's memories
     * @param albumIdentifier String uuid album
     * @return boolean success
     */
    private boolean deleteAlbumsCollection(String albumIdentifier) {
        boolean result=true;
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS_MEMORIES+" WHERE "+ MemoriesDbHelper.AlbumsMemoriesColumns.AMId +"=?");
        stmt.bindString(1, albumIdentifier);
        try{
            stmt.execute();
        }catch(SQLException e) {
            result=false;
        }
        return result;
    }

    /**
     * Gets associated memories from an album
     * @param albumIdentifier String uuid of the album
     * @return List<String> uuid's of memories
     */
    public List<String> getSelectedMemories(String albumIdentifier) {
        List<String> selectedMems = new ArrayList<String>();
        Cursor cursor = getSelectedMemoriesC(albumIdentifier);
        while(cursor.moveToNext()) {
            selectedMems.add(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumsMemoriesColumns.AMMemory.toString())));
        }
        cursor.close();
        return selectedMems;
    }

    /**
     * Derives an Album from a cursor with album
     * @param cursor Cursor containing album
     * @return Album datamodel
     */
    //TODO : user viewmodel
    public Album partiallyFrom(Cursor cursor) {
        Album a = new Album();
        a.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumColumns.AlbumUuid.toString())));
        a.setName(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumColumns.AlbumTitle.toString())));
        Memory thumbnail = new Memory();
        thumbnail.setPath(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryPath.toString())));
        thumbnail.setType(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryType.toString())));
        a.setThumbnail(thumbnail);
        return a;
    }
    /**
     * Derives an Album from a cursor with album
     * @param cursor Cursor containing album
     * @return Album datamodel
     */
    public Album from(Cursor cursor) {
        Album a = new Album();
        a.setUuid(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumColumns.AlbumUuid.toString())));
        a.setName(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumColumns.AlbumTitle.toString())));
        a.setAuthor(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.AlbumColumns.AlbumCreator.toString())));
        Memory thumbnail = new Memory();
        thumbnail.setPath(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryPath.toString())));
        thumbnail.setType(cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.MemoryColumns.MemoryType.toString())));
        a.setThumbnail(thumbnail);
        return a;
    }

}
