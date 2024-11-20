package com.example.registrodeusuario;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private UserDatabaseHelper dbHelper;
    private Spinner spinnerLocalities;
    private TextView textViewDistance;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    private boolean bound = false;
    private GeoApiContext geoApiContext;
    private Map<String, LatLng> localityCoordinates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);

        dbHelper = new UserDatabaseHelper(this);
        spinnerLocalities = findViewById(R.id.spinnerLocalities);
        textViewDistance = findViewById(R.id.textViewDistance);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize GeoApiContext
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();

        // Load localities into the Spinner
        loadLocalities();

    }





    public LatLng UbicationActually() {
        double latitude = 4.7465957678842114;
        double longitude = -74.03092034568157;
        return new LatLng(latitude, longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the coordinates for Bogotá
        LatLng bogota = new LatLng(4.7110, -74.0721);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 10));

        // Enable the location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }

        // Get localities from the database
        Map<String, Integer> localities = dbHelper.getLocalitiesWithStatus(1);

        // Add markers to the map
        for (Map.Entry<String, Integer> entry : localities.entrySet()) {
            String locality = entry.getKey();
            int count = entry.getValue();

            // Convert locality to LatLng
            LatLng latLng = getLatLngFromLocality(locality);

            if (latLng != null) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(locality)
                        .snippet("Proposals: " + count)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                                }
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }



    private void loadLocalities() {
        ArrayList<String> localitiesList = new ArrayList<>();
        localitiesList.add("Localities"); // Add default item

        // Get localities with proposal_status = 1
        Map<String, Integer> localities = dbHelper.getLocalitiesWithStatus(1);

        for (String locality : localities.keySet()) {
            localitiesList.add(locality);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, localitiesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalities.setAdapter(adapter);
    }

    private LatLng getLatLngFromLocality(String locality) {
        Map<String, LatLng> localityCoordinates = new HashMap<>();
        localityCoordinates.put("Bogotá", new LatLng(4.7110, -74.0721));

        return localityCoordinates.get(locality);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



}