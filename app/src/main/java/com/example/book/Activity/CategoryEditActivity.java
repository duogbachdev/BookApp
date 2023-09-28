package com.example.book.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.book.databinding.ActivityCategoryEditBinding;
import com.example.book.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryEditActivity extends AppCompatActivity {

    private ActivityCategoryEditBinding binding;

    //book id get from intent started from AdapterPdfAdmin
    private String id;
    private String category;

    //progress dialog
    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    private static final String TAG = "CATEGORY_EDIT_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getStringExtra("id");


        // startup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String category = ""+snapshot.child("category").getValue();
                binding.categoryEt.setText(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        loadBookInfo();
        //handle click, edit btn


        //handle click, back btn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, submit btn
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }



    private void validateData() {
        category = binding.categoryEt.getText().toString().trim();


        //validate data
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "Vui lòng nhập danh mục sách...", Toast.LENGTH_SHORT).show();
        }
        else {
            updateCategory();
        }
    }

    private void updateCategory() {
        Log.d(TAG, "updatePdf: Đang lưu update danh mục lên database");

        //show progress
        progressDialog.setMessage("Đang lưu các thay đổi danh mục");
        progressDialog.show();

        //setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("category",""+category);

        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("Categories");
        refBook.child(id)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Lưu các thay đổi thành công");
                        progressDialog.dismiss();
                        Toast.makeText(CategoryEditActivity.this, "Lưu các thay đổi thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Lưu các thay đổi thất bại do: "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(CategoryEditActivity.this, "Lưu các thay đổi thất bại do"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}