package com.alimustofa.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alimustofa.textrecognition.database.DbQuestionAndAnswer;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;

public class UpdateQuestionAnswer extends AppCompatActivity {

    EditText etQuestion, etAnswer;
    ImageView imQuestion, imAnswer;
    Button btnUpdate;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String[] cameraPermission;
    String[] storagePermission;

    String[] setImageItem;

    Uri image_uri;

    public String id;
    public DbQuestionAndAnswer db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Click + button to insert image");

        etQuestion = findViewById(R.id.resultEt_question);
        etAnswer = findViewById(R.id.resultEt_answer);
        imQuestion = findViewById(R.id.imageIv_question);
        imAnswer = findViewById(R.id.imageIv_answer);
        btnUpdate = findViewById(R.id.btn_save);

        db = new DbQuestionAndAnswer(getBaseContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            final byte[] ImQuestion = (byte[]) bundle.getByteArray("ImQuestion");
            Bitmap bitmap = BitmapFactory.decodeByteArray(ImQuestion, 0, ImQuestion.length);

            String Id = bundle.getString("Id");
            String question = bundle.getString("Question");
            String answer = bundle.getString("Answer");
            id = Id;
            etQuestion.setText(question);
            etAnswer.setText(answer);
            imQuestion.setImageBitmap(bitmap);
        }


        // camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestionAnswer();
                startActivity(new Intent(UpdateQuestionAnswer.this, ListQuestionAnswer.class));
                finish();
            }
        });
    }

    private void updateQuestionAnswer()  {

        String setAnswer = etAnswer.getText().toString();
        String setQuestion = etQuestion.getText().toString();
        byte[] setImageQuestion = imageToByte(imQuestion);
        byte[] setImageAnswer = imageToByte(imAnswer);
        if (!setAnswer.isEmpty() && !setQuestion.isEmpty() && imQuestion.getDrawable() != null && imAnswer.getDrawable() != null){
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DbQuestionAndAnswer.column.answer, setAnswer);
            contentValues.put(DbQuestionAndAnswer.column.question, setQuestion);
            contentValues.put(DbQuestionAndAnswer.column.imageQuestion, setImageQuestion);
            contentValues.put(DbQuestionAndAnswer.column.imageAnswer, setImageAnswer);

            try {
                String section = DbQuestionAndAnswer.column.id + " LIKE ?";
                String[] sectionArgs ={id};
                sqLiteDatabase.update(DbQuestionAndAnswer.column.tableName, contentValues, section, sectionArgs);
                Toast.makeText(this, "Success Update"+setQuestion, Toast.LENGTH_SHORT).show();
                clearData();
            }catch (SQLException e){
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Question or Answer is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] imageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void clearData() {
        etQuestion.setText("");
        etAnswer.setText("");
        imQuestion.setImageDrawable(null);
        imAnswer.setImageDrawable(null);
    }

    //action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // handle action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addImage){
            chaneImageDialog();
        }
        if (id == R.id.settings){
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void chaneImageDialog() {
        // item to display dialog
        String[] items = new String[0];
        if (imAnswer.getDrawable() == null && imQuestion.getDrawable() == null){
            items = new String[]{" Question", " Answer"};
        } else if (imAnswer.getDrawable() == null){
            items = new String[]{" Answer"};
        } else if (imQuestion.getDrawable() == null){
            items = new String[]{" Question"};
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Get Image");
        final String[] finalItems = items;
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setImageItem = new String[]{(String) Array.get(finalItems, 0)};
                    showImageImportDialog();
                }
                if (i == 1){
                    setImageItem = new String[]{(String) Array.get(finalItems, 1)};
                    showImageImportDialog();
                }
            }
        });
        dialog.create().show();
    }

    private void showImageImportDialog() {
        // item to display dialog
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        // set tittle
        dialog.setTitle("Get Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    if (!checkCameraPermission()){
                        // camera permission not allowed
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                }
                if (i == 1){
                    if (!checkStoragePermission()){
                        // camera permission not allowed
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        // set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camera, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    // handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // got image from gallery
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // got image from camera
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }

        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                if (Array.get(setImageItem, 0) == " Question"){
                    Log.d("TAG", "onClick: "+ Array.get(setImageItem, 0));
                    imQuestion.setImageURI(resultUri);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imQuestion.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!textRecognizer.isOperational()) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                        StringBuilder stringBuilder = new StringBuilder();
                        //get text from string builder until is no text
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                        }
                        etQuestion.setText(stringBuilder.toString());
                    }
                }
                if (Array.get(setImageItem, 0) == " Answer"){
                    imAnswer.setImageURI(resultUri);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imAnswer.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!textRecognizer.isOperational()) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                        StringBuilder stringBuilder = new StringBuilder();
                        //get text from string builder until is no text
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                        }
                        etAnswer.setText(stringBuilder.toString());
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, "" + exception, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}