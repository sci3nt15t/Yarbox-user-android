package app.yarbax.com.Utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by shayanrhm on 1/20/19.
 */


public class MyDb {


    SQLiteDatabase database;
    public MyDb()
    {
        database = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/mydb.sql",null);
        database.execSQL("CREATE TABLE IF NOT EXISTS fav(\n" +
                "  id INTEGER not null primary key AUTOINCREMENT,\n" +
                "  location TEXT NOT NULL,\n" +
                "  province TEXT,\n" +
                "  city TEXT,\n" +
                "  address TEXT,\n" +
                "  plaque TEXT,\n" +
                "  name TEXT,\n" +
                "  number TEXT,\n" +
                "  lat DOUBLE,\n" +
                "  lng DOUBLE,\n" +
                " title TEXT not null )");
    }
    public void insert(String query)
    {
        database.execSQL(query);
    }
    public Cursor get(String query)
    {
        Cursor crs = database.rawQuery(query,null);
        return crs;
    }

}
