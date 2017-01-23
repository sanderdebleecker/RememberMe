package be.sanderdebleecker.herinneringsapp.Data.Repositories;


import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
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
            res = db.query(dbh.TBL_MEMORIES, DbHelper.MemoryColumns.getColumns(), DbHelper.MemoryColumns.MemoryId +"=?",new String[]{""+id},null,null,null);
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
                DbHelper.MemoryColumns.MemoryTitle +"=?",
                new String[]{title},
                null,null,null,null);
        return res;
    }

    public Cursor getAllC(int userId) {
        Cursor res = null;
        try{
            String selection = DbHelper.MemoryColumns.MemoryCreator +"=?";
            String[] selectionArgs = new String[]{""+userId};
            res = db.query(dbh.TBL_MEMORIES, DbHelper.MemoryColumns.getColumns(),selection, selectionArgs, null, null, DbHelper.MemoryColumns.MemoryDateTime.toString());
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
                        " ON m."+ DbHelper.MemoryColumns.MemoryId +"="+"am."+ DbHelper.AlbumsMemoriesColumns.AMMemory +" WHERE "+
                        " am."+ DbHelper.AlbumsMemoriesColumns.AMAlbum +" = ?";
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
                DbHelper.MemoryColumns.MemoryTitle +"=?,"+ DbHelper.MemoryColumns.MemoryDescription +"=?,"+ DbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                DbHelper.MemoryColumns.MemoryCreator +"=?,"+ DbHelper.MemoryColumns.MemoryPath +"=?,"+ DbHelper.MemoryColumns.MemoryType +"=? WHERE "+ DbHelper.MemoryColumns.MemoryId +"=?");
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
                DbHelper.MemoryColumns.MemoryTitle +"=?,"+ DbHelper.MemoryColumns.MemoryDescription +"=?,"+ DbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                DbHelper.MemoryColumns.MemoryLocationLat +"=?,"+ DbHelper.MemoryColumns.MemoryLocationLong +"=?,"+ DbHelper.MemoryColumns.MemoryLocationName +"=?,"+
                DbHelper.MemoryColumns.MemoryCreator +"=?,"+ DbHelper.MemoryColumns.MemoryPath +"=?,"+ DbHelper.MemoryColumns.MemoryType +"=? WHERE "+ DbHelper.MemoryColumns.MemoryId +"=?");
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
                " ON m."+ DbHelper.MemoryColumns.MemoryCreator +"="+"u."+ DbHelper.UserColumns.UserId +" WHERE"+
                " ( m."+ DbHelper.MemoryColumns.MemoryTitle +" LIKE ? OR"+
                " m."+ DbHelper.MemoryColumns.MemoryLocationName +" LIKE ? OR"+
                " u."+ DbHelper.UserColumns.UserFirstName +" LIKE ? OR"+
                " u."+ DbHelper.UserColumns.UserLastName +" LIKE ? OR"+
                " u."+ DbHelper.UserColumns.UserName+" LIKE ? ) AND"+
                " u."+ DbHelper.UserColumns.UserName+" LIKE ?";
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
        String where = DbHelper.MemoryColumns.MemoryLocationLat +" IS NOT NULL AND "+ DbHelper.MemoryColumns.MemoryLocationLong +" IS NOT NULL";
        String[] select = new String[]{DbHelper.MemoryColumns.MemoryId.toString(), DbHelper.MemoryColumns.MemoryTitle.toString(), DbHelper.MemoryColumns.MemoryLocationLong.toString(), DbHelper.MemoryColumns.MemoryLocationLat.toString()};
        try{
            res = db.query(dbh.TBL_MEMORIES, select, where, null, null, null, DbHelper.MemoryColumns.MemoryDateTime.toString());
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

}
