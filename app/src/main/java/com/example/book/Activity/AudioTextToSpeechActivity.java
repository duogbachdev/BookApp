package com.example.book.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.example.book.databinding.ActivityAudioTextToSpeechBinding;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class AudioTextToSpeechActivity extends AppCompatActivity {

    private ActivityAudioTextToSpeechBinding binding;

    public TextToSpeech textToSpeech;

    private static final String TAG = "SPEECH";

//    private String envPath = Environment.getExternalStorageDirectory()
//            .getAbsolutePath() + "/Download/AppBook";

    private Uri fileUri;
    
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityAudioTextToSpeechBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            //setup firebase auth
            Log.d(TAG, "onCreate: Đang chạy dự án");

//            bookUrl = getIntent().getStringExtra("bookUrl");



            binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Đã thoát app");
                onBackPressed();
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR){
                        Log.d(TAG, "onInit: Đang đọc dữ liệu");
                        textToSpeech.setLanguage(Locale.UK);

                    }
                    else {
                        Log.d(TAG, "onInit: Lỗi ko đọc được");
                        Toast.makeText(AudioTextToSpeechActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            binding.btnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toSpeak = binding.textEt.getText().toString().trim();
                    if (toSpeak.equals("")){
                        Log.d(TAG, "onClick: Vui lòng nhập văn bản vào trước khi nói");
                        Toast.makeText(AudioTextToSpeechActivity.this, "Vui lòng nhập văn bản...", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Log.d(TAG, "onClick: Đang chuyển văn bản thành giọng nói");
                        Toast.makeText(AudioTextToSpeechActivity.this, toSpeak, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            });

            binding.btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textToSpeech.isSpeaking()){
                        textToSpeech.stop();
                        //textToSpeech.shutdown();
                    }
                    else {
                        Toast.makeText(AudioTextToSpeechActivity.this, "Không nói", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.btnMP3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toSpeak = binding.textEt.getText().toString().trim();
//                    textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH, null);

                    HashMap<String, String> myHashRender = new HashMap<>();
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, toSpeak);
                    Log.d(TAG, "Đang tạo file âm thanh, vui lòng đợi");
                    //UUID phân loại file ko trùng nhau.
                    final String music = "appbook_" + UUID.randomUUID().toString() + ".mp3";
                    File dir = new File(Environment.getExternalStorageDirectory() + "/Download/Appbook/");
                    dir.mkdirs();
                    String destFileName = dir + "/" +music;
                    Log.d(TAG, "onClick: File âm thanh đã tạo"+destFileName);

//                    int sr = textToSpeech.synthesizeToFile(toSpeak, myHashRender, destFileName);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.d(TAG, "onClick: Đọc");
                        textToSpeech.synthesizeToFile(toSpeak, null, new File(destFileName), music);
                        Toast.makeText(AudioTextToSpeechActivity.this, "File âm thanh đã lưu tại bộ nhớ điện thoại thư mục Download/AppBook", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Bundle params = new Bundle();
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, music);
                        textToSpeech.synthesizeToFile(toSpeak, myHashRender, destFileName);

                        textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                            @Override
                            public void onUtteranceCompleted(String s) {
                                if (s.equals(music)) {
                                    // start playing the audio file defined at myTestingId.wav
                                    Log.d(TAG, "onUtteranceCompleted: Thành công"+music);
                                    Toast.makeText(AudioTextToSpeechActivity.this, "Tệp âm thanh đã lưu thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
            });
    }


    @Override
        protected void onPause() {
            if (textToSpeech != null || textToSpeech.isSpeaking()){
                textToSpeech.stop();
                //textToSpeech.shutdown();
            }
            super.onPause();
        }
}