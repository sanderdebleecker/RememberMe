package be.sanderdebleecker.herinneringsapp.Data.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;

import java.util.UUID;

import be.sanderdebleecker.herinneringsapp.Data.Databases.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.Crypto;
import be.sanderdebleecker.herinneringsapp.Models.User;

/**
 * Provides base methods to access TBL_USERS in the local database
 */
public class UserRepository extends BaseRepository {
    public UserRepository(Context context) {
        super(context);
    }

    /**
     * Queries the local database for all users
     * @return Cursor users
     */
    protected Cursor getAllAsCursor() {
        Cursor res = null;
        try{
            res = db.query(dbh.TBL_USERS, new String[] {MemoriesDbHelper.UserColumns.UserUuid.toString(), MemoriesDbHelper.UserColumns.UserName.toString()},
                    null, null, null, null, null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * Queries the local database for the total count of users
     * @return int userPopulation
     */
    public int count() {
        Cursor cursor = db.query(dbh.TBL_USERS,
                new String[]{ "count("+ MemoriesDbHelper.UserColumns.UserUuid.toString()+") AS count" },
                null,null,null,null,null,null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * Inserts a new user into the local database
     * @param user the model
     * @return String the uuid (identifier) of the new user or "" on failure
     */
    public String insert(User user) {
        user.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
        boolean success=false;
        ContentValues cv = new ContentValues();
        cv.put(MemoriesDbHelper.UserColumns.UserUuid.toString(),"X'"+user.getUuid()+"'");//SQLite 16 byte notation
        cv.put(MemoriesDbHelper.UserColumns.UserFirstName.toString(), user.getFirstName());
        cv.put(MemoriesDbHelper.UserColumns.UserLastName.toString(), user.getLastName());
        cv.put(MemoriesDbHelper.UserColumns.UserPassword.toString(), Crypto.md5(user.getPassword()));
        cv.put(MemoriesDbHelper.UserColumns.UserQuestion1.toString(), user.getQ1());
        cv.put(MemoriesDbHelper.UserColumns.UserQuestion2.toString(), user.getQ2());
        cv.put(MemoriesDbHelper.UserColumns.UserAnswer1.toString(), user.getA1());
        cv.put(MemoriesDbHelper.UserColumns.UserAnswer2.toString(), user.getA2());
        cv.put(MemoriesDbHelper.UserColumns.UserName.toString(), user.getUsername());

        try{
            db.insert(dbh.TBL_USERS,null,cv);
            success=true;
        }catch(SQLException e) {
            e.printStackTrace();
        }

        if(!success) return "";
        return user.getUuid();
    }





}
