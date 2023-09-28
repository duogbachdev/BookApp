package com.example.book.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Activity.PdfListAdminActivity;
import com.example.book.Filter.FilterCategoryUsers;
import com.example.book.Model.book.ModelCategory;
import com.example.book.databinding.RowCategoryUsersBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategoryUsers extends RecyclerView.Adapter<AdapterCategoryUsers.HolderCategory> implements Filterable {

    private Context context;
    public ArrayList<ModelCategory> categoryArrayList, filterListUser;

    //view binding
    private RowCategoryUsersBinding binding;

    //instance of our filtter class
    private FilterCategoryUsers filter;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "ADAPTERCATEGORY";

    public AdapterCategoryUsers(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterListUser = categoryArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowCategoryUsersBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategoryUsers.HolderCategory holder, int position) {
        //get data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        long timestamp = model.getTimestamp();
        String uid = model.getUid();
//        int status = model.getStatus();


        //set data
        holder.categoryTv.setText(category);


        //handle item click, goto PdfListAdminActivity, also pass pdf category and categoryId
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, PdfListAdminActivity.class);
//                intent.putExtra("categoryId", id);
//                intent.putExtra("categoryTitle", category);
//                context.startActivity(intent);
//            }
//        });



    }

    private void deleteCategory(ModelCategory model, HolderCategory holder) {
        //trạng thái category
//        int status = model.getStatus();
        //get id of category to delete
        String id = model.getId();
        //Firebase DB > Categories > categoryId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //cập nhật lại trạng thái status

                        //delete successfully
                        Toast.makeText(context, "Xóa thành công...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterCategoryUsers(filterListUser,this);
        }
        return filter;
    }

    //View holder class to hold UI views for row_category.xml
    public class HolderCategory extends RecyclerView.ViewHolder{

        //ui view of row_category.xml
        TextView categoryTv;
        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            categoryTv = (TextView) binding.categoryTv;
        }
    }
}
