package com.primedsoft.primedclass.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.primedsoft.primedclass.Model.ChildModel;
import com.primedsoft.primedclass.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildDetails extends Fragment {
    String key;
    private FirebaseAuth auth;
    private DatabaseReference mUsersDatabase;
    EditText childName, childClass,childAge,childExpectation,academicChallenges;
    Button btnAddChild;
    ProgressDialog mProgressBar;


    public ChildDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_child_details, container, false);

       key = container.getTag().toString();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        mProgressBar = new ProgressDialog(getContext());
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Children").child(uid);

        childAge = (EditText)view.findViewById(R.id.edtChildAge);
        childClass = (EditText)view.findViewById(R.id.edtChildPresentClass);
        childName = (EditText)view.findViewById(R.id.edtChildName);
        childExpectation = (EditText)view.findViewById(R.id.edtParentExpectation);
        academicChallenges = (EditText)view.findViewById(R.id.edtChildChallenges);

        btnAddChild = (Button)view.findViewById(R.id.btnAddChild);

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddingChild();
            }
        });


        getChildProfile();

    }

    private void startAddingChild() {
        String age = childAge.getText().toString().trim();
        String chilClass = childClass.getText().toString().trim();
        String name = childName.getText().toString().trim();
        String expectation = childExpectation.getText().toString().trim();
        String academicChallenge = academicChallenges.getText().toString().trim();

        if (age.isEmpty()){
            Toast.makeText(getContext(), "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chilClass.isEmpty()){
            Toast.makeText(getContext(), "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()){
            Toast.makeText(getContext(), "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expectation.isEmpty()){
            Toast.makeText(getContext(), "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (academicChallenge.isEmpty()){
            Toast.makeText(getContext(), "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }


            mProgressBar.setMessage("Updating your child info .. please wait");
            mProgressBar.show();


    }


    private void getChildProfile() {
        mUsersDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChildModel childModel = dataSnapshot.getValue(ChildModel.class);

                childName.setText(childModel.getName());
                childAge.setText(childModel.getAge());
                childClass.setText(childModel.getKlass());
                childExpectation.setText(childModel.getExpectation());
                academicChallenges.setText(childModel.getChallenges());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
