package be.sanderdebleecker.herinneringsapp.Data;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.UserRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.Crypto;
import be.sanderdebleecker.herinneringsapp.Models.User;
import be.sanderdebleecker.herinneringsapp.Models.View.UserVM;

public class UserDA extends UserRepository {
    public UserDA(Context context) {
        super(context);
    }

    public boolean exists(String user) {
        Cursor res = db.query(dbh.TBL_USERS,
                new String[]{MemoriesDbHelper.UserColumns.UserId.toString()},
                MemoriesDbHelper.UserColumns.UserName+"=?",
                new String[]{user},
                null,null,null,null);
        int count = res.getCount();
        res.close();
        return !(count==0);
    }


    public int exists(String user, String pass) {
        Cursor res = db.query(dbh.TBL_USERS,
                new String[] {MemoriesDbHelper.UserColumns.UserId.toString()},
                MemoriesDbHelper.UserColumns.UserName.toString()+"=? AND "+ MemoriesDbHelper.UserColumns.UserPassword.toString()+"=?",
                new String[]{user, Crypto.md5(pass)},
                null,null,null,null);
        return (res.moveToNext()) ? res.getInt(res.getColumnIndex(MemoriesDbHelper.UserColumns.UserId.toString())) : -1;
    }
    public int getId(String username) {
        try{
            Cursor cursor = db.query(dbh.TBL_USERS,
                    new String[]{MemoriesDbHelper.UserColumns.UserId.toString()},
                    MemoriesDbHelper.UserColumns.UserName+"=?",
                    new String[]{username},
                    null,null,null,null);
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex(MemoriesDbHelper.UserColumns.UserId.toString()));
            cursor.close();
            return id;
        }catch (Exception e) {
            return -1;
        }
    }
    public List<UserVM> getAll() {
        ArrayList<UserVM> users = new ArrayList<>();
        Cursor c = getAllC();
        while(c.moveToNext()) {
            users.add(partiallyFrom(c));
        }
        return users;
    }
    public int insert(User user) {
        return insertUser(user);
    }
    public int count() {
        int count = getUserCount();
        return count;
    }

    public UserVM partiallyFrom(Cursor c) {
        UserVM user = new UserVM();
        user.setId(c.getInt(c.getColumnIndex(MemoriesDbHelper.UserColumns.UserId.toString())));
        user.setUsername(c.getString(c.getColumnIndex(MemoriesDbHelper.UserColumns.UserName.toString())));
        return user;
    }

}
