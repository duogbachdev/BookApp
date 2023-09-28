package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class PdfEditActivity extends AppCompatActivity {

    private ActivityPdfEditBinding binding;

    //book id get from intent started from AdapterPdfAdmin
    private String bookId;

    //progress dialog
    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    private static final String TAG = "BOOK_EDIT_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookId = getIntent().getStringExtra("bookId");

        // startup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadBookInfo();
        //handle click, edit btn
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

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



    private void loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Đang tải thông tin sách");
        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("Books");
        refBook.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String decscription = ""+snapshot.child("decscription").getValue();
                        String title = ""+snapshot.child("title").getValue();
                        String author = ""+snapshot.child("author").getValue();
                        String year = ""+snapshot.child("year").getValue();
                        String major = ""+snapshot.child("major").getValue();

                        //set to views
                        binding.titleEt.setText(title);
                        binding.decscriptionEt.setText(decscription);
                        binding.authorEt.setText(author);
                        binding.yearEt.setText(year);
                        binding.majorEt.setText(major);

                        Log.d(TAG, "onDataChange: Đang tải thông tin danh mục sách");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                        reference.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = ""+snapshot.child("category").getValue();
                                        //set category text view
                                        binding.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title="";
    private String decscription="";
    private String author="";
    private String year="";
    private String major="";
    private void validateData() {
        title = binding.titleEt.getText().toString().trim();
        decscription = binding.decscriptionEt.getText().toString().trim();
        author = binding.authorEt.getText().toString().trim();
        year = binding.yearEt.getText().toString().trim();
        major = binding.majorEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Vui lòng nhập tiêu đề sách...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(decscription)){
            Toast.makeText(this, "Vui lòng nhập mô tả sách...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(major)){
            Toast.makeText(this, "Vui lòng nhập chuyên ngành lĩnh vực", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(year)){
            Toast.makeText(this, "Vui lòng nhập năm xuất bản", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(author)){
            Toast.makeText(this, "Vui lòng nhập tác giả", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryId)){
            Toast.makeText(this, "Vui lòng chọn danh mục sách", Toast.LENGTH_SHORT).show();
        }
        else {
            updatePdf();
        }
    }

    private void updatePdf() {
        Log.d(TAG, "updatePdf: Đang lưu update pdf lên database");

        //show progress
        progressDialog.setMessage("Đang lưu các thay đổi sách");
        progressDialog.show();

        //setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("decscription",""+decscription);
        hashMap.put("author",""+author);
        hashMap.put("year",""+year);
        hashMap.put("major",""+major);
        hashMap.put("categoryId",""+selectedCategoryId);

        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("Books");
        refBook.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Lưu các thay đổi thành công");
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Lưu các thay đổi thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Lưu các thay đổi thất bại"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Lưu các thay đổi thất bại do"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String selectedCategoryId="", selectedCategoryTitle="";

    private void categoryDialog(){
        //tạo mảng chuỗi từ danh sách mảng của chuỗi category
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i=0; i<categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryId = categoryIdArrayList.get(which);
                        selectedCategoryTitle = categoryTitleArrayList.get(which);

                        binding.categoryTv.setText(selectedCategoryTitle);
                    }
                })
                .show();

    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Đang tải danh mục sách");

        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID: "+id);
                    Log.d(TAG, "onDataChange: Category: "+category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}