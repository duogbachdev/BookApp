package com.example.book.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.book.Adapter.AdapterComment;
import com.example.book.Model.book.ModelComment;
import com.example.book.MyApplication;

import com.example.book.databinding.ActivityPdfDetailBinding;
import com.example.book.R;
import com.example.book.databinding.DialogAddCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfDetailActivity extends AppCompatActivity {

    //view binding
    private ActivityPdfDetailBinding binding;

    private String bookId, bookTitle, bookUrl;

    boolean isInMyFavorite = false;
    private FirebaseAuth firebaseAuth;

    private static  final  String TAG_DOWNLOAD ="DOWNLOAD_TAG";

    //progress dialog
    private ProgressDialog progressDialog;
    private AdapterComment adapterComment;

    private ArrayList<ModelComment> commentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent e.g. bookId
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        //lúc bắt đầu ẩn nút tải xuống, vì chúng tôi cần url sách mà chúng tôi sẽ tải sau này trong loadBookDetails();
        binding.downloadBookBtn.setVisibility(View.GONE);

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //nếu đã đăng nhập thì cho thêm sách vào mục yêu thích
        if (firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }
        
        loadBookDetails(); // load book detail
        loadComments(); // load comment

        //increment book view count, whenever this page starts
        MyApplication.incrementBookViewCount(bookId);

        //back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //read book
        binding.readBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PdfDetailActivity.this,PdfViewActivity.class);//tạo hoạt động đọc sách
                intent1.putExtra("bookId", bookId);
                startActivity(intent1);
            }
        });

        //download book
        //Có liên quan đến MyApplication
        binding.downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_DOWNLOAD, "onClick: Đã kiểm tra quyền");
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG_DOWNLOAD, "onClick: Sự cho phép đã được cấp, có thể tải sách");
                    MyApplication.downloadBook(PdfDetailActivity.this, ""+bookId,""+bookTitle,""+bookUrl);
                }
                else {
                    Log.d(TAG_DOWNLOAD, "onClick: Chưa cấp quyền, vui lòng liên hệ admin để được cấp quyền");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        //handle click, add/remove favorite
        //Có liên quan đến MyApplication
        binding.favoriteBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isInMyFavorite){
                        //remove favorite
                        MyApplication.removeFavorite(PdfDetailActivity.this,bookId);
                    }
                    else {
                        //add favorite
                        MyApplication.addFavorite(PdfDetailActivity.this,bookId);
                    }
                }
            }
        });

        //handle click, comment
        binding.addBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Requirement: User must be logged in to add comment*/
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                }
                else {
                    addComment();
                }
            }
        });
        

    }

    private void loadComments() {
        //init array list before adding data info it
        commentArrayList = new ArrayList<>();

        //db path to load comment
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear array list
                        commentArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelComment modelComment = ds.getValue(ModelComment.class);
                            commentArrayList.add(modelComment);
                        }
                        adapterComment = new AdapterComment(PdfDetailActivity.this,commentArrayList);
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //phần comment
    private String comment ="";
    private void addComment() {
        DialogAddCommentBinding addCommentBinding = DialogAddCommentBinding.inflate(LayoutInflater.from(this));

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(addCommentBinding.getRoot());

        //create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //handle click, dismis dialog
        addCommentBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //handle click, add comment
        addCommentBinding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data
                comment = addCommentBinding.commentEt.getText().toString().trim();
                //validate data
                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(PdfDetailActivity.this, "Vui lòng nhập bình luận của bạn", Toast.LENGTH_SHORT).show();

                }
                else {
                    alertDialog.dismiss();
                    addCommentMen();
                }
            }
        });
    }

    private void addCommentMen() {
        //show progress
        progressDialog.setMessage("Thêm bình luận của bạn...");
        progressDialog.show();

        //timestamp for comment id, comment title
        String timestamp = ""+System.currentTimeMillis();

        //set data to add in db for comment
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("bookId", ""+bookId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("comment", ""+comment);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        //Database data to add in db for comment
        //Books->bookId->Comments-> commentId->commentData
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfDetailActivity.this, "Bình luận của bạn thành công", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to add comment
                        progressDialog.dismiss();
                        Toast.makeText(PdfDetailActivity.this, "Lỗi do "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //request storage permission
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult
            (new ActivityResultContracts.RequestPermission(), isGranted ->{
                if (isGranted){
                    Log.d(TAG_DOWNLOAD, "Đã được cho phép");
                    MyApplication.downloadBook(this, ""+bookId, ""+bookTitle, ""+bookUrl);
                    Toast.makeText(this, "Đã được cho phép", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG_DOWNLOAD, "Không cho phép");
                    Toast.makeText(this, "Không cho phép", Toast.LENGTH_SHORT).show();
                }
            });

    private void loadBookDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle = ""+snapshot.child("title").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String decscription = ""+snapshot.child("decscription").getValue();
                        String author = ""+snapshot.child("author").getValue();
                        String year = ""+snapshot.child("year").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String major = ""+snapshot.child("major").getValue();
                        bookUrl = ""+snapshot.child("url").getValue();
                        String uid = ""+snapshot.child("uid").getValue();

                        //required data loaded, show download button
                        binding.downloadBookBtn.setVisibility(View.VISIBLE);

                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );

                        MyApplication.loadPdfFromUrlSinglePage(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.pdfView,
                                binding.progressBar,
                                binding.pagesTv
                        );

                        MyApplication.loadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.sizeTv
                        );

                        //set data
                        binding.titleTv.setText(bookTitle);
                        binding.decscriptionTv.setText(decscription);
                        binding.authorTv.setText(author);
                        binding.yearsTv.setText(year);
                        binding.dateTv.setText(date);
                        binding.majorsTv.setText(major);
                        binding.viewTv.setText(viewsCount.replace("null","N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null","N/A"));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private  void checkIsFavorite(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        //true and false
                        if (isInMyFavorite){
                            binding.favoriteBookBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white,0,0);
                            binding.favoriteBookBtn.setText("Xóa");
                        }
                        else {
                            binding.favoriteBookBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_border_24,0,0);
                            binding.favoriteBookBtn.setText("Yêu thích");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}