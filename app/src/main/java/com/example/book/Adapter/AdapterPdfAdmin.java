package com.example.book.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import com.example.book.Activity.AdminReportActivity;
import com.example.book.Filter.FilterPdfAdmin;
import com.example.book.Model.book.ModelPdf;
import com.example.book.MyApplication;
import com.example.book.Activity.PdfDetailActivity;
import com.example.book.Activity.PdfEditActivity;
import com.example.book.Activity.ShareBookActivity;
import com.example.book.databinding.RowPdfAdminBinding;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {
    private Context context;
    //arraylist
    public ArrayList<ModelPdf> pdfArrayList,filterList;

    //binding
    private RowPdfAdminBinding binding;
    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    //progress
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        //get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String decscription = model.getDecscription();
        String pdfUrl = model.getUrl();
        String major = model.getMajor();
        String author = model.getAuthor();
        String year = model.getYear();
        long timestamp = model.getTimestamp();

        //timestamp dd/MM/yyyy
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.decscriptionTv.setText(decscription);
        holder.authorTV.setText(author);
        holder.dateTv.setText(formattedDate);
        holder.yearTv.setText(year);
        holder.majorTv.setText(major);

        /*load further details like category, pdf from url, pdf size in seprate function
        * loadCategory, loadPdfFromSinglePage, loadPdfSize đều có liên quan đến lớp class MyApplication
        * Khi sửa 1 bên nào phải sửa bên kia cùng lúc*/

        MyApplication.loadCategory(""+categoryId,
                holder.categoryTv);

        //we dont need page number here, page null
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null);
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv);

        //handle click, show dialog with option 1) Edit, 2) Delete , 3) Share , 4) Read to Speech
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model, holder);
            }
        });

        //handle click, detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Chuyển qua xem sách");
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", pdfId);
                context.startActivity(intent);
            }
        });


    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {

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
        String[] options = {"Chỉnh sửa","Xóa","Chia sẻ", "Quản lý Báo cáo"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vui lòng chọn các tùy chọn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            //Edit clicked, Open PdfEditActivity to edit the book info
                            Log.d(TAG, "onClick: Chỉnh sửa");
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);
                        }
                        else if (which==1){
                            //Delete clicked
                            Log.d(TAG, "onClick: Xóa");
                            MyApplication.deleteBook(
                                    context,
                                    ""+bookId,
                                    ""+bookUrl,
                                    ""+bookTitle
                            );
                        }
                        else if (which==2){
                            //Share clicked
                            Log.d(TAG, "onClick: Chia sẻ");
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
                            Intent intent = new Intent(context, AdminReportActivity.class);
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
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }

    public class HolderPdfAdmin extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,decscriptionTv,categoryTv,sizeTv,dateTv, authorTV,yearTv,majorTv;
        ImageButton moreBtn;
        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            authorTV = binding.authorTv;
            decscriptionTv = binding.decscriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;
            yearTv = binding.yearTv;
            majorTv = binding.majorTv;

        }
    }
}
