package be.sanderdebleecker.herinneringsapp.Data.Repositories;


import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import java.util.List;
import java.util.UUID;

import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

/**
 * Provides base methods to access TBL_MEMORIES in the local database
 */
public class MemoryRepository extends BaseRepository {
    public MemoryRepository(Context context) {
        super(context);
    }

    /**
     * Queries the local database for a memory
     * @param identifier uuid of the memory
     * @return Cursor containing memory
     */
    public Cursor getByCursor(String identifier) {
        Cursor res=null;
        String[] columns = new String[] {
                MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDescription.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationLat.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationLong.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryLocationName.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryCreator.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryType.toString(),
        };
        try {
            res = db.query(dbh.TBL_MEMORIES,columns, MemoriesDbHelper.MemoryColumns.MemoryUuid +"=?",new String[]{""+identifier},null,null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for a memory
     * @param title of the memory
     * @return Cursor containing memory
     */
    public Cursor getCursorByTitle(String title) {
        return db.query(dbh.TBL_MEMORIES,
                null,
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?",
                new String[]{title},
                null,null,null,null);
    }

    /**
     * Queries the local database for all memories
     * @param userIdentifier uuid of the user
     * @return Cursor containing several memories
     */
    public Cursor getAllC(String userIdentifier) {
        String[] columns = new String[]{
                MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryType.toString()
        };
                Cursor res = null;
        try{
            String selection = MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?";
            String[] selectionArgs = new String[]{""+userIdentifier};
            res = db.query(dbh.TBL_MEMORIES, columns,selection, selectionArgs, null, null, MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString());
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for all related memories
     * @param albumIdentifiers enumeration of album identifiers
     * @return Cursor containing all memories
     */
    public Cursor getAllCFromAlbums(List<String> albumIdentifiers) {
        if(albumIdentifiers.size()<1) return null;
        MergeCursor allAlbumMemoriesCursor = null;
        try{
            int size = albumIdentifiers.size();
            Cursor[] albumMemoriesCursor = new Cursor[size];
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String join =  dbh.TBL_MEMORIES+" INNER JOIN "+ dbh.TBL_ALBUMS_MEMORIES+
                    " ON "+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"="+ MemoriesDbHelper.AlbumsMemoriesColumns.AMMemory;
            String selection =  MemoriesDbHelper.AlbumsMemoriesColumns.AMAlbum +" = ?";
            String projection[] = new String[] {
                    MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                    MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(),
                    MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString(),
                    MemoriesDbHelper.MemoryColumns.MemoryType.toString(),
                    MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
            };
            for(int i=0;i<size;i++) {
                qb.setTables(join);
                albumMemoriesCursor[i] =  qb.query(db,projection,selection,new String[]{""+albumIdentifiers.get(i)},null,null,null);
            }
            allAlbumMemoriesCursor = new MergeCursor(albumMemoriesCursor);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return allAlbumMemoriesCursor;
    }

    /**
     * Insert a new memory into the local database without specified location
     * @param memory Memory te be inserted
     * @return boolean success
     */
    public boolean insertMemory(Memory memory) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_MEMORIES+" VALUES(?,?,?,?,NULL,NULL,NULL,?,?,?)");
        String identifier = UUID.randomUUID().toString().replaceAll("-","");
        stmt.bindString(1, identifier);
        stmt.bindString(2, memory.getTitle());
        stmt.bindString(3, memory.getDescription());
        stmt.bindString(4, memory.getDate());
        stmt.bindLong(  5, memory.getCreator());
        stmt.bindString(6, memory.getPath());
        stmt.bindString(7, memory.getType());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Inserts a new memory into the local database
     * @param memory Memory te be inserted
     * @return boolean success
     */
    public boolean insertMemoryWithLocation(Memory memory) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_MEMORIES+" VALUES(NULL,?,?,?,?,?,?,?,?,?)");
        stmt.bindString(1, memory.getTitle());
        stmt.bindString(2, memory.getDescription());
        stmt.bindString(3, memory.getDate());
        stmt.bindDouble(4, memory.getLocation().getLat());
        stmt.bindDouble(5, memory.getLocation().getLng());
        stmt.bindString(6, memory.getLocation().getName());
        stmt.bindLong(7,   memory.getCreator());
        stmt.bindString(8, memory.getPath());
        stmt.bindString(9, memory.getType());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Updates a memory in the local database
     * @param memory Memory
     * @return
     */
    public boolean updateMemory(Memory memory) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_MEMORIES+" SET "+
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDescription +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryPath +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryType +"=? WHERE "+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"=?");
        stmt.bindString(1, memory.getTitle());
        stmt.bindString(2, memory.getDescription());
        stmt.bindString(3, memory.getDate());
        stmt.bindLong(4, memory.getCreator());
        stmt.bindString(5, memory.getPath());
        stmt.bindString(6, memory.getType());
        stmt.bindString(7, memory.getUuid());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Updates a memory in the local database with memory
     * @param memory Memory
     * @return
     */
    public boolean updateMemoryWithLocation(Memory memory) {
        SQLiteStatement stmt = db.compileStatement("UPDATE "+dbh.TBL_MEMORIES+" SET "+
                MemoriesDbHelper.MemoryColumns.MemoryTitle +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDescription +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryDateTime +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryLocationLat +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryLocationLong +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryLocationName +"=?,"+
                MemoriesDbHelper.MemoryColumns.MemoryCreator +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryPath +"=?,"+ MemoriesDbHelper.MemoryColumns.MemoryType +"=? WHERE "+ MemoriesDbHelper.MemoryColumns.MemoryUuid +"=?");
        stmt.bindString(1, memory.getTitle());
        stmt.bindString(2, memory.getDescription());
        stmt.bindString(3, memory.getDate());
        Location l = memory.getLocation();
        stmt.bindDouble(4, l.getLat());
        stmt.bindDouble(5, l.getLng());
        stmt.bindString(6, l.getName());
        stmt.bindLong(7, memory.getCreator());
        stmt.bindString(8, memory.getPath());
        stmt.bindString(9, memory.getType());
        stmt.bindString(10, memory.getUuid());
        try{
            stmt.execute();
            return true;
        }catch(SQLException e) {
            return false;
        }
    }

    /**
     * Queries the local database for memories filtered on title/author/locationname
     * @param username String username
     * @param keyword String keyword
     * @return
     */
    public Cursor getFilteredC(String username, String keyword) {
        Cursor res = null;
        keyword = "%"+keyword+"%";
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String join = dbh.TBL_MEMORIES+" INNER JOIN "+ dbh.TBL_USERS+
                " ON "+ MemoriesDbHelper.MemoryColumns.MemoryCreator +"="+MemoriesDbHelper.UserColumns.UserUuid;
        qb.setTables(join);
        String[] projection = new String[] {
                MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryType.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryPath.toString(),
                MemoriesDbHelper.MemoryColumns.MemoryDateTime.toString()
        };
        String selection = MemoriesDbHelper.MemoryColumns.MemoryTitle +" LIKE ? OR "+
                MemoriesDbHelper.MemoryColumns.MemoryLocationName +" LIKE ? OR "+
                MemoriesDbHelper.UserColumns.UserFirstName +" LIKE ? OR "+
                MemoriesDbHelper.UserColumns.UserLastName +" LIKE ? OR "+
                MemoriesDbHelper.UserColumns.UserName+" LIKE ? ) AND "+
                MemoriesDbHelper.UserColumns.UserName+" LIKE ?";
        try{
            res = qb.query(db,projection,selection,new String[]{keyword,keyword,keyword,keyword,keyword,username},null,null,null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for memories with specified locations
     * @return Cursor memories
     */
    public Cursor getMappedC() {
        Cursor res = null;
        String where = MemoriesDbHelper.MemoryColumns.MemoryLocationLat +" IS NOT NULL AND "+ MemoriesDbHelper.MemoryColumns.MemoryLocationLong +" IS NOT NULL";
        String[] select = new String[]{MemoriesDbHelper.MemoryColumns.MemoryUuid.toString(), MemoriesDbHelper.MemoryColumns.MemoryTitle.toString(), MemoriesDbHelper.MemoryColumns.MemoryLocationLong.toString(), MemoriesDbHelper.MemoryColumns.MemoryLocationLat.toString()};
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
