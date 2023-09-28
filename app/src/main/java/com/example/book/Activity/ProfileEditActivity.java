package com.example.book.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.book.Model.user.ModelUser;
import com.example.book.MyApplication;
import com.example.book.R;
import com.example.book.databinding.ActivityProfileEditBinding;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;


public class ProfileEditActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileEditBinding binding;

    //firebase user
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_EDIT_TAG";


    //xử lí profile ảnh
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //image picked uri
    private Uri image_uri;
    private String imageUrl = "" ;
    //Xử lý đăng lên storage firebase
    private StorageTask uploadTask;
    StorageReference Postreference;

    private String name ="";
    private ImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profile = binding.profileTv;
        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi trong giây lát");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup firebase auth
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //set up firebase storage
//        Postreference = FirebaseStorage.getInstance().getReference().child("ProfileImage/");
        loadUserInfo();
        //init permissions array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //handle click, button back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.profileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();// dành cho compileSdk 32 trở xuống, trong build.gradle
//                imageDialog();// dành cho compileSdk 33, trong build.gradle
            }
        });

        //handle click, button upload
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Trường hợp này giải thích như sau:
            * Hình ảnh profile mặc định theo tài khoản đã có hình sẵn
            * Nên ở đây chúng ta không đặt điều kiện khi nó bằng null
            * Chỉ đặt điều kiện = null thì nó sẽ câu if bên dưới khi chúng ta có cập nhật lại tên thì chỉ đổi tên thôi.
            * Ngược lại nếu thay đổi hình ko thay đổi tên thì tên vẫn giữ nguyên và đi qua else
            * Và cuối cùng nếu đổi cả 2 thì nó đi qua else*/
                if (image_uri == null){
                    uploadName(); // thay đổi tên
                }
                else {
                    uploadName();
                    updateAnh();//thay đổi ảnh

                }

            }
        });
    }

    //Image profile
    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, (dialog, which) -> {
                    //handle clicks
                    if (which == 0) {
                        //camera clicked
                        if (checkCameraPermission()) {
                            //camera permissions allowed
                            pickFromCamera();
                        } else {
                            //not allowed, request
                            requestCameraPermission();
                        }
                    } else {
                        //gallery clicked
                        if (checkStoragePermission()) {
                            //storage permissions allowed
                            pickFromGallery();
                        } else {
                            //not allowed, request
                            requestStoragePermission();
                        }
                    }
                })
                .show();

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    //image profile
    private boolean checkStoragePermission(){

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    //image profile
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    //image profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                //get picked image
                assert data != null;
                image_uri = data.getData();
                //set to imageview
                binding.profileTv.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                binding.profileTv.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadName() {
        name = binding.nameEt.getText().toString().trim();

        //validateData

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Vui lòng điền tên mới vào đây", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadNameDB();
        }
    }

    private void uploadNameDB() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("name",""+name);

        Toast.makeText(this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "uploadNameDB: Cập nhật tên: "+name+ " thành công");
        reference.updateChildren(hashMap);
    }

    private void updateAnh() {
        if (image_uri != null){
            Log.d(TAG, "updateAnh: Đã vô tới update ảnh");
            //name and path of image
            String filePathAndName = "profile_images/" + "" + firebaseUser.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        //get url of uploaded image
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadImageUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {

                            //setup data to save
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", "" + firebaseUser.getUid());
                            hashMap.put("profileimage", "" + downloadImageUri);//url of uploaded image

                            //save to db
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(firebaseUser.getUid()).updateChildren(hashMap)
                                    .addOnSuccessListener(aVoid -> {
                                        //db updated
                                        progressDialog.dismiss();
                                        Log.d(TAG, "saverFirebaseData: Upload thêm ảnh thành công");
                                        Toast.makeText(ProfileEditActivity.this, "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileEditActivity.this, ProfileActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        //failed updating db
                                        progressDialog.dismiss();
                                        Log.d(TAG, "updateAnh: Failed");
                                        Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileEditActivity.this, ProfileActivity.class));
                                        finish();
                                    });

                        }
                    });
        }
    }

    //load tất cả những thông tin cần có của phần profile
    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Đang tải thông tin người dùng của người dùng");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user here from snapshot
                        ModelUser user = snapshot.getValue(ModelUser.class);
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
                        binding.nameEt.setText(name);

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

    /*Đoạn code dành cho phần xử lý hình ảnh
    * Nếu trong build.gradle của bạn compileSdk 33 thì dùng đoạn code bên dưới nha
    * Còn nếu trong build.gradle của bạn từ compileSdk 32 trở xuống thì đóng đoạn code phía bên dưới lại*/
//    private void imageDialog(){
//        PopupMenu popupMenu = new PopupMenu(ProfileEditActivity.this,binding.profileTv);
//        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
//        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");
//
//        popupMenu.show();
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int itemId = item.getItemId();
//                if (itemId ==1){
//                    Log.d(TAG, "onMenuItemClick: Mở camera, check camera");
//                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
//                        requestCameraPemissions.launch(new String[]{Manifest.permission.CAMERA});
//                    }else {
//                        requestCameraPemissions.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
//                    }
//                }
//                else if (itemId==2){
//                    Log.d(TAG, "onMenuItemClick: Mở storage, check storage");
//                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
//                        pickFromGallery1();
//                    }else {
//                        requestStoragePemissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    }
//                }
//                return false;
//            }
//        });
//    }
//    private ActivityResultLauncher<String[]> requestCameraPemissions = registerForActivityResult(
//            new ActivityResultContracts.RequestMultiplePermissions(),
//            new ActivityResultCallback<Map<String,Boolean>>(){
//
//                @Override
//                public void onActivityResult(Map<String, Boolean> result) {
//                    Log.d(TAG, "onActivityResult: "+result.toString());
//                    boolean areAllGranted = true;
//                    for (Boolean isGranted: result.values()){
//                        areAllGranted = areAllGranted && isGranted;
//                    }
//                    if (areAllGranted){
//                        Log.d(TAG, "onActivityResult: Tất cả quyền camera & storage");
//                        pickFromCamera1();
//                    }
//                    else {
//                        Log.d(TAG, "onActivityResult: Tất cả hoặc chỉ có một quyền");
//                        Toast.makeText(ProfileEditActivity.this, "Quyền camera hoặc storage", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );
//
//    private ActivityResultLauncher<String> requestStoragePemissions = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            new ActivityResultCallback<Boolean>() {
//                @Override
//                public void onActivityResult(Boolean isGranted) {
//                    if (isGranted){
//                        pickFromGallery1();
//                    }
//                    else {
//                        Toast.makeText(ProfileEditActivity.this, "Quyền Storage chưa cấp quyền", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );
//
//    private void pickFromGallery1() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//       galleryActivityResultLaucher.launch(intent);
//    }
//    private ActivityResultLauncher<Intent> galleryActivityResultLaucher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK){
//                        Log.d(TAG, "onActivityResult: Hình ảnh thư viện: "+image_uri);
//                        Intent data = result.getData();
//                        image_uri = data.getData();
//                        try {
//                            Glide.with(ProfileEditActivity.this)
//                                    .load(image_uri)
//                                    .placeholder(R.drawable.ic_person_gray)
//                                    .into(binding.profileTv);
//                        }catch (Exception e){
//                            Log.d(TAG, "onActivityResult: "+e);
//                            Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        Toast.makeText(ProfileEditActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );
//
//
//    private void pickFromCamera1() {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");
//
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
//        cameraActivityResultLaucher.launch(intent);
//
//    }
//    private ActivityResultLauncher<Intent> cameraActivityResultLaucher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK){
//                        Log.d(TAG, "onActivityResult: Hình ảnh: "+image_uri);
//                        try {
//                            Glide.with(ProfileEditActivity.this)
//                                    .load(image_uri)
//                                    .placeholder(R.drawable.ic_person_gray)
//                                    .into(binding.profileTv);
//                        }catch (Exception e){
//                            Log.d(TAG, "onActivityResult: "+e);
//                            Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        Toast.makeText(ProfileEditActivity.this, "Hủy", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );



}