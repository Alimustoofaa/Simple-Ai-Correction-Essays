package com.alimustofa.textrecognition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.alimustofa.textrecognition.adapter.AdapterQuestionAndAnswer;
import com.alimustofa.textrecognition.database.DbQuestionAndAnswer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListQuestionAnswer extends AppCompatActivity {

    private DbQuestionAndAnswer db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<AdapterQuestionAndAnswer.ViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> idList;
    private ArrayList<String> questionList;
    private ArrayList<String> answerList;
    private ArrayList<byte[]> ImQuestionList;
    private ArrayList<byte[]> ImAnswerList;

    FloatingActionButton mAddFab;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_question_answer);

        idList = new ArrayList<String>();
        questionList = new ArrayList<String>();
        answerList = new ArrayList<String>();
        ImQuestionList = new ArrayList<byte[]>();
        ImAnswerList = new ArrayList<byte[]>();

        db = new DbQuestionAndAnswer(getBaseContext());

        recyclerView = findViewById(R.id.RcQnA);
        mAddFab = findViewById(R.id.AddItemBtn);

        getData();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new AdapterQuestionAndAnswer(idList, questionList, answerList, ImQuestionList, ImAnswerList);

        recyclerView.setAdapter(adapter);
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListQuestionAnswer.this, QuestionAnswer.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        SQLiteDatabase readData = db.getReadableDatabase();
        Cursor cursor = readData.rawQuery("SELECT * FROM "+DbQuestionAndAnswer.column.tableName, null);

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);

            idList.add(cursor.getString(0));
            questionList.add(cursor.getString(1));
            answerList.add(cursor.getString(2));
            ImQuestionList.add(cursor.getBlob(3));
            ImAnswerList.add(cursor.getBlob(4));
        }
    }

}