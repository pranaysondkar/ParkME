package com.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetupActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public static final String FIRST_NAME = "First Name";
    public static final String LAST_NAME = "Last Name";
    public static final String PHONE_NUMBER = "Phone Number";
    public static final String EMAIL_ID = "Email ID";
    public static final String RC_NUMBER = "RC Number";
    public static final String TAG = "User Data";
    FirebaseFirestore mUserInfo = FirebaseFirestore.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        retainUserInfo();
    }

    private void retainUserInfo() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        final String email_firebaseauth = (firebaseUser.getEmail());
        final String phonenumber_firebaseauth = (firebaseUser.getPhoneNumber());

        if (phonenumber_firebaseauth == null) {
            final DocumentReference UserRef = mUserInfo.collection("User Info").document(email_firebaseauth);
            UserRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        String firstname = documentSnapshot.getString(FIRST_NAME);
                        String lastname = documentSnapshot.getString(LAST_NAME);
                        String phonenumber = documentSnapshot.getString(PHONE_NUMBER);
                        String emailid = documentSnapshot.getString(EMAIL_ID);
                        String rcnumber = documentSnapshot.getString(RC_NUMBER);

                        EditText mFirstname = (EditText) findViewById(R.id.first_name_txt_profile_setup);
                        EditText mLastname = (EditText) findViewById(R.id.last_name_txt_profile_setup);
                        EditText mPhonenumber = (EditText) findViewById(R.id.phone_number_profile_setup);
                        EditText mEmailid = (EditText) findViewById(R.id.email_id_txt_profile_setup);
                        EditText mRcnumber = (EditText) findViewById(R.id.rc_number_txt_profile_setup);

                        mFirstname.setText(firstname);
                        mLastname.setText(lastname);
                        mPhonenumber.setText(phonenumber);
                        mEmailid.setText(emailid);
                        mRcnumber.setText(rcnumber);
                    } else if (e != null) {
                        Log.w(TAG, "Got an Exception!", e);
                    }

                }
            });
        }else if (email_firebaseauth == null) {
            final DocumentReference UserRef = mUserInfo.collection("User Info").document(phonenumber_firebaseauth);
            UserRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        String firstname = documentSnapshot.getString(FIRST_NAME);
                        String lastname = documentSnapshot.getString(LAST_NAME);
                        String phonenumber = documentSnapshot.getString(PHONE_NUMBER);
                        String emailid = documentSnapshot.getString(EMAIL_ID);
                        String rcnumber = documentSnapshot.getString(RC_NUMBER);

                        EditText mFirstname = (EditText) findViewById(R.id.first_name_txt_profile_setup);
                        EditText mLastname = (EditText) findViewById(R.id.last_name_txt_profile_setup);
                        EditText mPhonenumber = (EditText) findViewById(R.id.phone_number_profile_setup);
                        EditText mEmailid = (EditText) findViewById(R.id.email_id_txt_profile_setup);
                        EditText mRcnumber = (EditText) findViewById(R.id.rc_number_txt_profile_setup);

                        mFirstname.setText(firstname);
                        mLastname.setText(lastname);
                        mPhonenumber.setText(phonenumber);
                        mEmailid.setText(emailid);
                        mRcnumber.setText(rcnumber);
                    } else if (e != null) {
                        Log.w(TAG, "Got an Exception!", e);
                    }

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
    }

    public void onUpdateUserProfileData(View View) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


            assert firebaseUser != null;
            final String email_firebaseauth = (firebaseUser.getEmail());
            final String phonenumber_firebaseauth = (firebaseUser.getPhoneNumber());


            EditText mFirstName = (EditText) findViewById(R.id.first_name_txt_profile_setup);
            EditText mLastName = (EditText) findViewById(R.id.last_name_txt_profile_setup);
            EditText mPhoneNumber = (EditText) findViewById(R.id.phone_number_profile_setup);
            EditText mEmailId = (EditText) findViewById(R.id.email_id_txt_profile_setup);
            EditText mRCNumber = (EditText) findViewById(R.id.rc_number_txt_profile_setup);
            TextView mFeedback = (TextView) findViewById(R.id.feedback_txt_profile_setup);

            String FirstName = mFirstName.getText().toString();
            String LastName = mLastName.getText().toString();
            String PhoneNumber = mPhoneNumber.getText().toString();
            String EmailID = mEmailId.getText().toString();
            String RCNumber = mRCNumber.getText().toString();

            if (FirstName.isEmpty()
                    || LastName.isEmpty()
                    || PhoneNumber.isEmpty()
                    || EmailID.isEmpty()
                    || RCNumber.isEmpty()) {

                Toast.makeText(getApplicationContext(), "Please fill in all details.", Toast.LENGTH_LONG).show();
                //mFeedback.setText("Please fill in all details.");
                //mFeedback.setVisibility(View.VISIBLE);
            } else {
                Map<String, Object> datatosave = new HashMap<String, Object>();
                datatosave.put(FIRST_NAME, FirstName);
                datatosave.put(LAST_NAME, LastName);
                datatosave.put(PHONE_NUMBER, PhoneNumber);
                datatosave.put(EMAIL_ID, EmailID);
                datatosave.put(RC_NUMBER, RCNumber);

                if (phonenumber_firebaseauth == null) {
                    mUserInfo.collection("User Info").document(email_firebaseauth)
                            .set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document has been saved!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                } else if (email_firebaseauth == null) {
                    mUserInfo.collection("User Info").document(phonenumber_firebaseauth)
                            .set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document has been saved!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }


                Intent homeIntent = new Intent(ProfileSetupActivity.this, NavDrawerActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);

            }
        }

}
