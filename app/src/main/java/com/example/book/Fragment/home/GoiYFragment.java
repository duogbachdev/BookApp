package com.example.book.Fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.book.Adapter.AdapterPdfUser;
import com.example.book.Model.book.ModelPdf;
import com.example.book.databinding.FragmentGoiYBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GoiYFragment extends Fragment {

    private FragmentGoiYBinding binding;
    private FirebaseAuth mAuth;
    private String user;
    private String  majorUser;
    private AdapterPdfUser adapterPdfUser;
    private RecyclerView recyclerView;
    private ArrayList<ModelPdf> pdfArrayList;
    private String idBook;


    public GoiYFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGoiYBinding.inflate(LayoutInflater.from(getContext()),container, false);

        //auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getUid();
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type each letter
                try {
                    adapterPdfUser.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = ""+snapshot.child("id").getValue();
                idBook = id;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //recycleview
        recyclerView = binding.booksRv ;
        pdfArrayList = new ArrayList<>();
        adapterPdfUser = new AdapterPdfUser(getContext(),pdfArrayList);
        recyclerView.setAdapter(adapterPdfUser);

        if (user == null){
            loadGoiY1();
        }
        else {
            XuLyGoiY();
        }

        return binding.getRoot();
    }

    private void XuLyGoiY() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = ""+snapshot.child("userType").getValue();
                String major = ""+snapshot.child("major").getValue();

                majorUser = major;

                if (userType.equals("user")){
                    loadGoiY("major");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGoiY1() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Books");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    pdfArrayList.add(model);
                }
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGoiY(String major) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild(major).equalTo(majorUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);

                    pdfArrayList.add(model);
                }
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}