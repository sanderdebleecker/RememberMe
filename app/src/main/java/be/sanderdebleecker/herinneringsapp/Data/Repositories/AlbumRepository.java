package be.sanderdebleecker.herinneringsapp.Data.Repositories;

//TODO make update a transaction
//Todo change getAllC

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;

public class AlbumRepository extends BaseRepository {

    protected AlbumRepository(Context context) {
        super(context);
    }
    protected Cursor getAllC(int userId) {
        Cursor res = null;
        //Album Memories !!!!
        String sql = "SELECT a."+ DbHelper.AlbumColumns.AlbumId +","+
                " a."+ DbHelper.AlbumColumns.AlbumTitle +","+
                " a."+ DbHelper.AlbumColumns.AlbumCreator +","+
                " m."+ DbHelper.MemoryColumns.MemoryPath +","+
                " m."+ DbHelper.MemoryColumns.MemoryType +""+
                " FROM "+dbh.TBL_ALBUMS+" a"+
                " LEFT OUTER JOIN "+dbh.TBL_MEMORIES+" m"+
                " ON a."+ DbHelper.AlbumColumns.AlbumThumbnail +"="+"m."+ DbHelper.MemoryColumns.MemoryId;/* +
                " WHERE"+
                " a."+ DbHelper.AlbumColumns.AlbumCreator +"=?";*/
        try{
            res =  db.rawQuery(sql,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
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

    //SPECIFIC
    protected Cursor getC(int albumId) {
        Cursor res = null;
        String sql = "SELECT a."+ DbHelper.AlbumColumns.AlbumId +","+
                " a."+ DbHelper.AlbumColumns.AlbumTitle +","+
                " a."+ DbHelper.AlbumColumns.AlbumCreator +","+
                " a."+ DbHelper.AlbumColumns.AlbumThumbnail +", "+
                " m."+ DbHelper.MemoryColumns.MemoryId +", "+
                " m."+ DbHelper.MemoryColumns.MemoryType +", "+
                " m."+ DbHelper.MemoryColumns.MemoryPath +", "+
                " m."+ DbHelper.MemoryColumns.MemoryTitle +""+
                " FROM "+dbh.TBL_ALBUMS+" a"+
                " LEFT OUTER JOIN "+dbh.TBL_MEMORIES+" m"+
                " ON a."+ DbHelper.AlbumColumns.AlbumThumbnail +"="+"m."+ DbHelper.MemoryColumns.MemoryId +
                " WHERE"+
                " a."+ DbHelper.AlbumColumns.AlbumId +"=?";
        try{
            res =  db.rawQuery(sql,new String[]{""+albumId});
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }


    public boolean updateAlbum(Album a) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_ALBUMS+" SET "+
                DbHelper.AlbumColumns.AlbumTitle +"=? WHERE "+ DbHelper.AlbumColumns.AlbumId +"=?");
        stmt.bindString(1, a.getName());
        stmt.bindLong(2,a.getId());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    public boolean updateAlbumContents(int albumId, List<Integer> selectedMemories, List<Integer> prevSelectedMemories) {
        //differences!!!
        try{
            for(int m : selectedMemories) {
                if(!prevSelectedMemories.contains(m)){
                    insertAlbumMemory(albumId,m);
                }
            }
            for(int m : prevSelectedMemories) {
                if (!selectedMemories.contains(m)) {
                    deleteAlbumMemory(albumId, m);
                }
            }
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    //OTHERS
    public Cursor getSelectedMemoriesC(int albumId) {
        Cursor res = null;
        String[] select = new String[]{DbHelper.AlbumsMemoriesColumns.AMMemory.toString()};
        String where = DbHelper.AlbumsMemoriesColumns.AMAlbum +"=?";
        try{
            res = db.query(dbh.TBL_ALBUMS_MEMORIES, select, where, new String[]{ ""+albumId}, null, null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    protected int insertAlbum(Album newAlbum) {
        int id = -1;
        try{
            ContentValues cv = new ContentValues();
            cv.put(DbHelper.AlbumColumns.AlbumTitle.toString(), newAlbum.getName());
            cv.put(DbHelper.AlbumColumns.AlbumCreator.toString(), newAlbum.getAuthorId());
            cv.put(DbHelper.AlbumColumns.AlbumThumbnail.toString(), newAlbum.getThumbnail().getId());
            //execute
            id = (int) db.insert(dbh.TBL_ALBUMS,null,cv);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return id;
    }
    protected void insertAlbumMemory(int albumId, Integer mem) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_ALBUMS_MEMORIES+" VALUES(NULL,?,?)");
        stmt.bindLong(1, albumId);
        stmt.bindLong(2, mem);
        stmt.execute();
    }
    protected void deleteAlbumMemory(int albumId, int m) {
        SQLiteStatement stmt = db.compileStatement("DELETE FROM "+dbh.TBL_ALBUMS_MEMORIES+" WHERE "+ DbHelper.AlbumsMemoriesColumns.AMAlbum +"=? AND "+ DbHelper.AlbumsMemoriesColumns.AMMemory +"=?");
        stmt.bindLong(1, albumId);
        stmt.bindLong(2, m);
        stmt.execute();
    }

    protected boolean insertAlbumMemories(int albumId,List<Integer> memories) {
        boolean result = true;
        try{
            if(albumId==-1) {
                throw new SQLException("AMAlbum '"+albumId+"' does not exist yet, so no memories can be inserted (AlbumRepository.insertAlbumMemories)");
            }
            for(Integer mem : memories) {
                insertAlbumMemory(albumId, mem);
            }
        }catch(SQLException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
