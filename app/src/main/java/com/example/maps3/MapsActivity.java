package com.example.maps3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient mFused;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private ActivityMapsBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        init();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        binding.enableGPS.setOnClickListener(v -> {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                x();

            }
        });

    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        mMap.setOnMapLoadedCallback(() -> {
            LatLng customMarkerLocationOne = new LatLng(28.583911, 77.319116);
            LatLng customMarkerLocationTwo = new LatLng(28.583078, 77.313744);
            LatLng customMarkerLocationThree = new LatLng(28.580903, 77.317408);
            LatLng customMarkerLocationFour = new LatLng(28.580108, 77.315271);
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).icon(BitmapDescriptorFactory.fromBitmap(Utils.createCustomMarker(MapsActivity.this, R.drawable.ic_launcher_background, "Manish")))).setTitle("iPragmatech Solutions Pvt Lmt");
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationTwo).icon(BitmapDescriptorFactory.fromBitmap(Utils.createCustomMarker(MapsActivity.this, R.drawable.ic_launcher_background, "Narender")))).setTitle("Hotel Nirulas Noida");
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationThree).icon(BitmapDescriptorFactory.fromBitmap(Utils.createCustomMarker(MapsActivity.this, R.drawable.ic_launcher_background, "Neha")))).setTitle("Acha Khao Acha Khilao");
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationFour).icon(BitmapDescriptorFactory.fromBitmap(Utils.createCustomMarker(MapsActivity.this, R.drawable.ic_launcher_background, "Nupur")))).setTitle("Subway Sector 16 Noida");
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(customMarkerLocationOne);
            builder.include(customMarkerLocationThree);
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            mMap.moveCamera(cu);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        });

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions().position(latLng).title("mohamed").snippet("allam"));
            Toast.makeText(MapsActivity.this, latLng.latitude + "\n" + latLng.longitude, Toast.LENGTH_SHORT).show();
        });
    }

    private void getCurrentLocation() {
        mFused = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> location = mFused.getLastLocation();
            location.addOnCompleteListener((OnCompleteListener<Location>) task -> {
                if (task.isSuccessful()) {
                    Location location1 = (Location) task.getResult();
                    if (location1 != null) {
                        movingCamera(new LatLng(location1.getLatitude(), location1.getLongitude()), 15f);
                    }
                }
            });


        } catch (SecurityException e) {
            e.fillInStackTrace();
        }
    }

    private void movingCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void x() {
        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double lng = locationGPS.getLongitude();
                movingCamera(new LatLng(lat, lng), 15f);
                Log.d("bbb", "x: " + "lat " + lat + "  lng  " + lng);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}