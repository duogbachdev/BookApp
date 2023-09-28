package com.example.book.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Activity.ReportActivity;
import com.example.book.Activity.ShareBookActivity;
import com.example.book.Filter.FilterPdfUser;
import com.example.book.Model.book.ModelPdf;
import com.example.book.MyApplication;
import com.example.book.Activity.PdfDetailActivity;

import com.example.book.databinding.RowPdfUserBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {

    private Context context;

    public ArrayList<ModelPdf> pdfUserArrayList, filterList;

    private FilterPdfUser filter;

    private RowPdfUserBinding binding;

    private FirebaseAuth firebaseAuth;
    private String user;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfUserArrayList) {
        this.context = context;
        this.pdfUserArrayList = pdfUserArrayList;
        this.filterList = pdfUserArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        /*get data, set data*/
        ModelPdf model = pdfUserArrayList.get(position);
        String pdfId = model.getId();
        String title = model.getTitle();
        String decscription = model.getDecscription();
        String author = model.getAuthor();
        String major = model.getMajor();
        String year = model.getYear();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();

        //convert time
        String date = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.decscriptionTv.setText(decscription);
        holder.authorTv.setText(author);
        holder.dateTv.setText(date);
        holder.majorTv.setText(major);
        holder.yearTv.setText(year);

        MyApplication.loadCategory(""+categoryId, holder.categoryTv);
        //we dont need page number here, page null
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null
        );

        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv);

        //liên quan đến PdfDetailActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

        //handle click, show dialog with option 1) Edit, 2) Delete , 3) Share , 4) Read to Speech
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model, holder);
            }
        });
    }


    private void moreOptionsDialog(ModelPdf model, HolderPdfUser holder) {
        String bookId = model.getId();
        /*Những thứ ở đây chưa dùng đến
         * Và sẽ dùng trong những chức năng sau*/
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();
        String bookDecscription = model.getDecscription();
        String bookAuthor = model.getAuthor();
        String bookYear = model.getYear();
        String bookMajor = model.getMajor();
        String bookCategoryId = model.getCategoryId();

        //options to show in dialog
        String[] options = {"Chia sẻ","Báo cáo"};
        Log.d(TAG, "moreOptionsDialog: Chọn option");

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vui lòng chọn các tùy chọn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            Log.d(TAG, "onClick: Share book");
                            //Share clicked
                            Intent intent = new Intent(context, ShareBookActivity.class);
                            intent.putExtra("bookCategoryId", bookCategoryId);
                            intent.putExtra("bookTitle", bookTitle);
                            intent.putExtra("bookDecscription", bookDecscription);
                            intent.putExtra("bookAuthor", bookAuthor);
                            intent.putExtra("bookYear", bookYear);
                            intent.putExtra("bookMajor", bookMajor);
                            intent.putExtra("bookUrl", bookUrl);
                            context.startActivity(intent);
                        }

                        else {
                            //Báo cáo
                            Log.d(TAG, "onClick: Báo cáo");
                            Intent intent = new Intent(context, ReportActivity.class);
                            intent.putExtra("bookCategoryId", bookCategoryId);
                            intent.putExtra("bookId", bookId);
                            intent.putExtra("bookTitle", bookTitle);
                            intent.putExtra("bookAuthor", bookAuthor);
                            intent.putExtra("bookYear", bookYear);
                            intent.putExtra("bookMajor", bookMajor);
                            context.startActivity(intent);
                        }

                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return pdfUserArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterPdfUser(filterList, this);

        }
        return filter;
    }

    public class HolderPdfUser extends RecyclerView.ViewHolder{

        TextView titleTv,authorTv,decscriptionTv,categoryTv,sizeTv,dateTv,yearTv,majorTv;
        PDFView pdfView;
        ProgressBar progressBar;
        ImageButton moreBtn;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
            decscriptionTv = binding.decscriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            yearTv = binding.yearTv;
            majorTv = binding.majorTv;
            moreBtn = binding.moreBtn;

        }
    }
}
