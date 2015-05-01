package com.example.gui.myplaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLOpenHelper extends SQLiteOpenHelper {

	public MySQLOpenHelper(Context context) {
		super(context, "myplaces.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.beginTransaction();
        arg0.execSQL("CREATE TABLE myplaces (id INTEGER PRIMARY KEY AUTOINCREMENT, latitud DOUBLE, longitud DOUBLE, name TEXT NOT NULL, description TEXT, image TEXT);");        arg0.setTransactionSuccessful();
        arg0.endTransaction();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL("DROP TABLE IF EXISTS myplaces");
		onCreate(arg0);
	}

}
