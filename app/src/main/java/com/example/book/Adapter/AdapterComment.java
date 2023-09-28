package com.example.book.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Model.book.ModelComment;
import com.example.book.Model.user.ModelUser;
import com.example.book.MyApplication;
import com.example.book.R;
import com.example.book.databinding.RowCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    //context
    private Context context;

    //array list
    private ArrayList<ModelComment> commentArrayList;

    private FirebaseAuth firebaseAuth;

    //view binding
    private RowCommentBinding binding;

    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context),parent,false);

        return new AdapterComment.HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        /*Get- set data*/

        ModelComment modelComment = commentArrayList.get(position);
        String id = modelComment.getId();
        String bookId = modelComment.getBookId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();

        //format date in MyApplication
        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

        //set data
        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        //load detail comment
        loadUserDetails(modelComment,holder);

        //handle click, show option delete comment
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Yêu cầu xóa một bình luận:
                *1)Người dùng phải đăng nhập
                *2)uid trong nhận xét bị xóa phải giống với uid của người dùng đã đăng nhập*/
                if (firebaseAuth.getCurrentUser() != null && uid.equals(firebaseAuth.getUid())){
                    deletesComment(modelComment, holder);
                }
            }
        });
    }

    //xóa bình luận
    private void deletesComment(ModelComment modelComment, HolderComment holder) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa bình luận")
                .setMessage("Bạn có chắc chắn muốn xóa bình luận của mình không?")
                .setPositiveButton("XÓA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete comment
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(modelComment.getBookId()).child("Comments")
                                .child(modelComment.getId())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Xóa thành công"+modelComment.getComment(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Lỗi ko xóa được do "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loadUserDetails(ModelComment modelComment, HolderComment holder) {
        String uid = modelComment.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        //get data
                        String name = ""+snapshot.child("name").getValue();
                        String profileimage = ""+snapshot.child("profileimage").getValue();

                        //set data
                        holder.nameTv.setText(name);
                        /*set image, using picasso
                         *nếu bạn là admin thì sẽ load hình profile admin
                         * và ngược lại nếu bạn là người dùng thì load hình profile người dùng
                         * và cuối cùng nếu bạn thay đổi ảnh profile của cả hai thì nó sẽ load hình thay đổi đó */

                        try {
                            Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.user).into(binding.profileTv);
                        }
                        catch (Exception e){
                            holder.profileTv.setImageResource(R.drawable.user);
                        }
//                        String userType = null;
//                        if (profileimage.isEmpty() && Objects.equals(userType, "admin")){
//                            binding.profileTv.setImageResource(R.drawable.admin);
//                        }
//                        else if (profileimage.isEmpty() && userType.equals("user")){
//                            binding.profileTv.setImageResource(R.drawable.user);
//                        }
//                        else {
//
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class HolderComment extends RecyclerView.ViewHolder{
        ShapeableImageView profileTv;
        TextView nameTv,dateTv,commentTv;
        public HolderComment(@NonNull View itemView) {
            super(itemView);
            profileTv = binding.profileTv;
            nameTv = binding.nameTv;
            dateTv = binding.dateTv;
            commentTv = binding.commentTv;
        }
    }
}
