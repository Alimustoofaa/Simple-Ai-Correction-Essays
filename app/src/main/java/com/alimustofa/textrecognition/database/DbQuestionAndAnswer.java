package com.alimustofa.textrecognition.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbQuestionAndAnswer extends SQLiteOpenHelper {
    static abstract class column implements BaseColumns {
        static final String tableName = "tbl_qna";
        static final String id = "id";
        static final String question = "question";
        static final String answer = "answer";
    }
    private static final String DATABASE_NAME = "db_correction.db";
    private static final int DATABASE_VERSION = 1;

    private static  final String SQL_CREATE_ENTRIES = "CREATE TABLE "+column.tableName+" ("+
            column.id+" INTEGER PRIMARY KEY  AUTOINCREMENT,"+
            column.question+" TEXT NOT NULL,"+
            column.answer+" TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+column.tableName;

    DbQuestionAndAnswer(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int ii){
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
