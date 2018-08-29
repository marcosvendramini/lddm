package com.android.lddm;


import android.provider.BaseColumns;

public class DataSource {

    private DataSource(){}

    public static class Dados implements BaseColumns{

        public static final String TABLE_NAME = "POSTOS";
        public static final String COLUMN_NAME_LATITUDE = "LATITUDE";
        public static final String COLUMN_NAME_LOGITUDE = "LOGITUDE";
        public static final String COLUMN_NAME_FOTO ="FOTO";
        public static final String COLUMN_NAME_DATA="DATA";
    }

}
