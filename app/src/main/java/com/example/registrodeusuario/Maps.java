package com.example.registrodeusuario;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.database.Cursor;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private UserDatabaseHelper dbHelper;
    private Spinner spinnerLocalities;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.maps_activity);

        dbHelper = new UserDatabaseHelper(this);
        spinnerLocalities = findViewById(R.id.spinnerLocalities);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Load localities into the Spinner
        loadLocalities();
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
        Map<String, Integer> localities = dbHelper.getLocalities();

        // Add markers to the map
        for (Map.Entry<String, Integer> entry : localities.entrySet()) {
            String locality = entry.getKey();
            int count = entry.getValue();

            // Convert locality to LatLng
            LatLng latLng = getLatLngFromLocality(locality);

            if (latLng != null) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(locality + " (" + count + ")"));
            }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Current Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    private void loadLocalities() {
        ArrayList<String> localitiesList = new ArrayList<>();
        localitiesList.add("Localities"); // Add default item

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT DISTINCT " + UserDatabaseHelper.COLUMN_PROPOSAL_LOCALITY + " FROM " + UserDatabaseHelper.TABLE_PROPOSALS, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String locality = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_LOCALITY));
                localitiesList.add(locality);
            }
            cursor.close();
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
}