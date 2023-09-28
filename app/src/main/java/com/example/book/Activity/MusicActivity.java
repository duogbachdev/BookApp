package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.book.Adapter.AdapterListMusic;
import com.example.book.Model.audio.AudioModel;
import com.example.book.databinding.ActivityMusicBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private ActivityMusicBinding binding;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(checkPermission() == false){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if(songsList.size()==0){
            binding.noSongsText.setVisibility(View.VISIBLE);
        }else{
            //recyclerview
            binding.recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewMusic.setAdapter(new AdapterListMusic(songsList,getApplicationContext()));
        }
        UserView();
    }

    private void UserView() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = ""+snapshot.child("email").getValue();
                String name = ""+snapshot.child("name").getValue();
                binding.toolbarEmailTv.setText(email);
                binding.toolbarNameTv.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    boolean  checkPermission () {
        int  result = ContextCompat. checkSelfPermission ( MusicActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE);
        if ( result == PackageManager. PERMISSION_GRANTED ) {
            return   true ;
        }
        else {
            return  false ;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MusicActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MusicActivity.this,"BẮT BUỘC CÓ SỰ CHO PHÉP ĐỌC, VUI LÒNG CHO PHÉP TỪ CÀI ĐẶT",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(MusicActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }

    @Override
    protected void  onResume() {
       super.onResume ();
        if(binding.recyclerViewMusic!=null){
            binding.recyclerViewMusic.setAdapter(new AdapterListMusic(songsList,getApplicationContext()));
        }
    }

}