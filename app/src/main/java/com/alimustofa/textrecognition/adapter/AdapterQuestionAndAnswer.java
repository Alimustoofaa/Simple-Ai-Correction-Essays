package com.alimustofa.textrecognition.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alimustofa.textrecognition.ListCorrection;
import com.alimustofa.textrecognition.MainActivity;
import com.alimustofa.textrecognition.R;
import com.alimustofa.textrecognition.UpdateQuestionAnswer;
import com.alimustofa.textrecognition.database.DbQuestionAndAnswer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.createDeviceProtectedStorageContext;
import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;

public class AdapterQuestionAndAnswer extends RecyclerView.Adapter<AdapterQuestionAndAnswer.ViewHolder> {

    private ArrayList idList;
    private ArrayList questionList;
    private ArrayList answerList;
    private ArrayList imQuestionList;
    private ArrayList imAnswerList;

    public AdapterQuestionAndAnswer(ArrayList<String> idList, ArrayList questionList, ArrayList answerList, ArrayList imQuestionList, ArrayList imAnswerList){
        this.idList = idList;
        this.answerList = answerList;
        this.questionList = questionList;
        this.imQuestionList = imQuestionList;
        this.imAnswerList = imAnswerList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mQuestionTv, mAnswerTv;
        private ImageView mQuestionIv, mAnswerIv;
        private ImageButton mOverflowIb;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mQuestionTv = itemView.findViewById(R.id.questionTv);
            mAnswerTv = itemView.findViewById(R.id.answerTv);
            mQuestionIv = itemView.findViewById(R.id.questionIv);
            mAnswerIv = itemView.findViewById(R.id.answerIv);
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
        final String Id = (String) idList.get(position);
        final String Question = (String) questionList.get(position);
        final String Answer = (String) answerList.get(position);
        final byte[] imQuestion = (byte[]) imQuestionList.get(position);
        final byte[] imAnswer = (byte[]) imAnswerList.get(position);
        holder.mQuestionTv.setText(Question);
        holder.mAnswerTv.setText(Answer);

        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imQuestion, 0, imQuestion.length);
        holder.mQuestionIv.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeByteArray(imAnswer, 0, imAnswer.length);
        holder.mAnswerIv.setImageBitmap(bitmap2);

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
                                updateData(view, holder, imQuestion, Id);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void updateData(View view, ViewHolder holder, byte[] imQuestion, String id) {

        Intent intent = new Intent(view.getContext(), UpdateQuestionAnswer.class);
        Bundle bundle = new Bundle();
        bundle.putString("Id", id);
        bundle.putString("Question", holder.mQuestionTv.getText().toString());
        bundle.putString("Answer", holder.mAnswerTv.getText().toString());
        bundle.putByteArray("ImQuestion", imQuestion);
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
        ((Activity)view.getContext()).finish();
    }

    private byte[] imageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
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
        deleteData.close();
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

}
