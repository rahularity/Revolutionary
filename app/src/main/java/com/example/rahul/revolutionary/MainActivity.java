package com.example.rahul.revolutionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public String[] mDrawableListItem;
    private RecyclerView mPostsList;
    boolean mLoved = false;
    String Uid;
    FirebaseAuth mAuth;
    private DatabaseReference mRefLoves,mRef,mLoveNode,mRefPosts;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public Query postsNode,likedPosts;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentNavigationDrawer fragment = (FragmentNavigationDrawer) getFragmentManager().findFragmentById(R.id.fragment_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        mDrawableListItem = getResources().getStringArray(R.array.drawer_list);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                myToolbar,
                R.string.app_name,
                R.string.app_name
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });





        mRef=FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        postsNode = FirebaseDatabase.getInstance().getReference().child("posts");
        mPostsList = (RecyclerView)findViewById(R.id.posts_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostsList.setLayoutManager(mLayoutManager);



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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPost.class);
                startActivity(intent);
            }
        });


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        //getting the reference of the liked posts here in likedPosts and then putting the value in postsNode for retrieving liked posts
        DatabaseReference Node = FirebaseDatabase.getInstance().getReference().child("loves");
        final String likesUid = "likes/"+Uid;

        Node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likedPosts = dataSnapshot.getRef().orderByChild(likesUid).equalTo("thank you for your love");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }else{
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;

            case R.id.sign_out:
                mAuth.signOut();
                break;

            case R.id.myPosts:
                postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("uid").equalTo(Uid);
                setTheScreen();
                Toast.makeText(MainActivity.this,"you selected your posts",Toast.LENGTH_SHORT).show();
                break;

            case R.id.likedPosts:

                postsNode = likedPosts;
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
        postsNode = FirebaseDatabase.getInstance().getReference().child("posts");
        setTheScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    public void setTheScreen(){
       FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post,PostViewHolder>(
               Post.class,
               R.layout.post_display_blueprint,
               PostViewHolder.class,
               postsNode
       ) {
           @Override
           protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {
               final String key = getRef(position).getKey();
               mRefPosts = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
               mLoveNode = mRef.child("loves");
               mRefLoves = mRef.child("loves").child(key);
               viewHolder.setTitle(model.getTitle());
               viewHolder.setImage(getApplicationContext(),model.getImageUrl());
               viewHolder.setLoves(mRefLoves);
               viewHolder.setLoveButton(Uid,key);


               mRefPosts.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {

                       String isAnonymous = dataSnapshot.child("anonymous").getValue(String.class);
                       if (isAnonymous != null && isAnonymous.equals("true")) {
                           viewHolder.anonymousButton.setVisibility(View.VISIBLE);
                       }

                   }
                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });

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
                                       mRefPosts = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
                                       mRefPosts.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               mLoveNode.child(key).child("title").setValue(dataSnapshot.child("title").getValue(String.class));
                                               mLoveNode.child(key).child("content").setValue(dataSnapshot.child("content").getValue(String.class));
                                               mLoveNode.child(key).child("ImageUrl").setValue(dataSnapshot.child("ImageUrl").getValue(String.class));
                                               mLoveNode.child(key).child("uid").setValue(dataSnapshot.child("uid").getValue(String.class));
                                               mLoveNode.child(key).child("name").setValue(dataSnapshot.child("name").getValue(String.class));
                                               mLoveNode.child(key).child("email").setValue(dataSnapshot.child("email").getValue(String.class));
//                                               mLoveNode.child(key).child("category").setValue(dataSnapshot.child("category").getValue(String.class));
                                           }
                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {                                           }
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


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this,PostDisplayPageActivity.class);
                        intent.putExtra("Key",key);
                        startActivity(intent);

                    }
                });
           }
       };
       mPostsList.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.notifyDataSetChanged();
   }

    private static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton heartButton,anonymousButton;
        DatabaseReference mLoveNode;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            heartButton = (ImageButton)mView.findViewById(R.id.heart_button);
            anonymousButton = (ImageButton)mView.findViewById(R.id.anonymous_button);
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
                public void onCancelled(DatabaseError databaseError) {}
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
            Picasso.with(ctx).load(imageUrl).fit().centerCrop().into(post_image);
        }

    }



}
