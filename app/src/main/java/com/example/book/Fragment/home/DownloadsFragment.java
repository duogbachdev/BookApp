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
import com.example.book.databinding.FragmentDownloadsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DownloadsFragment extends Fragment {

    private FragmentDownloadsBinding binding;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private ArrayList<ModelPdf> pdfArrayList;
    public AdapterPdfUser adapterPdfUser;

    public DownloadsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDownloadsBinding.inflate(LayoutInflater.from(getContext()),container, false);

        //auth
        mAuth = FirebaseAuth.getInstance();
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
        //recycleview
        recyclerView = binding.booksRv ;
        pdfArrayList = new ArrayList<>();
        adapterPdfUser = new AdapterPdfUser(getContext(),pdfArrayList);
        recyclerView.setAdapter(adapterPdfUser);


        loadPdfUserDownloads("downloadsCount");



        return binding.getRoot();
    }

    private void loadPdfUserDownloads(String downloadsCount) {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(downloadsCount).limitToLast(10) // load 10 lượt xem sách hoặc nhiều hơn
                .addValueEventListener(new ValueEventListener() {
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