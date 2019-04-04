package com.primedsoft.primedclass.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllParents extends Fragment {

    FirebaseAuth auth;
    private RecyclerView parentsRv;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private Query parentsQuery;


    public AllParents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_parents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        parentsRv = (RecyclerView) view.findViewById(R.id.rvAllParents);
        parentsQuery = mUsersDatabase.orderByChild("status").equalTo("parent");

        mLayoutManager = new LinearLayoutManager(getContext());
        parentsRv.setHasFixedSize(true);
        parentsRv.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<ParentModel,AllParent> adapter = new FirebaseRecyclerAdapter<ParentModel, AllParent>(
                ParentModel.class,
                R.layout.single_child_item,
                AllParent.class,
                parentsQuery
        ) {
            @Override
            protected void populateViewHolder(AllParent viewHolder, ParentModel model, int position) {

                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserImage(model.getImageUrl(),getContext());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO set onclick listener for full profile
                    }
                });

            }
        };
        parentsRv.setAdapter(adapter);
    }

    public static class AllParent extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;


        public AllParent(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);
            userImage = (ImageView) mView.findViewById(R.id.imgChild);
            name = (TextView)mView.findViewById(R.id.childName);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }




        public void setUserImage(String status, Context context){

            Picasso.with(context).load(status).transform(new CircleTransform()) .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(status).into(userImage);

                }
            });



        }


    }
}
