package com.example.rahul.revolutionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PostDisplayPageActivity extends AppCompatActivity {

    private TextView title,authorName,authorEmail,content;
    private ImageButton edit,delete,love;
    private ImageView postImage;
    private DatabaseReference mPostRef,mLoveRef,mRef;
    private StorageReference mPicReference;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String key = null,Uid,photoUrl;
    private boolean mLoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display_page);

        title = (TextView)findViewById(R.id.title);
        authorName = (TextView)findViewById(R.id.author_name);
        authorEmail = (TextView)findViewById(R.id.author_email);
        content = (TextView)findViewById(R.id.content);
        edit = (ImageButton)findViewById(R.id.edit_button);
        delete = (ImageButton)findViewById(R.id.delete_button);
        love = (ImageButton)findViewById(R.id.love_button);
        postImage = (ImageView)findViewById(R.id.image_post);

        key = getIntent().getStringExtra("Key");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Uid = currentUser.getUid();
        }

        mFirebaseStorage = FirebaseStorage.getInstance();
        mLoveRef = FirebaseDatabase.getInstance().getReference().child("loves");
        mPostRef = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
        mRef = FirebaseDatabase.getInstance().getReference().child("posts");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.child(key).child("uid").getValue(String.class);
                if (uid != null && uid.equals(Uid)) {
                    photoUrl = dataSnapshot.child(key).child("ImageUrl").getValue(String.class);
                    delete.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnsetLove(mLoveRef,key,Uid);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostRef.removeValue();
                mLoveRef.child(key).removeValue();
                mPicReference = mFirebaseStorage.getReferenceFromUrl(photoUrl);
                mPicReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PostDisplayPageActivity.this,"deleted successfully",Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                Intent mainActivity = new Intent(PostDisplayPageActivity.this,MainActivity.class);
                startActivity(mainActivity);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost();
            }
        });

        showPost();
        setLoveButton(Uid,key);

    }

    private void editPost() {

        Intent editorActivity = new Intent(PostDisplayPageActivity.this,AddPost.class).putExtra("Key",key);
        startActivity(editorActivity);

    }

    private void setUnsetLove(final DatabaseReference mLoveRef, final String key, String uid) {

        mLoved = true;

        mLoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(mLoved){
                    if(dataSnapshot.child(key).child("likes").hasChild(Uid)){

                        mLoveRef.child(key).child("likes").child(Uid).removeValue();
                        mLoved = false;
                    }else {
                        mPostRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mLoveRef.child(key).child("title").setValue(dataSnapshot.child("title").getValue(String.class));
                                mLoveRef.child(key).child("content").setValue(dataSnapshot.child("content").getValue(String.class));
                                mLoveRef.child(key).child("ImageUrl").setValue(dataSnapshot.child("ImageUrl").getValue(String.class));
                                mLoveRef.child(key).child("uid").setValue(dataSnapshot.child("uid").getValue(String.class));
                                mLoveRef.child(key).child("name").setValue(dataSnapshot.child("name").getValue(String.class));
                                mLoveRef.child(key).child("email").setValue(dataSnapshot.child("email").getValue(String.class));
//                                               mLoveNode.child(key).child("category").setValue(dataSnapshot.child("category").getValue(String.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mLoveRef.child(key).child("likes").child(Uid).setValue("thank you for your love");
                        mLoved = false;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showPost() {

        mPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("anonymous").getValue(String.class).equals("true")){
                    title.setText(dataSnapshot.child("title").getValue(String.class));
                    authorEmail.setText("Anonymous");
                    authorName.setText("Anonymous");
                    content.setText(dataSnapshot.child("content").getValue(String.class));
                    Picasso.with(PostDisplayPageActivity.this).load(dataSnapshot.child("ImageUrl").getValue(String.class)).into(postImage);

                }else{
                    title.setText(dataSnapshot.child("title").getValue(String.class));
                    authorEmail.setText(dataSnapshot.child("email").getValue(String.class));
                    authorName.setText(dataSnapshot.child("name").getValue(String.class));
                    content.setText(dataSnapshot.child("content").getValue(String.class));
                    Picasso.with(PostDisplayPageActivity.this).load(dataSnapshot.child("ImageUrl").getValue(String.class)).into(postImage);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setLoveButton(final String Uid, final String key){

        mLoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(key).child("likes").hasChild(Uid)){
                    love.setBackgroundResource(R.drawable.heart_filled);
                }else{
                    love.setBackgroundResource(R.drawable.heart);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
