package be.sanderdebleecker.herinneringsapp.Data;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.UserRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.MemoriesDbHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.Crypto;
import be.sanderdebleecker.herinneringsapp.Models.View.UserVM;


/**
 * Sander De Bleecker
 */


/**
 * Provides methods to access TBL_USERS in the local database
 */
public class UserDA extends UserRepository {
    public UserDA(Context context) {
        super(context);
    }

    /**
     * Queries the local database weather a username getIdentifier or not.
     * @param userName username of the user
     * @return boolean getIdentifier
     */
    public boolean exists(String userName) {
        Cursor res = db.query(dbh.TBL_USERS,
                new String[]{MemoriesDbHelper.UserColumns.UserUuid.toString()},
                MemoriesDbHelper.UserColumns.UserName+"=?",
                new String[]{userName},
                null,null,null,null);
        int count = res.getCount();
        res.close();
        return !(count==0);
    }

    /**
     * Queries the local database for a user's identifier
     * @param userName username of the user
     * @param pass corresponding password
     * @return String uuid (identifier) of the user or an empty string in case none found
     */
    public String getIdentifier(String userName, String pass) {
        Cursor res = db.query(dbh.TBL_USERS,
                new String[] {MemoriesDbHelper.UserColumns.UserUuid.toString()},
                MemoriesDbHelper.UserColumns.UserName.toString()+"=? AND "+ MemoriesDbHelper.UserColumns.UserPassword.toString()+"=?",
                new String[]{userName, Crypto.md5(pass)},
                null,null,null,null);
        return (res.moveToNext()) ? res.getString(res.getColumnIndex(MemoriesDbHelper.UserColumns.UserUuid.toString())) : "";
    }

    /**
     * Queries the local database for user's identifier
     * @param username username of the user
     * @return String uuid (identifier) of the user or an empty string in case none found
     */
    public String getId(String username) {
        try{
            Cursor cursor = db.query(dbh.TBL_USERS,
                    new String[]{MemoriesDbHelper.UserColumns.UserUuid.toString()},
                    MemoriesDbHelper.UserColumns.UserName+"=?",
                    new String[]{username},
                    null,null,null,null);
            cursor.moveToNext();
            String uuid = cursor.getString(cursor.getColumnIndex(MemoriesDbHelper.UserColumns.UserUuid.toString()));
            cursor.close();
            return uuid;
        }catch (Exception e) {
            return "";
        }
    }

    /**
     * Queries the local database for all users
     * @return a list of UserVM (viewmodel) to display the users
     */
    public List<UserVM> getAll() {
        ArrayList<UserVM> users = new ArrayList<>();
        Cursor c = getAllAsCursor();
        while(c.moveToNext()) {
            users.add(partiallyFrom(c));
        }
        return users;
    }

    /**
     * Derives UserVM (viewmodel) from a cursor loaded with userdata
     * @param c the cursor
     * @return the viewmodel from the user
     */
    public UserVM partiallyFrom(Cursor c) {
        UserVM user = new UserVM();
        user.setUuid(c.getString(c.getColumnIndex(MemoriesDbHelper.UserColumns.UserUuid.toString())));
        user.setUsername(c.getString(c.getColumnIndex(MemoriesDbHelper.UserColumns.UserName.toString())));
        return user;
    }

}
