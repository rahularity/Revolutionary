package com.example.rahul.revolutionary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

public class AddPost extends AppCompatActivity {

    private static final int MAX_LENGTH = 20;
    private static final int PICK_PHOTO = 0;
    private ImageView post_image;
    private EditText title,content;
    private Spinner category;
    private Button save_normal,save_anonymous;
    private DatabaseReference mRef,mRefUser,newPost,oldPost;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri imageUri = null,imageUrl;
    private ProgressDialog mProgress_save_to_database;
    private String Uid;
    private String titleString,contentString;
    private String mCategory = "Social";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress_save_to_database = new ProgressDialog(this);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("blog_pics").child(randomize());
        Uid = mAuth.getCurrentUser().getUid();
        newPost = mRef.child("posts").push();

        title = (EditText)findViewById(R.id.title_post);
        content = (EditText)findViewById(R.id.content_post);
        category = (Spinner)findViewById(R.id.category);
        save_anonymous = (Button)findViewById(R.id.anonymous_post);
        save_normal = (Button)findViewById(R.id.yourself_post);
        post_image = (ImageView)findViewById(R.id.post_image);
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, PICK_PHOTO);
            }
        });

        save_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleString = title.getText().toString().trim();
                contentString = content.getText().toString().trim();
                if(titleString.length()!=0 && contentString.length()!=0) {

                    mProgress_save_to_database.setMessage("saving data...");
                    mProgress_save_to_database.show();
                    mProgress_save_to_database.setCancelable(false);
                    saveNormally();
                }else{
                    Toast.makeText(AddPost.this,"Please write some information before saving...",Toast.LENGTH_LONG).show();
                }
            }
        });

        save_anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgress_save_to_database.setMessage("saving data...");
//                mProgress_save_to_database.show();
                saveAnonymously();
            }
        });

    }




    private void saveAnonymously() {

        Toast.makeText(AddPost.this,"App Under construction...",Toast.LENGTH_LONG).show();

    }

    private void saveNormally() {
        newPost.child("title").setValue(titleString);
        newPost.child("content").setValue(contentString);
        newPost.child("uid").setValue(Uid);
        mStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AddPost.this,"upload done",Toast.LENGTH_LONG).show();
                imageUrl = taskSnapshot.getDownloadUrl();
                newPost.child("ImageUrl").setValue(imageUrl.toString());
                mProgress_save_to_database.dismiss();

                finish();
            }
        });

    }

    public static String randomize() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK){

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                post_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
