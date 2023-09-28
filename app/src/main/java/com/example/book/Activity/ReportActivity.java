package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.example.book.R;
import com.example.book.databinding.ActivityReportBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;


public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;
    private String bookCategoryId,bookId,bookTitle, bookAuthor,bookYear,bookMajor;
    private String name, email, uidUser;
    private DatabaseReference databaseReference;

    private String user;
    private CheckBox chk1, chk2, chk3, chk4, chk5, chk6, chk7;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private long date;





    private static final String TAG = "REPORT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReportBook");

        //api service


        bookCategoryId = getIntent().getStringExtra("bookCategoryId");
        bookId = getIntent().getStringExtra("bookId");
        bookTitle = getIntent().getStringExtra("bookTitle");
        bookAuthor = getIntent().getStringExtra("bookAuthor");
        bookYear = getIntent().getStringExtra("bookYear");
        bookMajor = getIntent().getStringExtra("bookMajor");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameUser = ""+snapshot.child("name").getValue();
                String emailUser = ""+snapshot.child("email").getValue();
                String uid = ""+snapshot.child("uid").getValue();
                name = nameUser;
                email = emailUser;
                uidUser = uid;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultBaoCao();
            }
        });

        //xem báo cáo
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //người dùng xem lại báo cáo
                startActivity(new Intent(ReportActivity.this, ViewReportActivity.class));

            }
        });
    }

    private void resultBaoCao() {
        String message = null;
        if(binding.chk1.isChecked()) {
            message =  binding.chk1.getText().toString();
            binding.chk1.setChecked(false);

        }
        if(binding.chk2.isChecked()) {
            if(message== null)  {
                message =  binding.chk2.getText().toString();
                binding.chk2.setChecked(false);
            } else {
                message += "," + this.chk2.getText().toString();
            }
        }
        if(binding.chk3.isChecked()) {
            if(message== null)  {
                message =  binding.chk3.getText().toString();
                binding.chk3.setChecked(false);
            } else {
                message += "," + binding.chk3.getText().toString();
            }
        }
        if(binding.chk4.isChecked()) {
            if(message== null)  {
                message =  binding.chk4.getText().toString();
                binding.chk4.setChecked(false);
            } else {
                message += "," + binding.chk4.getText().toString();
            }
        }
        if(binding.chk5.isChecked()) {
            if(message== null)  {
                message =  binding.chk5.getText().toString();
                binding.chk5.setChecked(false);
            } else {
                message += "," + binding.chk5.getText().toString();
            }
        }
        if(binding.chk6.isChecked()) {
            if(message== null)  {
                message =  binding.chk6.getText().toString();
                binding.chk6.setChecked(false);
            } else {
                message += "," + binding.chk6.getText().toString();
            }
        }
        if(binding.chk7.isChecked()) {
            if(message== null)  {
                message =  binding.chk7.getText().toString();
                binding.chk7.setChecked(false);
            } else {
                message += "," + binding.chk7.getText().toString();
            }
        }
        else {

        }

        message = message == null? "You select nothing": "" + message;
        Toast.makeText(this, "Nội dung báo cáo: "+message, Toast.LENGTH_LONG).show();

        sendReport(user,bookCategoryId,bookId,bookTitle,bookAuthor,bookYear,bookMajor,message);
//        sendNotification();
        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Report");
        ref.child(""+id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timestamp = Long.parseLong(""+snapshot.child("timestamp").getValue());
                date = timestamp;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Thông báo Báo cáo sách")
                .setContentText(""+binding.chk1)
                .setContentText(""+binding.chk2)
                .setContentText(""+binding.chk3)
                .setContentText(""+chk4)
                .setContentText(""+chk5)
                .setContentText(""+chk6)
                .setSmallIcon(R.drawable.ic_check)
                .setColor(getResources().getColor(R.color.purple_500))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null){
            notificationManager.notify(getNotificationId(),notification);
        }


    }



    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    private void sendReport(String user, String bookCategoryId, String bookId, String bookTitle,String bookAuthor,
                            String bookYear,String bookMajor,String reason) {

        progressDialog.setMessage("Báo cáo sách");
        progressDialog.show();

        // id firebase tự sinh ra và ta lấy id đó xuống và lưu lại
        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
        //thời gian
        long timestamp = System.currentTimeMillis();

        //setup upload database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+id);
        hashMap.put("uid",""+user);
        hashMap.put("categoryId",""+bookCategoryId);
        hashMap.put("bookId",""+bookId);
        hashMap.put("bookTitle",""+bookTitle);
        hashMap.put("bookAuthor",""+bookAuthor);
        hashMap.put("bookYear",""+bookYear);
        hashMap.put("bookMajor",""+bookMajor);
        hashMap.put("reason",reason);
        hashMap.put("timestamp" , timestamp);
        hashMap.put("emailUser" , email);
        hashMap.put("nameUser" , name);
        Log.d(TAG, "sendReport: Phần báo cáo gồm: "+reason+","+bookCategoryId+","
        +bookId+","+bookTitle+","+bookAuthor+","+bookMajor+","+bookYear+","+user+","+timestamp);

        //Đưa lên database.... Database Root > Categories > catogoryId > category info
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportBook");
        reference.child(""+id).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Log.d(TAG, "onSuccess: Báo cáo thành công");
                        progressDialog.dismiss();
                        Toast.makeText(ReportActivity.this, "Báo cáo thành công", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Log.d(TAG, "onFailure: Báo cáo thất bại");
                        progressDialog.dismiss();
                        Toast.makeText(ReportActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

}