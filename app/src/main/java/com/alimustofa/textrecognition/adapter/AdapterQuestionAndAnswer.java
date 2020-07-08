package com.alimustofa.textrecognition.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alimustofa.textrecognition.R;
import com.alimustofa.textrecognition.database.DbQuestionAndAnswer;

import java.util.ArrayList;

public class AdapterQuestionAndAnswer extends RecyclerView.Adapter<AdapterQuestionAndAnswer.ViewHolder> {

    private ArrayList questionList;
    private ArrayList answerList;

    public AdapterQuestionAndAnswer(ArrayList questionList, ArrayList answerList){
        this.answerList = answerList;
        this.questionList = questionList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mQuestionTv, mAnswerTv;
        private ImageButton mOverflowIb;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mQuestionTv = itemView.findViewById(R.id.questionTv);
            mAnswerTv = itemView.findViewById(R.id.answerTv);
            mOverflowIb = itemView.findViewById(R.id.overflow);
        }
    }

    @NonNull
    @Override
    public AdapterQuestionAndAnswer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_question_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterQuestionAndAnswer.ViewHolder holder, int position) {
        final String Question = (String) questionList.get(position);
        final String Answer = (String) answerList.get(position);
        holder.mQuestionTv.setText(Question);
        holder.mAnswerTv.setText(Answer);
        holder.mOverflowIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("TAG", "onClick: ");
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete:
                                deleteData(view, holder, Answer, Question);
                                break;
                            case R.id.update:
                                //updateData(view, holder);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deleteData(View view, ViewHolder holder,  String answer, String question) {
        DbQuestionAndAnswer db = new DbQuestionAndAnswer(view.getContext());
        SQLiteDatabase deleteData = db.getWritableDatabase();

        String selection = DbQuestionAndAnswer.column.answer +" LIKE ?";
        String[] stringsArgs = {holder.mAnswerTv.getText().toString()};
        deleteData.delete(DbQuestionAndAnswer.column.tableName, selection, stringsArgs);

        int position = questionList.indexOf(question);
        questionList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(view.getContext(), "Data "+answer+" success deleted", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

}
