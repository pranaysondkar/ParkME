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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class BookingActiivity extends AppCompatActivity {

    FirebaseFirestore mLocation = FirebaseFirestore.getInstance();
    private static final String TAG = "BookingActiivity";
    String locID;
    String latlngName;
    String Title;
    String Address;
    GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_actiivity);
        loadSlots();
    }

    private void loadSlots() {
        TextView mLocationTitle = (TextView) findViewById(R.id.location_title);
        final TextView mLocationAddres = (TextView) findViewById(R.id.location_address);
        Log.d(TAG, "Load Slots");
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String location_data = bundle.getString("locFromMap");
        mLocationTitle.setText(location_data);
        mLocation.collection("Location").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                locID = document.getId();
                                latlngName = document.getString("latlngName");
                                Title = document.getString("Title");
                                Address = document.getString("Address");
                                geoPoint = document.getGeoPoint("geoLatLng");
                                //Log.d(TAG,"Location title from firebase:" +locID);
                                assert location_data != null;
                                if (location_data.equals(locID)){
                                    Log.d(TAG,"Location same hai");
                                    Log.d(TAG, "location name" + locID);
                                    Log.d(TAG,"Title:"+ latlngName);
                                    Log.d(TAG,"Address:" + Address);
                                    mLocationAddres.setText(Address);
                                    Log.d(TAG, "geoPoint:"+ geoPoint);
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
        });
    }



}
