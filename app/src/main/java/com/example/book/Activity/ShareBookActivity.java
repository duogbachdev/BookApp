package com.example.book.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.book.Adapter.AdapterPdfAdmin;
import com.example.book.databinding.ActivityShareBookBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;

public class ShareBookActivity extends AppCompatActivity {
    //view binding
    private ActivityShareBookBinding binding;

    private AdapterPdfAdmin adapterPdfAdmin;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    private String bookCategoryId,bookTitle, bookDecscription, bookAuthor, bookUrl,bookYear, bookMajor;

    //arraylist pdf categories


    //uri of picked
    private Uri pdfUri = null;


    //tag for debugging
    private static final String TAG = "ADD_PDF_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        bookCategoryId = getIntent().getStringExtra("bookCategoryId");
        bookTitle = getIntent().getStringExtra("bookTitle");
        bookDecscription = getIntent().getStringExtra("bookDecscription");
        bookAuthor = getIntent().getStringExtra("bookAuthor");
        bookYear = getIntent().getStringExtra("bookYear");
        bookMajor = getIntent().getStringExtra("bookMajor");
        bookUrl = getIntent().getStringExtra("bookUrl");

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.titleTv.setText(bookTitle);
        binding.decscriptionTv.setText(bookDecscription);
        binding.categoryTv.setText(bookCategoryId);
        binding.authorTv.setText(bookAuthor);
        binding.yearTv.setText(bookYear);
        binding.majorTv.setText(bookMajor);
        binding.pdfTv.setText(bookUrl);



        //handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextOnly();
                sharePDFAndText();
            }
        });

//        loadBookInfo();



    }

    private void shareTextOnly() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share PDF");
        intent.putExtra(Intent.EXTRA_TEXT,""+bookCategoryId);
        intent.putExtra(Intent.EXTRA_TEXT,""+bookTitle);
        intent.putExtra(Intent.EXTRA_TEXT,""+bookDecscription);
        intent.putExtra(Intent.EXTRA_TEXT,""+bookAuthor);
        intent.putExtra(Intent.EXTRA_TEXT,""+bookUrl);
        startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }


    private Uri sharePDFAndText() {

        File imageFolder = new File(this.getCacheDir(),"pdf/");
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, ""+bookUrl);

            FileOutputStream stream = new FileOutputStream(file);
            stream.flush();
            stream.close();
            pdfUri = FileProvider.getUriForFile(ShareBookActivity.this,"com.example.book.fileprovider",file);
        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return pdfUri;
    }
}