package com.primedsoft.primedclass.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.primedsoft.primedclass.Model.ChildModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddChildActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;
    private ProgressDialog mProgressBar;
    private CircleImageView circleImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private ImageView background;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    FirebaseAuth mAuth;
    EditText childName, childClass,childAge,childExpectation,academicChallenges;
    Button btnAddChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        FirebaseApp.initializeApp(AddChildActivity.this);
        mProgressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        circleImageView = (CircleImageView) findViewById(R.id.imgAddChild);
        mStorageRef = FirebaseStorage.getInstance().getReference("Child's Profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Children");
        background = (ImageView)findViewById(R.id.imgBackground);




        childAge = (EditText)findViewById(R.id.edtChildAge);
        childClass = (EditText)findViewById(R.id.edtChildPresentClass);
        childName = (EditText)findViewById(R.id.edtChildName);
        childExpectation = (EditText)findViewById(R.id.edtParentExpectation);
        academicChallenges = (EditText)findViewById(R.id.edtChildChallenges);

        btnAddChild = (Button)findViewById(R.id.btnAddChild);

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddingChild();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void startAddingChild() {
        String age = childAge.getText().toString().trim();
        String chilClass = childClass.getText().toString().trim();
        String name = childName.getText().toString().trim();
        String expectation = childExpectation.getText().toString().trim();
        String academicChallenge = academicChallenges.getText().toString().trim();

        if (age.isEmpty()){
            Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chilClass.isEmpty()){
            Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()){
            Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expectation.isEmpty()){
            Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (academicChallenge.isEmpty()){
            Toast.makeText(this, "Field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri != null){
            mProgressBar.setMessage("Adding your child .. please wait");
            mProgressBar.setCancelable(false);
            mProgressBar.show();

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downUri = task.getResult();
                   String imageUrl  = downUri.toString();

                   String uid = mAuth.getCurrentUser().getUid();

                    ChildModel childModel = new ChildModel();
                    childModel.setAge(age);
                    childModel.setChallenges(academicChallenge);
                    childModel.setExpectation(expectation);
                    childModel.setImageUrl(imageUrl);
                    childModel.setName(name);
                    childModel.setKlass(chilClass);

                    mDatabaseRef.child(uid).push().setValue(childModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        mProgressBar.dismiss();
                                        Toast.makeText(AddChildActivity.this, "Successfully added your child to PrimedClass", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddChildActivity.this,HomeTeacher.class));
                                        finish();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressBar.dismiss();
                            Toast.makeText(AddChildActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddChildActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // image chooser
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(AddChildActivity.this).load(mImageUri).transform(new CircleTransform()).into(circleImageView);
            Picasso.with(AddChildActivity.this).load(mImageUri).into(background);


        }
    }

}
