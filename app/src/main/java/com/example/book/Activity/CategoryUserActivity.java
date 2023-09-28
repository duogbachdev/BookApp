package com.example.book.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.book.Adapter.AdapterCategoryUsers;
import com.example.book.Adapter.AdapterPdfUser;
import com.example.book.Model.book.ModelCategory;
import com.example.book.Model.book.ModelPdf;
import com.example.book.databinding.ActivityCategoryUsersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryUserActivity extends AppCompatActivity {

    //array list
    public ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategoryUsers adapterCategoryUsers;

    public ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdfUser adapterPdfUser;


    private ViewPager viewPager;

    //view binding
    private ActivityCategoryUsersBinding binding;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser; // user

    private String bookId,categoryId,category;
    BottomNavigationView bottomNavigationView; // bottom menu
    Fragment selectedFragment; // fragment

    private RelativeLayout rv1,rv2,rv3;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loadCategories();

        //back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                    adapterCategoryUsers.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadCategories() {
        //arraylist
        categoryArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data info it
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add to arraylist
                    categoryArrayList.add(model);
                }
                //setup adapter
                adapterCategoryUsers = new AdapterCategoryUsers(CategoryUserActivity.this, categoryArrayList);
                //set adapter to recycleview
                binding.categoriesRv.setAdapter(adapterCategoryUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}