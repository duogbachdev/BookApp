package com.example.book.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Filter.FilterReport;
import com.example.book.Model.report.ModelReport;
import com.example.book.Model.user.ModelUser;
import com.example.book.MyApplication;
import com.example.book.databinding.RowBookReportBinding;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterReport extends RecyclerView.Adapter<AdapterReport.HolderReport> implements Filterable {
    private Context context;

    public ArrayList<ModelReport> reportArrayList, filterList;
    public ArrayList<ModelUser> userArrayList;

    private FilterReport filter;

    private RowBookReportBinding binding;

    private FirebaseAuth firebaseAuth;

    private String id; // id người dùng


    private static final String TAG = "ADAPTER_REPORT_TAG";
    public AdapterReport(Context context, ArrayList<ModelReport> reportArrayList) {
        this.context = context;
        this.reportArrayList = reportArrayList;
        this.filterList = reportArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public AdapterReport.HolderReport onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowBookReportBinding.inflate(LayoutInflater.from(context),parent,false);

        return new AdapterReport.HolderReport(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(AdapterReport.HolderReport holder, int position) {
        ModelReport model = reportArrayList.get(position);
        String reportId = model.getId();
        String bookId = model.getBookId();
        String bookTitle = model.getBookTitle();
        String bookAuthor = model.getBookAuthor();
        String bookYear = model.getBookYear();
        String bookMajor = model.getBookMajor();
        String categoryId = model.getCategoryId();
        String reason = model.getReason();
        String uid = model.getUid();
        String nameUser = model.getNameUser();
        String emailUser = model.getEmailUser();

        Log.d(TAG, "onBindViewHolder: Báo cáo sách đã load thành công");

        long timestamp = model.getTimestamp();

        //convert time
        String date = MyApplication.formatTimestamp(timestamp);



        //set date
        holder.reasonTv.setText(reason);
        holder.emailTv.setText(emailUser);
        holder.bookTitle.setText(bookTitle);
        holder.bookYear.setText(bookYear);
        holder.bookMajor.setText(bookMajor);
        holder.bookAuthor.setText(bookAuthor);
        holder.timestampTv.setText(date);
        holder.nameTv.setText(nameUser);

    }


    @Override
    public int getItemCount() {
        return reportArrayList.size();
    }
    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterReport(filterList, this);

        }
        return filter;
    }

    public class HolderReport extends RecyclerView.ViewHolder{
        TextView nameTv,emailTv,reasonTv,categoryTv,timestampTv, bookTitle, bookMajor, bookAuthor, bookYear;

        public HolderReport(@NonNull View itemView) {
            super(itemView);
            nameTv = binding.nameTv;
            reasonTv = binding.reasonTv;
            emailTv = binding.emailTv;
            timestampTv = binding.timestampTv;
            bookTitle = binding.bookTitleTv;
            bookAuthor = binding.bookAuthorTv;
            bookMajor = binding.bookMajorTv;
            bookYear = binding.bookYearTv;

        }
    }
}
