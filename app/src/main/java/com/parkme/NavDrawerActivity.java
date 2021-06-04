package com.parkme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;


//GMAPS//////////////////////////////////////
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NavDrawerActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseFirestore mLocation = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 133;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 16;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AppBarConfiguration mAppBarConfiguration;
    //private SearchView searchView;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //Declare HashMap to store mapping of marker to Activity
    HashMap<String, String> markerMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Places.initialize(getApplicationContext(), "AIzaSyC2mdjNibL5ijktc2a4-hM1ju3ZeAlHh3w");
        //PlacesClient placesClient = Places.createClient(this);
        //setSupportActionBar(toolbar);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search_text);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        //AutoCompleteSearch();
        getLocationPermission();


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/    //floating button
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //GOOGLE MAPS//////////////////////////////
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



        /*searchView = findViewById(R.id.search_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(NavDrawerActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/                                                                                           //Search View

        mapFragment.getMapAsync(this);


    }

    /*private void AutoCompleteSearch() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyC2mdjNibL5ijktc2a4-hM1ju3ZeAlHh3w");
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }*/                                                                                             //AutoCompleteSearchQuery


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(NavDrawerActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title)  {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            //mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;


        //Custom MAP STYLEEEEE kya bolta bantai
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapActivity", "Can't find style. Error: ", e);
        }
        // Custom MAP STYLE ends here na bachi


        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            
            init();
        }


        retainLocation();

        /* Add a marker in Sydney and move the camera
        LatLng myHome = new LatLng(19.215019,72.963857);
        mMap.addMarker(new MarkerOptions()
                .position(myHome)
                .title("Marker at my hood")
                .snippet("Apun reheta yaha")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_mali)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHome,18f));*/                                                                                        //Sydney map example

        //map objects test
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        //String currentlocation = mMap.getMyLocation().toString();
        //Toast.makeText(getApplicationContext(),currentlocation,Toast.LENGTH_LONG).show();

        /*
        double defaultLat = 19.204536;
        double defaultLng = 72.968413;
        LatLng defaultLatLng = new LatLng(defaultLat, defaultLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 18f));
        */                                                                                                  //Default map camera location



    }
/*    private void parkingLocations() {

        LatLng pokharanRoad = new LatLng(19.209083, 72.963189);
        Marker pokharanRoadMkr = mMap.addMarker(new MarkerOptions().position(pokharanRoad).title("Pokharan Rd Number 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        String id1 = pokharanRoadMkr.getId();
        markerMap.put(id1, "action_one");

        LatLng samtaNagar = new LatLng(19.208329, 72.963745);
        Marker samtaNagarMkr = mMap.addMarker(new MarkerOptions().position(samtaNagar).title("Samta Nagar").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        String idTwo = samtaNagarMkr.getId();
        markerMap.put(idTwo, "action_two");

        LatLng vartakNagar0 = new LatLng(19.210101, 72.961987);
        Marker vartakNagar0Mkr = mMap.addMarker(new MarkerOptions().position(vartakNagar0).title("Vartak Nagar 0").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        String idThree = vartakNagar0Mkr.getId();
        markerMap.put(idThree, "action_three");

        LatLng buildingNo3 = new LatLng(19.212048, 72.960116);
        Marker buildingNo3Mkr = mMap.addMarker(new MarkerOptions().position(buildingNo3).title("Building No 3").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        String idFour = buildingNo3Mkr.getId();
        markerMap.put(idFour, "action_four");

        LatLng vartakNagar1 = new LatLng(19.212600, 72.959692);
        Marker vartakNagar1Mkr = mMap.addMarker(new MarkerOptions().position(vartakNagar1).title("Vartak Nagar 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        String idFive = vartakNagar1Mkr.getId();
        markerMap.put(idFive, "action_five");

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String actionId = markerMap.get(marker.getId());

                if (actionId.equals("action_one")) {
                    Intent i = new Intent(NavDrawerActivity.this, BookingActiivity.class);
                    startActivity(i);
                } else if (actionId.equals("action_two")) {
                    Intent i = new Intent(NavDrawerActivity.this, BookingActiivity.class);
                    startActivity(i);
                }
                else if (actionId.equals("action_three")) {
                    Intent i = new Intent(NavDrawerActivity.this, BookingActiivity.class);
                    startActivity(i);
                }
                else if (actionId.equals("action_four")) {
                    Intent i = new Intent(NavDrawerActivity.this, BookingActiivity.class);
                    startActivity(i);
                }
                else if (actionId.equals("action_five")) {
                    Intent i = new Intent(NavDrawerActivity.this, BookingActiivity.class);
                    startActivity(i);
                }
            }
        });


    }*/                                                                                                     //Parking Location default
    private void retainLocation() {
        Log.d(TAG, "retainLocation");
        mLocation.collection("Location")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int markerCount = 0;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                markerCount++;
                                Log.d(TAG,"Total Number of Marker:"+markerCount);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String locID = document.getId();
                                String latlngName = document.getString("latlngName");
                                String Title = document.getString("Title");
                                GeoPoint geoPoint = document.getGeoPoint("geoLatLng");
                                assert geoPoint != null;
                                double lat = geoPoint.getLatitude();
                                double lng = geoPoint.getLongitude();
                                LatLng latLng = new LatLng(lat,lng);
                                Log.d(TAG,"location ID:"+ locID +", latlngName:" + latlngName +", Title:" +Title+ "LatLng:"+latLng);
                                //till here i have count
                                final Marker[] allMarkers = new Marker[markerCount];
                                //mMap.clear();
                                for(int i = 0; i< markerCount; i++) {
                                    allMarkers[i] = mMap.addMarker(new MarkerOptions().position(latLng).title(Title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                     mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            String markerClicked = marker.getTitle();
                                            Log.d(TAG,"Marker Clicked:"+markerClicked);
                                            Intent i = new Intent(getApplicationContext(), BookingActiivity.class);
                                            i.putExtra("locFromMap", markerClicked);
                                            startActivity(i);


                                        }
                                    });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }




    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;

            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(NavDrawerActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: No Result " + e.getMessage() );
            Toast.makeText(this,"No Result",Toast.LENGTH_LONG).show();
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));

        }
        else Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            //googleMap.setMyLocationEnabled(true);
            //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            //Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;

                }
            }
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }*/

    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            sendUserToLogin();


        }
    }



    private void sendUserToLogin() {
        Intent loginIntent = new Intent(NavDrawerActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
