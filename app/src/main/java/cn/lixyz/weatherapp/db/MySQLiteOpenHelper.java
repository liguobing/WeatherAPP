package cn.lixyz.weatherapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LGB on 2016/4/5.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private String str = "create table if not exists city(_id integer primary key autoincrement,city text,cnty text,id text,lat text,lon text,prov text )";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
