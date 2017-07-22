package com.example.rahul.revolutionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mPostsList;
    boolean mLoved = false;
    String Uid;
    FirebaseAuth mAuth;
    private DatabaseReference mRefLoves,mRef,mLoveNode,mRefPosts;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Query postsNode;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRef=FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        postsNode = FirebaseDatabase.getInstance().getReference().child("posts");
        mPostsList = (RecyclerView)findViewById(R.id.posts_list);
        mPostsList.setLayoutManager(new LinearLayoutManager(this));


        Uid = mAuth.getCurrentUser().getUid();
        dialog=new ProgressDialog(this);

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.add_post:
                startActivity(new Intent(MainActivity.this,AddPost.class));
                break;

            case R.id.all:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts");
                setTheScreen();
                break;

            case R.id.sign_out:
                mAuth.signOut();
                break;

            case R.id.social:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("social");
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is social",Toast.LENGTH_SHORT).show();
                break;

            case R.id.conversation:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("conversation");
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is conversation",Toast.LENGTH_SHORT).show();
                break;

            case R.id.ideas:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("ideas");
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is ideas",Toast.LENGTH_SHORT).show();
                break;

            case R.id.day_to_day:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("daytoday");
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is Day To Day",Toast.LENGTH_SHORT).show();
                break;

            case R.id.life_style:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("lifestyle");
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is Life Style",Toast.LENGTH_SHORT).show();
                break;

            case R.id.liked_posts:
                DatabaseReference Node = FirebaseDatabase.getInstance().getReference().child("loves");
                final String likesUid = "likes/"+Uid;
                Node.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        postsNode = dataSnapshot.getRef().orderByChild(likesUid).equalTo("thank you for your love");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selection is Liked Pages",Toast.LENGTH_SHORT).show();

                break;


            default:
                return super.onOptionsItemSelected(item);


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        setTheScreen();
    }

   private void setTheScreen(){
       FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post,PostViewHolder>(
               Post.class,
               R.layout.post_display_blueprint,
               PostViewHolder.class,
               postsNode
       ) {
           @Override
           protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {



               final String key = getRef(position).getKey();

               mRefPosts = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
               mLoveNode = mRef.child("loves");
               mRefLoves = mRef.child("loves").child(key);
               viewHolder.setTitle(model.getTitle());
               //viewHolder.setImage(getApplicationContext(),model.getImageUrl());
               viewHolder.setLoves(mRefLoves);
               viewHolder.setLoveButton(Uid,key);



               viewHolder.heartButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       mLoved = true;

                       mLoveNode.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               if(mLoved){
                                   if(dataSnapshot.child(key).child("likes").hasChild(Uid)){

                                       mLoveNode.child(key).child("likes").child(Uid).removeValue();
                                       mLoved = false;
                                   }else {
                                       mRefPosts.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               Toast.makeText(MainActivity.this,"inside addValueEvent",Toast.LENGTH_SHORT).show();
                                               mLoveNode.child(key).child("title").setValue(dataSnapshot.child("title").getValue(String.class));
                                               mLoveNode.child(key).child("content").setValue(dataSnapshot.child("content").getValue(String.class));
                                               mLoveNode.child(key).child("ImageUrl").setValue(dataSnapshot.child("ImageUrl").getValue(String.class));
                                               mLoveNode.child(key).child("uid").setValue(dataSnapshot.child("uid").getValue(String.class));
                                               mLoveNode.child(key).child("name").setValue(dataSnapshot.child("name").getValue(String.class));
                                               mLoveNode.child(key).child("email").setValue(dataSnapshot.child("email").getValue(String.class));
//                                               mLoveNode.child(key).child("category").setValue(dataSnapshot.child("category").getValue(String.class));
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });
                                       mLoveNode.child(key).child("likes").child(Uid).setValue("thank you for your love");
                                       mLoved = false;
                                   }
                               }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                   }
               });


//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent(MainActivity.this,InterestedAd.class);
//                        intent.putExtra("Key",fb_key);
//                        startActivity(intent);
//
//                    }
//                });
           }
       };
       mPostsList.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.notifyDataSetChanged();
   }

    private static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton heartButton;
        DatabaseReference mLoveNode;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            heartButton = (ImageButton)mView.findViewById(R.id.heart_button);
            mLoveNode = FirebaseDatabase.getInstance().getReference().child("loves");
        }

        public void setLoves(DatabaseReference mRef){

            final TextView loveCount_textview = (TextView) mView.findViewById(R.id.love_count);
            mRef.child("likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long countLove = dataSnapshot.getChildrenCount();
                    String count = String.valueOf(countLove);
                    loveCount_textview.setText(count);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        private void setLoveButton(final String Uid, final String key){

            mLoveNode.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(key).child("likes").hasChild(Uid)){
                        heartButton.setBackgroundResource(R.drawable.heart_filled);
                    }else{
                        heartButton.setBackgroundResource(R.drawable.heart);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title) {
            TextView title_textview = (TextView) mView.findViewById(R.id.title_textview);
            title_textview.setText(title);
        }

        private void setImage(Context ctx,String imageUrl){
            ImageView post_image = (ImageView)mView.findViewById(R.id.blog_image);
            Picasso.with(ctx).load(imageUrl).into(post_image);
        }

    }



}
