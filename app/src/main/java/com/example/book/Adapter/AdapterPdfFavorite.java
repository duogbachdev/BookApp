package com.example.book.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Activity.PdfDetailActivity;
import com.example.book.Model.book.ModelPdf;
import com.example.book.MyApplication;
import com.example.book.databinding.RowPdfFavoriteBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterPdfFavorite extends RecyclerView.Adapter<AdapterPdfFavorite.HolderPdfFavorite>{

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;

    //view binding của row_pdf_favorite
    private RowPdfFavoriteBinding binding;

    private static final String TAG = "FAV_BOOK_TAG";

    //construstor
    public AdapterPdfFavorite(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(context),parent, false);

        return new AdapterPdfFavorite.HolderPdfFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfFavorite holder, int position) {
        /*---Get data , set data, handle click---**/
        ModelPdf model = pdfArrayList.get(position);

        loadBookDetails(model, holder);
        //handle click, open pdf detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", model.getId()); // pass book id not category id
                context.startActivity(intent);
            }
        });

        //handle click, remove from favorite
        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.removeFavorite(context,model.getId());
            }
        });

    }

    private void loadBookDetails(ModelPdf model, HolderPdfFavorite holder) {
        String bookId = model.getId();
        Log.d(TAG, "loadBookDetails: Xem sách chi tiết theo id: "+bookId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        String bookTitle = ""+snapshot.child("title").getValue();
                        String decscription = ""+snapshot.child("decscription").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String bookUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String author = ""+snapshot.child("author").getValue();

                        //set model
                        model.setFavorite(true);
                        model.setTitle(bookTitle);
                        model.setDecscription(decscription);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUrl(bookUrl);
                        model.setUid(uid);
                        model.setAuthor(author);

                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(categoryId, holder.categoryTv);
                        MyApplication.loadPdfFromUrlSinglePage(""+bookUrl,
                                ""+bookTitle, holder.pdfView, holder.progressBar, null);
                        MyApplication.loadPdfSize(""+bookUrl,""+bookTitle, holder.sizeTv);


                        //set data
                        holder.titleTv.setText(bookTitle);
                        holder.decscriptionTv.setText(decscription);
                        holder.authorTV.setText(author);
                        holder.dateTv.setText(date);




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    public  class HolderPdfFavorite extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,decscriptionTv,categoryTv,sizeTv,dateTv, authorTV;
        ImageButton removeFavBtn;
        public HolderPdfFavorite(@NonNull View itemView) {
            super(itemView);
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            authorTV = binding.authorTv;
            decscriptionTv = binding.decscriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            removeFavBtn = binding.removeFavBtn;
        }
    }
}
