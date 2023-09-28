package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.book.Adapter.AdapterReport;
import com.example.book.Model.report.ModelReport;
import com.example.book.Model.user.ModelUser;
import com.example.book.databinding.ActivityViewReportBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewReportActivity extends AppCompatActivity {

    private ActivityViewReportBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    private AdapterReport adapterReport;
    public ArrayList<ModelReport> reportArrayList;
    public ArrayList<ModelUser> userArrayList;

    private String id ="";

    private String uidUser;




    private static final String TAG = "BOOK_VIEW_REPORT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // startup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();



        //xử lý phần xem báo cáo user và admin
        XulyReport();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xử lý report của admin và khóa tài khoản user lại
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = ""+snapshot.child("userType").getValue();

                        if (userType.equals("admin")){
                            ReportAdmin();
                        }
                        else {
                            Toast.makeText(ViewReportActivity.this, "Bạn không sử dụng được chức năng này", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        //edittext search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type each letter
                try {
                    adapterReport.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void ReportAdmin() {
        startActivity(new Intent(ViewReportActivity.this, AdminReportActivity.class));
    }

    private void XulyReport() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = ""+snapshot.child("uid").getValue();
                String userType = ""+snapshot.child("userType").getValue();
                uidUser = uid;
                if (userType.equals("user")){
                    loadReport("uid");
                }
                else {
                    loadReportAdmin();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //load toàn bộ báo cáo nếu là tài khoản admin
    private void loadReportAdmin() {
        //init array list before adding data info it
        reportArrayList = new ArrayList<>();

        //db path to load comment
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ReportBook");
        ref.child(""+id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear array list
                        reportArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelReport modelReport = ds.getValue(ModelReport.class);
                            reportArrayList.add(modelReport);
                        }
                        adapterReport = new AdapterReport(ViewReportActivity.this,reportArrayList);
                        binding.reportRv.setAdapter(adapterReport);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    //load báo cáo tài khoản người dùng
    private void loadReport(String uid) {
        //init array list before adding data info it
        reportArrayList = new ArrayList<>();

        //db path to load comment
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ReportBook");
        ref.orderByChild(uid).equalTo(uidUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear array list
                        reportArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelReport modelReport = ds.getValue(ModelReport.class);
                            reportArrayList.add(modelReport);
                        }
                        adapterReport = new AdapterReport(ViewReportActivity.this,reportArrayList);
                        binding.reportRv.setAdapter(adapterReport);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}