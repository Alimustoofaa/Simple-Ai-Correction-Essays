package com.alimustofa.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class Correction extends AppCompatActivity {

   ListView simpleList;
   String[] countrylists = {"ali", "mustofa"};
   int[] flags = {R.drawable.ic_action_add, R.drawable.ic_action_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corection);
        simpleList = (ListView)findViewById(R.id.ListViewID);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_correction, R.id.resultEt, countrylists);
        simpleList.setAdapter(arrayAdapter);
    }

}