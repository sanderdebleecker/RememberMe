package be.sanderdebleecker.herinneringsapp.Data.Repositories;


import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

/*
* low-level memory data-access
* returns cursors
* */
//TODO : check creation of memory with location and retention of it through getMappedC()
public class MemoryRepository extends BaseRepository {
    public MemoryRepository(Context context) {
        super(context);
    }
    public Cursor getC(int id) {
        Cursor res=null;
        try {
            res = db.query(dbh.TBL_MEMORIES, MemoriesDbHelper.MemoryColumns.getColumns(), MemoriesDbHelper.MemoryColumns.MemoryId +"=?",new String[]{""+id},null,null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    public Cursor getC(String title) {
        Cursor res = db.query(dbh.TBL_MEMORIES,
                null,
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?",
                new String[]{title},
                null,null,null,null);
        return res;
    }

    public Cursor getAllC(int userId) {
        Cursor res = null;
        try{
            String selection = MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?";
            String[] selectionArgs = new String[]{""+userId};
            res = db.query(dbh.TBL_MEMORIES, MemoriesDbHelper.MemoryColumns.getColumns(),selection, selectionArgs, null, null, MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString());
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    public Cursor getAllCFromAlbums(List<Integer> albumIds) {
        if(albumIds.size()<1) return null;
        MergeCursor res = null;
        try{
            //RawQuery?
            int size = albumIds.size();
            Cursor[] rawQueries = new Cursor[size];
            for(int i=0;i<size;i++) {
                String sql = "SELECT * FROM "+ dbh.TBL_MEMORIES+" m INNER JOIN "+ dbh.TBL_ALBUMS_MEMORIES+" am"+
                        " ON m."+ MemoriesDbHelper.MemoryColumns.MemoryId +"="+"am."+ MemoriesDbHelper.AlbumsMemoriesColumns.AMMemory +" WHERE "+
                        " am."+ MemoriesDbHelper.AlbumsMemoriesColumns.AMAlbum +" = ?";
                rawQueries[i] =  db.rawQuery(sql,new String[]{""+albumIds.get(i)});
            }
            res = new MergeCursor(rawQueries);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return res;
    }
    public boolean insertMemory(Memory newMemory) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_MEMORIES+" VALUES(NULL,?,?,?,NULL,NULL,NULL,?,?,?)");
        stmt.bindString(1, newMemory.getTitle());
        stmt.bindString(2, newMemory.getDescription());
        stmt.bindString(3, newMemory.getDate());
        stmt.bindLong(  4, newMemory.getCreator());
        stmt.bindString(5, newMemory.getPath());
        stmt.bindString(6, newMemory.getType());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    public boolean insertMemoryWithLocation(Memory newMemory) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_MEMORIES+" VALUES(NULL,?,?,?,?,?,?,?,?,?)");
        stmt.bindString(1, newMemory.getTitle());
        stmt.bindString(2, newMemory.getDescription());
        stmt.bindString(3, newMemory.getDate());
        stmt.bindDouble(4, newMemory.getLocation().getLat());
        stmt.bindDouble(5, newMemory.getLocation().getLng());
        stmt.bindString(6, newMemory.getLocation().getName());
        stmt.bindLong(7,   newMemory.getCreator());
        stmt.bindString(8, newMemory.getPath());
        stmt.bindString(9, newMemory.getType());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    public boolean updateMemory(Memory m) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_MEMORIES+" SET "+
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDescription +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryPath +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryType +"=? WHERE "+ MemoriesDbHelper.MemoryColumns.MemoryId +"=?");
        stmt.bindString(1, m.getTitle());
        stmt.bindString(2, m.getDescription());
        stmt.bindString(3, m.getDate());
        stmt.bindLong(4, m.getCreator());
        stmt.bindString(5, m.getPath());
        stmt.bindString(6, m.getType());
        stmt.bindLong(7, m.getId());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    public boolean updateMemoryWithLocation(Memory m) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_MEMORIES+" SET "+
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDescription +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryLocationLat +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryLocationLong +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryLocationName +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryPath +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryType +"=? WHERE "+ MemoriesDbHelper.MemoryColumns.MemoryId +"=?");
        stmt.bindString(1, m.getTitle());
        stmt.bindString(2, m.getDescription());
        stmt.bindString(3, m.getDate());
        Location l = m.getLocation();
        stmt.bindDouble(4, l.getLat());
        stmt.bindDouble(5, l.getLng());
        stmt.bindString(6, l.getName());
        stmt.bindLong(7, m.getCreator());
        stmt.bindString(8, m.getPath());
        stmt.bindString(9, m.getType());
        stmt.bindLong(10, m.getId());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }
    //SPECIFIC
    public Cursor getFilteredC(String username, String keyword) {
        Cursor res = null;
        keyword = "%"+keyword+"%";
        String sql = "SELECT * FROM "+ dbh.TBL_MEMORIES+" m INNER JOIN "+ dbh.TBL_USERS+" u"+
                " ON m."+ MemoriesDbHelper.MemoryColumns.MemoryCreator +"="+"u."+ MemoriesDbHelper.UserColumns.UserId +" WHERE"+
                " ( m."+ MemoriesDbHelper.MemoryColumns.MemoryTitle +" LIKE ? OR"+
                " m."+ MemoriesDbHelper.MemoryColumns.MemoryLocationName +" LIKE ? OR"+
                " u."+ MemoriesDbHelper.UserColumns.UserFirstName +" LIKE ? OR"+
                " u."+ MemoriesDbHelper.UserColumns.UserLastName +" LIKE ? OR"+
                " u."+ MemoriesDbHelper.UserColumns.UserName+" LIKE ? ) AND"+
                " u."+ MemoriesDbHelper.UserColumns.UserName+" LIKE ?";
        try{
            res =  db.rawQuery(sql,new String[]{keyword,keyword,keyword,keyword,keyword,username});
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    public Cursor getMappedC() {
        Cursor res = null;
        String where = MemoriesDbHelper.MemoryColumns.MemoryLocationLat +" IS NOT NULL AND "+ MemoriesDbHelper.MemoryColumns.MemoryLocationLong +" IS NOT NULL";
        String[] select = new String[]{MemoriesDbHelper.MemoryColumns.MemoryId.toString(), MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(), MemoriesDbHelper.MemoryColumns.MemoryLocationLong.toString(), MemoriesDbHelper.MemoryColumns.MemoryLocationLat.toString()};
        try{
            res = db.query(dbh.TBL_MEMORIES, select, where, null, null, null, MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString());
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
