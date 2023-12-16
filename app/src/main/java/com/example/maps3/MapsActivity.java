package com.example.maps3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.example.maps3.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int FineLocationRequestCode = 1;
    private LocationManager locationManager;
    private ActivityMapsBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        init();
        binding.enableGPS.setOnClickListener(v -> enableGPS());

    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            requestPermission(FINE_LOCATION);
            requestPermission(COARSE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentFusedLocation();
        mMap.setOnMapLoadedCallback(this::setMapMarkers);
        enabledMyLocation();
        mMap.setOnMapClickListener(latLng -> {
            LocationModel currentLocation = new LocationModel(latLng.latitude, latLng.longitude, R.drawable.ic_location1, "Current Location", "Current Location Sub Title");
            setMarker(currentLocation);
        });
    }
    private void getCurrentFusedLocation() {
        FusedLocationProviderClient mFused = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> location = mFused.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location1 = task.getResult();
                    if (location1 != null) {
                        movingCamera(new LatLng(location1.getLatitude(), location1.getLongitude()), 15f);
                    }
                }
            });
        } catch (SecurityException e) {
            e.fillInStackTrace();
        }
    }
    private void setMapMarkers() {
        List<LocationModel> locations = new ArrayList<>();
        locations.add(new LocationModel(28.583911, 77.319116, R.drawable.ic_location1, "Position1", "Position1 Sub Title"));
        locations.add(new LocationModel(28.583078, 77.313744, R.drawable.ic_location2, "Position2", "Position2 Sub Title"));
        locations.add(new LocationModel(28.580903, 77.317408, R.drawable.ic_location3, "Position3", "Position3 Sub Title"));
        locations.add(new LocationModel(28.580108, 77.315271, R.drawable.ic_location4, "Position4", "Position4 Sub Title"));
        for (int i = 0; i < locations.size(); i++) {
            setMarker(locations.get(i));
        }
        setLatLngBounds(locations);
    }
    private void enabledMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    private void setLatLngBounds(List<LocationModel> locations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < locations.size(); i++) {
            builder.include(getLatLng(locations.get(i)));
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    private void setMarker(LocationModel location) {
        mMap.addMarker(new MarkerOptions().position(getLatLng(location)).icon(Utils.createIcon(this, location.getIcon(), location.getTitle()))).setTitle(location.getSubTitle());
    }

    private LatLng getLatLng(LocationModel location) {
        return new LatLng(location.getLat(), location.getLng());
    }

    private void movingCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, FineLocationRequestCode);
    }

    private void enableGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            openGPSPermissionDialog();
        } else {
            getLastKnownLocation();
        }
    }

    private void openGPSPermissionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", (dialog, which) -> {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FineLocationRequestCode);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double lng = locationGPS.getLongitude();
                movingCamera(new LatLng(lat, lng), 15f);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}