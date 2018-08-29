package com.android.lddm;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class BDControl extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LDDM.db";

    private static final String REAL_TYPE = " REAL";
    private static final String BLOB_TYPE = " BLOB";
    private static final String DATATIME_TYPE = " DATATIME";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataSource.Dados.TABLE_NAME + " (" +
                    DataSource.Dados._ID + " INTEGER PRIMARY KEY," +
                    DataSource.Dados.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                    DataSource.Dados.COLUMN_NAME_LOGITUDE + REAL_TYPE + COMMA_SEP +
                    DataSource.Dados.COLUMN_NAME_FOTO + BLOB_TYPE + COMMA_SEP +
                    DataSource.Dados.COLUMN_NAME_DATA + DATATIME_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataSource.Dados.TABLE_NAME;


    public BDControl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
