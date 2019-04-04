package com.primedsoft.primedclass.Auth;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.primedsoft.primedclass.Model.ParentModel;
import com.primedsoft.primedclass.R;
import com.primedsoft.primedclass.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

public class ParentSignupActivity extends AppCompatActivity {
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private ProgressDialog mProgressBar;
    private ImageView circleImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String STATUS = "status";
    SharedPreferences sharedpreferences;
    FirebaseAuth mAuth;
    EditText inputEmail, inputName,inputPhoneNumber,homeAddress,password;
    private LatLng latLng;
    private String lat,lng,email,name,phoneNumber,subject,classT,address,imageUrl;
    private MaterialSpinner spinnerSubject,spinnerClass;
    SharedPreferences preferences;
    private FancyButton setLocation;
    Button completeSignupTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_signup);

        FirebaseApp.initializeApp(ParentSignupActivity.this);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        mProgressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        circleImageView = (ImageView)findViewById(R.id.addImageParent);
        mStorageRef = FirebaseStorage.getInstance().getReference("profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        inputEmail = (EditText)findViewById(R.id.edtParentEmail);
        inputName = (EditText)findViewById(R.id.edtParentName);
        inputPhoneNumber = (EditText)findViewById(R.id.edtParentPhoneNumber);
        homeAddress = (EditText)findViewById(R.id.edtParentAddress);
        password = (EditText)findViewById(R.id.edtParentPassword);


        setLocation = (FancyButton)findViewById(R.id.btnPickParentLocation);
        completeSignupTeacher =  findViewById(R.id.btnCompleteParentRegistration);


        completeSignupTeacher.setOnClickListener(v -> startSignup());
        setLocation.setOnClickListener(view -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(ParentSignupActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        });

        circleImageView.setOnClickListener(v -> openFileChooser());

    }
    private void startSignup() {

        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        phoneNumber = inputPhoneNumber.getText().toString().trim();
        address = homeAddress.getText().toString().trim();

        String mPassword = password.getText().toString().trim();


        if (name.isEmpty()){
            Toast.makeText(this, "Field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()){
            Toast.makeText(this, "Field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.isEmpty()){
            Toast.makeText(this, "Field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.isEmpty()){
            Toast.makeText(this, "Field can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email,mPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (mImageUri != null){
                            mProgressBar.setMessage("Signing you up .. please wait");
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
                                    imageUrl = downUri.toString();

                                    String uid = mAuth.getCurrentUser().getUid();

                                    ParentModel parentModel = new ParentModel();

                                    parentModel.setAddress(address);
                                    parentModel.setEmail(email);
                                    parentModel.setImageUrl(imageUrl);
                                    parentModel.setLatitude(lat);
                                    parentModel.setLongitude(lng);
                                    parentModel.setName(name);
                                    parentModel.setPhone(phoneNumber);
                                    parentModel.setStatus("parent");
                                    parentModel.setUid(uid);



                                    String uploadId = mAuth.getCurrentUser().getUid();


                                    mDatabaseRef.child(uploadId).setValue(parentModel)
                                            .addOnCompleteListener(task1 -> {
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(STATUS, "parent");
                                                editor.apply();


                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ParentSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ParentSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });






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
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                latLng = place.getLatLng();
                lat = String.valueOf(latLng.latitude);
                lng = String.valueOf(latLng.longitude);
            }
        }else  if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(ParentSignupActivity.this).load(mImageUri).transform(new CircleTransform()).into(circleImageView);


        }
    }
}
