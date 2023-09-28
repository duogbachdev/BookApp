package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.book.Adapter.AdapterAdminReport;
import com.example.book.Model.report.ModelReport;
import com.example.book.Model.user.ModelUser;
import com.example.book.databinding.ActivityAdminReportBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminReportActivity extends AppCompatActivity {

    private ActivityAdminReportBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    private AdapterAdminReport adapterAdminReport;
    public ArrayList<ModelReport> reportArrayList;
    public ArrayList<ModelUser> userArrayList;

    private String id ="";

    private String uidUser;




    private static final String TAG = "BOOK_VIEW_REPORT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminReportBinding.inflate(getLayoutInflater());
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
                Log.d(TAG, "onClick: Quay về");
                onBackPressed();
            }
        });

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
                    loadReport("uid"); // load báo cáo của người dùng
                    Log.d(TAG, "onDataChange: Load báo cáo");
                }
                else {
                    loadReportAdmin(); // load báo cáo của admin
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadReportAdmin() {
        //init array list before adding data info it
        reportArrayList = new ArrayList<>();
        Log.d(TAG, "loadReportAdmin: Load báo cáo thành công");

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
                        adapterAdminReport = new AdapterAdminReport(AdminReportActivity.this,reportArrayList);
                        binding.reportRv.setAdapter(adapterAdminReport);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadReport(String uid) {
        //init array list before adding data info it
        reportArrayList = new ArrayList<>();
        Log.d(TAG, "loadReport: Load báo cáo thành công");

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
                        adapterAdminReport = new AdapterAdminReport(AdminReportActivity.this,reportArrayList);
                        binding.reportRv.setAdapter(adapterAdminReport);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}