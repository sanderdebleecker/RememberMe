package be.sanderdebleecker.herinneringsapp.Data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Data.Repositories.TrustRepository;
import be.sanderdebleecker.herinneringsapp.Helpers.DbHelper;
import be.sanderdebleecker.herinneringsapp.Models.Trust;

public class TrustDA extends TrustRepository {
    public TrustDA(Context context) {
        super(context);
    }
    public ArrayList<Trust> getTrustList(int id) {
        ArrayList<Trust> trusts = new ArrayList<>();
        Cursor cursor = getAllC(id);
        while(cursor.moveToNext()) {
            Trust t = from(cursor);
            trusts.add(t);
        }
        cursor.close();
        return trusts;
    }


    public Trust from(Cursor cursor) {
        Trust t = new Trust();
        t.setA(new Trust.Party(
                cursor.getInt(cursor.getColumnIndex(DbHelper.TrustColumns.TrustSource.toString()))
        ));
        t.setB(new Trust.Party(
                cursor.getInt(cursor.getColumnIndex(DbHelper.TrustColumns.TrustDestination.toString()))
        ));
        return t;
    }

}
