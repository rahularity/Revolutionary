package com.example.rahul.revolutionary;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

/**
 * Created by Rahul on 7/25/2017.
 */

public class FragmentNavigationDrawer extends Fragment {

    TextView social,fashion,thoughts,food,lifestyle,experiences,all;
    FirebaseAuth mAuth;
    Query mRefMyPosts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        all = (TextView)view.findViewById(R.id.all);
        social = (TextView)view.findViewById(R.id.social);
        fashion = (TextView)view.findViewById(R.id.fashion);
        food = (TextView)view.findViewById(R.id.food);
        thoughts = (TextView)view.findViewById(R.id.thoughts);
        lifestyle = (TextView)view.findViewById(R.id.lifeStyle);
        experiences = (TextView)view.findViewById(R.id.experiences);


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("social");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("fashion");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        thoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("thoughts");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("food");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        lifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("lifestyle");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        experiences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).postsNode = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("category").equalTo("experiences");
                ((MainActivity)getActivity()).setTheScreen();
                ((MainActivity)getActivity()).mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

    }
}
