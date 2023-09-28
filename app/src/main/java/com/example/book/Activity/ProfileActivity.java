package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.book.Adapter.AdapterPdfFavorite;
import com.example.book.Model.book.ModelPdf;
import com.example.book.Model.user.ModelUser;
import com.example.book.MyApplication;
import com.example.book.R;
import com.example.book.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;

    //firebase auth and firebase user
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //array list
    private ArrayList<ModelPdf> pdfArrayList;

    //adapter favorite
    private AdapterPdfFavorite adapterPdfFavorite;

    //progress dialog
    private ProgressDialog progressDialog;


    private static final String TAG = "PROFILE_TAG";
    private String userType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //reset data of user info
        binding.accountTypeTv.setText("N/A");
        binding.memberDateTv.setText("N/A");
        binding.favoriteBookCountTv.setText("N/A");
        binding.accountStatusTv.setText("N/A");

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //setup firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        loadUserInfo();
        loadBookFavorite(); // load book favorite
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        //get all info of user here from snapshot
                        String uid = "" + snapshot.child("uid").getValue();
                        String userType = "" + snapshot.child("userType").getValue();

                        /*set image, using picasso
                         *nếu bạn là admin thì sẽ load hình profile admin
                         * và ngược lại nếu bạn là người dùng thì load hình profile người dùng
                         * và cuối cùng nếu bạn thay đổi ảnh profile của cả hai thì nó sẽ load hình thay đổi đó */

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //handle click, button back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click, button edit profile
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ProfileEditActivity.class));
            }
        });

        //handle click, very user status
        binding.accountStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.isEmailVerified()){
                    //already verified
                    Toast.makeText(ProfileActivity.this, "Đã được xác minh tài khoản", Toast.LENGTH_SHORT).show();

                }else {
                    emailVerificationDialog();
                }
            }
        });

        //handle click, gọi điện

    }

    private void emailVerificationDialog() {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác thực email")
                .setMessage("Bạn có chắc chắn muốn gửi hướng dẫn xác minh email đến email của mình không "+firebaseUser.getEmail())
                .setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmailVerification();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void sendEmailVerification() {
        //show progress
        progressDialog.setMessage("Gửi hướng dẫn xác minh email đến email của bạn "+firebaseUser.getEmail());
        progressDialog.show();

        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Đã gửi, vui lòng check email"+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Lỗi do "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Đang tải thông tin người dùng của người dùng");

        //nhận trạng thái xác minh email, sau khi xác minh, bạn phải đăng nhập để thay đổi
        if (firebaseUser.isEmailVerified()){
            binding.accountStatusTv.setText("Đã xác minh");
        }else {
            binding.accountStatusTv.setText("Chưa xác minh");
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        //get all info of user here from snapshot
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String password = "" + snapshot.child("password").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String profileimage = "" + snapshot.child("profileimage").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String userType = "" + snapshot.child("userType").getValue();

                        //format date
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        /*set image, using picasso
                        *nếu bạn là admin thì sẽ load hình profile admin
                        * và ngược lại nếu bạn là người dùng thì load hình profile người dùng
                        * và cuối cùng nếu bạn thay đổi ảnh profile của cả hai thì nó sẽ load hình thay đổi đó */

                        if (profileimage.isEmpty() && userType.equals("admin")){
                            binding.profileTv.setImageResource(R.drawable.admin);

                        }
                        else if (profileimage.isEmpty() && userType.equals("user")){
                            binding.profileTv.setImageResource(R.drawable.user);
                        }
                        else {
                            Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.ic_person_gray).into(binding.profileTv);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void loadBookFavorite() {
        //init list
        pdfArrayList = new ArrayList<>();

        //load favorite book
        //Users -> userId -> Favorites
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //we will only get the bookId here, and we got other details in adapter using that bookId
                            String bookId = ""+ds.child("bookId").getValue();

                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            //add model vào list
                            pdfArrayList.add(modelPdf);
                        }

                        //set number of favorites book
                        binding.favoriteBookCountTv.setText(""+pdfArrayList.size());
                        //set adapter
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this,pdfArrayList);
                        //set adapter to recycle view
                        binding.bookRv.setAdapter(adapterPdfFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}