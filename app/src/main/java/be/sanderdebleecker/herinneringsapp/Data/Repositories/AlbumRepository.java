package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import java.util.List;
import java.util.UUID;

import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;

/**
 * Sander De Bleecker
 */

/**
 * Provides base methods to access TBL_ALBUMS in the local database
 */
public class AlbumRepository extends BaseRepository {

    protected AlbumRepository(Context context) {
        super(context);
    }

    /**
     * Queries the local database for all albums
     * @param userIdentifier String uuid of the user
     * @return Cursor containing albums
     */
    protected Cursor getAllC(String userIdentifier) {
        Cursor res = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] projection = new String[] {
            MemoriesDbHelper.AlbumColumns.AlbumUuid.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumUuid.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumTitle.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumCreator.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryType.toString()
        };
        String leftOuterJoin = dbh.TBL_ALBUMS+" LEFT OUTER JOIN "+dbh.TBL_MEMORIES+
                " ON a."+ MemoriesDbHelper.AlbumColumns.AlbumThumbnail +"="+"m."+ MemoriesDbHelper.MemoryColumns.MemoryUuid;
        String selection = MemoriesDbHelper.AlbumColumns.AlbumCreator +"=?";
        try{
            qb.setTables(leftOuterJoin);
            qb.query(db,projection,selection,new String[] { userIdentifier},null,null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for all albums
     * @return Cursor containing albums
     */
    public Cursor getAllCRaw() {
        Cursor res = null;
        String sql = "SELECT * FROM "+dbh.TBL_ALBUMS;
        try {
            res = db.rawQuery(sql,null);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for an album
     * @param albumIdentifier String uuid of the album
     * @return Cursor containing the album
     */
    protected Cursor getC(String albumIdentifier) {
        Cursor res = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String join = dbh.TBL_ALBUMS+" LEFT OUTER JOIN "+dbh.TBL_MEMORIES+
                " ON "+ MemoriesDbHelper.AlbumColumns.AlbumThumbnail +"="+ MemoriesDbHelper.MemoryColumns.MemoryUuid;
        String[] projection =  new String[] {
                MemoriesDbHelper.AlbumColumns.AlbumUuid.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumTitle.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumCreator.toString(),
                MemoriesDbHelper.AlbumColumns.AlbumThumbnail.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryType.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryTitle.toString()
        };
        String selection = MemoriesDbHelper.AlbumColumns.AlbumUuid +"=?";
        try{
            qb.setTables(join);
            res =  qb.query(db,projection,selection,new String[]{""+albumIdentifier},null,null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Updates an album in the local database
     * @param album Album to be updated
     * @return boolean success
     */
    public boolean updateAlbum(Album album) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_ALBUMS+" SET "+
                MemoriesDbHelper.AlbumColumns.AlbumTitle +"=? WHERE "+ MemoriesDbHelper.AlbumColumns.AlbumUuid +"=?");
        stmt.bindString(1, album.getName());
        stmt.bindString(2,album.getUuid());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Associates newly selected memories with an album and disassociates differences
     * @param albumIdentifier String uuid of the album
     * @param selectedMemories Memories that are part of the album
     * @param prevSelectedMemories Memories that were part of the album
     * @return boolean success
     */
    public boolean updateAlbumContents(String albumIdentifier, List<String> selectedMemories, List<String> prevSelectedMemories) {
        try{
            for(String m : selectedMemories) {
                if(!prevSelectedMemories.contains(m)){
                    insertAlbumMemory(albumIdentifier,m);
                }
            }
            for(String m : prevSelectedMemories) {
                if (!selectedMemories.contains(m)) {
                    deleteAlbumMemory(albumIdentifier, m);
                }
            }
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Queries the local database for all album memories
     * @param albumIdentifier String uuid of the album
     * @return Cursor cursor containing memories
     */
    public Cursor getSelectedMemoriesC(String albumIdentifier) {
        Cursor res = null;
        String[] select = new String[]{MemoriesDbHelper.AlbumsMemoriesColumns.AMMemory.toString()};
        String where = MemoriesDbHelper.AlbumsMemoriesColumns.AMAlbum +"=?";
        try{
            res = db.query(dbh.TBL_ALBUMS_MEMORIES, select, where, new String[]{ albumIdentifier}, null, null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Inserts a new album into the local database
     * @param album Album to be inserted
     * @return String uuid of new album
     */
    protected String insertAlbum(Album album) {
        String identifier = UUID.randomUUID().toString().replaceAll("-","");
        try{
            ContentValues cv = new ContentValues();
            cv.put(MemoriesDbHelper.AlbumColumns.AlbumUuid.toString(),identifier );
            cv.put(MemoriesDbHelper.AlbumColumns.AlbumTitle.toString(), album.getName());
            cv.put(MemoriesDbHelper.AlbumColumns.AlbumCreator.toString(), album.getAuthorId());
            cv.put(MemoriesDbHelper.AlbumColumns.AlbumThumbnail.toString(), album.getThumbnail().getUuid());
            //execute
            db.insert(dbh.TBL_ALBUMS,null,cv);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return identifier;
    }

    /**
     * Associates an album with a memory in the local database
     * @param albumIdentifier String uuid of the album
     * @param mememoryIdentifier String uuid of the memory
     */
    protected void insertAlbumMemory(String albumIdentifier, String mememoryIdentifier) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_ALBUMS_MEMORIES+" VALUES(NULL,?,?)");
        stmt.bindString(1, albumIdentifier);
        stmt.bindString(2, mememoryIdentifier);
        stmt.execute();
    }

    /**
     * Disassociates an album from a memory in the local database
     * @param albumUuid String uuid of the album
     * @param memoryUuid String uuid of the memory
     */
    protected void deleteAlbumMemory(String albumUuid, String memoryUuid) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS_MEMORIES+" WHERE "+ MemoriesDbHelper.AlbumsMemoriesColumns.AMAlbum +"=? AND "+ MemoriesDbHelper.AlbumsMemoriesColumns.AMMemory +"=?");
        stmt.bindString(1, albumUuid);
        stmt.bindString(2, memoryUuid);
        stmt.execute();
    }

    /**
     * Associates an albnm with memories in the local database
     * @param albumIdentifier String uuid of the album
     * @param memories List<String> memories
     * @return boolean success
     */
    protected boolean insertAlbumMemories(String albumIdentifier,List<String> memories) {
        boolean result = true;
        try{
            if(albumIdentifier.equals("")) {
                throw new SQLException("AMAlbum '"+albumIdentifier+"' does not exist yet, so no memories can be inserted (AlbumRepository.insertAlbumMemories)");
            }
            for(String mem : memories) {
                insertAlbumMemory(albumIdentifier, mem);
            }
        }catch(SQLException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
