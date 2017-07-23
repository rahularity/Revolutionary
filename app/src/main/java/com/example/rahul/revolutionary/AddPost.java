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
import com.squareup.picasso.Picasso;
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
    private Uri imageUri = null,imageUrl,resultUri;
    private ProgressDialog mProgress_save_to_database;
    private String Uid,Name,Email,key=null;
    private String titleString,contentString;
    private String mCategory = "Social";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        key = getIntent().getStringExtra("Key");
        mProgress_save_to_database = new ProgressDialog(this);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child(randomize());
        Uid = mAuth.getCurrentUser().getUid();
        Name = mAuth.getCurrentUser().getDisplayName();
        Email = mAuth.getCurrentUser().getEmail();
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


        if(key!=null){
            oldPost = mRef.child("posts").child(key);
            oldPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    title.setText(dataSnapshot.child("title").getValue(String.class));
                    content.setText(dataSnapshot.child("content").getValue(String.class));
                    String image =dataSnapshot.child("ImageUrl").getValue(String.class);
                    Picasso.with(AddPost.this).load(image).into(post_image);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


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
                titleString = title.getText().toString().trim();
                contentString = content.getText().toString().trim();
                if(titleString.length()!=0 && contentString.length()!=0) {

                    mProgress_save_to_database.setMessage("saving data...");
                    mProgress_save_to_database.show();
                    mProgress_save_to_database.setCancelable(false);
                    saveAnonymously();
                }else{
                    Toast.makeText(AddPost.this,"Please write some information before saving...",Toast.LENGTH_LONG).show();
                }

            }
        });

    }




    private void saveAnonymously() {

        if(key!=null){
            oldPost.child("title").setValue(titleString);
            oldPost.child("content").setValue(contentString);
            oldPost.child("uid").setValue(Uid);
            oldPost.child("name").setValue(Name);
            oldPost.child("email").setValue(Email);
            oldPost.child("anonymous").setValue("true");
            mStorage.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(AddPost.this,"upload done",Toast.LENGTH_LONG).show();
                    imageUrl = taskSnapshot.getDownloadUrl();
                    newPost.child("ImageUrl").setValue(imageUrl.toString());
                    mProgress_save_to_database.dismiss();

                    finish();
                }
            });
        }else{
            newPost.child("title").setValue(titleString);
            newPost.child("content").setValue(contentString);
            newPost.child("uid").setValue(Uid);
            newPost.child("name").setValue(Name);
            newPost.child("email").setValue(Email);
            newPost.child("anonymous").setValue("true");
            mStorage.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    }

    private void saveNormally() {

        if(key!=null){
            oldPost.child("title").setValue(titleString);
            oldPost.child("content").setValue(contentString);
            oldPost.child("uid").setValue(Uid);
            oldPost.child("name").setValue(Name);
            oldPost.child("email").setValue(Email);
            oldPost.child("anonymous").setValue("false");
            mStorage.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(AddPost.this,"upload done",Toast.LENGTH_LONG).show();
                    imageUrl = taskSnapshot.getDownloadUrl();
                    newPost.child("ImageUrl").setValue(imageUrl.toString());
                    mProgress_save_to_database.dismiss();

                    finish();
                }
            });
        }else{
            newPost.child("title").setValue(titleString);
            newPost.child("content").setValue(contentString);
            newPost.child("uid").setValue(Uid);
            newPost.child("name").setValue(Name);
            newPost.child("email").setValue(Email);
            newPost.child("anonymous").setValue("false");
            mStorage.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                    .setAspectRatio(4,3)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                post_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
