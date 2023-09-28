package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.book.Adapter.AdapterReport;
import com.example.book.Model.book.ModelCategory;
import com.example.book.Model.report.ModelReport;
import com.example.book.databinding.ActivityCategoryAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class CategoryAddActivity extends AppCompatActivity {

    //view binding
    private ActivityCategoryAddBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


//    private ArrayList<ModelCategory> categoryArrayList;


    private static final String TAG = "CATEGORY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


    }


    private String categorys = "";
    private String id;

    //status
//     private String status1 = "0";
//     int status = Integer.parseInt(status1);

    private void validateData() {

        //get data
        categorys = binding.edtCategory.getText().toString().trim();
        if (TextUtils.isEmpty(categorys)) {
            Toast.makeText(this, "Vui lòng nhập danh mục sách...", Toast.LENGTH_SHORT).show();
        }
        else {
            //Xử lý danh sách trùng nhau
            DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Categories");
            categoryRef.orderByChild("category").equalTo(categorys).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //category already exists
                        Toast.makeText(CategoryAddActivity.this, "Danh mục sách đã tồn tại.", Toast.LENGTH_SHORT).show();
                    } else{
                        //category does not exist, add to Firebase
                        addCategoryFirebase();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //error handling
                    Toast.makeText(CategoryAddActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
//            addCategoryFirebase();



    private void addCategoryFirebase() {

        progressDialog.setMessage("Thêm danh mục");
        progressDialog.show();


        // id firebase tự sinh ra và ta lấy id đó xuống và lưu lại
        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
        //thời gian
        long timestamp = System.currentTimeMillis();

        //setup upload database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + id);
        hashMap.put("category", ""+categorys);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child("" + id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //success
                        Log.d(TAG, "onSuccess: Upload thành công: " + categorys);
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "Danh mục: " + categorys + " upload thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CategoryAddActivity.this, DashboardAdminActivity.class));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

}