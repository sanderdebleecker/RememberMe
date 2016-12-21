package be.sanderdebleecker.herinneringsapp.Data.Repositories;

//TODO: limit accessed columns in getAllC

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.Crypto;
import be.sanderdebleecker.herinneringsapp.Models.User;

public class UserRepository extends BaseRepository {
    public UserRepository(Context context) {
        super(context);
    }

    protected Cursor getAllC() {
        Cursor res = null;
        try{
            res = db.query(dbh.TBL_USERS, new String[] {DbHelper.UserColumns.UserId.toString(),DbHelper.UserColumns.UserName.toString()},
                    null, null, null, null, null);
        }catch(SQLiteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    protected int getUserCount() {
        Cursor cursor = db.query(dbh.TBL_USERS,
                new String[]{ "count("+ DbHelper.UserColumns.UserId.toString()+") AS count" },
                null,null,null,null,null,null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
    protected int insertUser(User newUser) {
        //SQLiteStatement stmt = db.compileStatement("INSERT INTO "+dbh.TBL_USERS+" VALUES(NULL,?,?,?,?,?,?,?,?)");
        ContentValues values = new ContentValues();
        values.put(DbHelper.UserColumns.UserFirstName.toString(), newUser.getFirstName());
        values.put(DbHelper.UserColumns.UserLastName.toString(), newUser.getLastName());
        values.put(DbHelper.UserColumns.UserPassword.toString(), Crypto.md5(newUser.getPassword()));
        values.put(DbHelper.UserColumns.UserQuestion1.toString(), newUser.getQ1());
        values.put(DbHelper.UserColumns.UserQuestion2.toString(), newUser.getQ2());
        values.put(DbHelper.UserColumns.UserAnswer1.toString(), newUser.getA1());
        values.put(DbHelper.UserColumns.UserAnswer2.toString(), newUser.getA2());
        values.put(DbHelper.UserColumns.UserName.toString(), newUser.getUsername());
        int id = -1;
        try{
            id = (int) db.insert(dbh.TBL_USERS,"",values);
        }catch(SQLException e) {
            e.printStackTrace();
        }finally {
            return id;
        }
    }



}
