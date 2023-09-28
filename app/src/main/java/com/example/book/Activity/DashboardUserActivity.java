package com.example.book.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import com.example.book.Adapter.AdapterCategory;
import com.example.book.Adapter.AdapterPdfUser;
import com.example.book.Adapter.viewpager.ViewPagerAdapter;

import com.example.book.Model.book.ModelCategory;
import com.example.book.Model.book.ModelPdf;
import com.example.book.R;
import com.example.book.databinding.ActivityDashboardUserBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    //array list
    public ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;

    public ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdfUser adapterPdfUser;


    private ViewPager viewPager;

    //view binding
    private ActivityDashboardUserBinding binding;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser; // user

    private String bookId, categoryId, category;
    BottomNavigationView bottomNavigationView; // bottom menu
    Fragment selectedFragment; // fragment

    private RelativeLayout rv1, rv2, rv3;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        checkUser();
        loadPdfUser();
        viewPager = binding.viewpager;
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        bottomNavigationView = binding.bottomNavigation; // menu bottom

        //handle click, logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
                finish();
            }
        });
        //handle click, profile
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
            }
        });
        //handle click, text_to_speech
//        binding.btnMusic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(DashboardUserActivity.this, MusicActivity.class));
//            }
//        });

        //handle click, danh mục sách
        binding.btnDanhmuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(DashboardUserActivity.this, "Chức năng đang hoàn thiện", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardUserActivity.this, CategoryUserActivity.class));
            }
        });

        //viewpager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_doc).setChecked(true);
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.nav_doc:
                        // Show a confirmation dialog when the "File đọc văn bản" item is clicked.
                        showExitConfirmationDialog();
                        return true;
                }
                return false;
            }
        });


    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có muốn thoát ứng dụng không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exit the app
                finishAffinity();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void loadPdfUser() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPdf model = ds.getValue(ModelPdf.class);

                    pdfArrayList.add(model);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //phần đăng nhập vô check coi người dùng là admin hay ko phải admin,
    // nếu ko phải admin thì vô trang người dùng thường
    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in
            binding.subTitleTv.setText("Bạn chưa đăng nhập");
        } else {
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //set in textview of toolbar
            binding.subTitleTv.setText(email);
            loadSubMajor();
        }
    }

    private void loadSubMajor() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("" + firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String major = "" + snapshot.child("major").getValue();
                binding.subMajorTv.setText(major);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}