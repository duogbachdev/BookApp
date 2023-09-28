package com.example.book.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Filter.FilterAdminReport;
import com.example.book.Model.report.ModelReport;
import com.example.book.Model.user.ModelUser;
import com.example.book.MyApplication;
import com.example.book.databinding.RowAdminReportBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterAdminReport extends RecyclerView.Adapter<AdapterAdminReport.HolderAdminReport> implements Filterable {
    private Context context;

    public ArrayList<ModelReport> reportArrayList, filterAdminList;
    public ArrayList<ModelUser> userArrayList;

    private FilterAdminReport filter;

    private RowAdminReportBinding binding;

    private FirebaseAuth firebaseAuth;

    private String id; // id người dùng


    private static final String TAG = "ADAPTER_REPORT_TAG";
    public AdapterAdminReport(Context context, ArrayList<ModelReport> reportArrayList) {
        this.context = context;
        this.reportArrayList = reportArrayList;
        this.filterAdminList = reportArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public AdapterAdminReport.HolderAdminReport onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowAdminReportBinding.inflate(LayoutInflater.from(context),parent,false);

        return new AdapterAdminReport.HolderAdminReport(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(AdapterAdminReport.HolderAdminReport holder, int position) {
        ModelReport model = reportArrayList.get(position);
        String reportId = model.getId();
        String bookId = model.getBookId();
        String bookTitle = model.getBookTitle();
        String bookAuthor = model.getBookAuthor();
        String bookYear = model.getBookYear();
        String bookMajor = model.getBookMajor();
        String categoryId = model.getCategoryId();
        String reason = model.getReason();
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
        holder.timestampTv.setText(date);
        holder.nameTv.setText(nameUser);
        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });




    }

    private void moreOptionsDialog(ModelReport model, HolderAdminReport holder) {
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
        //options to show in dialog
        String[] options = {"Xóa báo cáo","Khóa tài khoản","Ẩn sách"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vui lòng chọn các tùy chọn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            //Xóa
                            Log.d(TAG, "onClick: Xóa");
                            MyApplication.deleteReportBook(
                                    context,
                                    ""+reportId,
                                    ""+nameUser,
                                    ""+emailUser,
                                    ""+bookTitle,
                                    ""+reason
                            );

                        }
                        else if (which==1){
                            //Khóa tài khoản
                            Log.d(TAG, "onClick: Khóa tài khoản");
                            Toast.makeText(context, "Chức năng đang phát triển và hoàn thiện", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            //Ẩn sách
                            Log.d(TAG, "onClick: Ẩn sách");
                            Toast.makeText(context, "Chức năng đang phát triển và hoàn thiện", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return reportArrayList.size();
    }
    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterAdminReport(filterAdminList, this);

        }
        return filter;
    }

    public class HolderAdminReport extends RecyclerView.ViewHolder{
        TextView nameTv,emailTv,reasonTv,timestampTv, bookTitle;
        ImageButton moreBtn, btnLock;


        public HolderAdminReport(@NonNull View itemView) {
            super(itemView);
            nameTv = binding.nameTv;
            reasonTv = binding.reasonTv;
            emailTv = binding.emailTv;
            timestampTv = binding.timestampTv;
            bookTitle = binding.bookTitleTv;
            moreBtn = binding.moreBtn;



        }
    }
}
