package com.example.hamrahweatherchalleng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamrahweatherchalleng.api.MainAPI;
import com.example.hamrahweatherchalleng.api.RetrofitInstance;
import com.example.hamrahweatherchalleng.models.ResponseMain;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textViewStatus;
    private GoogleMap gMap;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private MainAPI mainAPI;
    private Double weatherStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.txt_weather_status);
        weatherStatus = 0.0 ;



//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();


        textViewStatus.setOnClickListener(view -> {
            textViewStatus.setText(String.format(getResources().getString(R.string.weather_selected_status),
                    new DecimalFormat("##.##").format(weatherStatus)));
        });

    }



    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MainActivity.this);


            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


//        gMap = googleMap;
//        LatLng myLocation = new LatLng(34.016774, 58.168308);
//        gMap.addMarker(new
//                MarkerOptions().position(myLocation).title("Tutorialspoint.com"));
//        gMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Click Me For Weather Details!!!");

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        googleMap.addMarker(markerOptions);

//        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Intent intent = new Intent(MainActivity.this,ActivityInfoWindow.class);
//                startActivity(intent);
//            }
//        });

        weather();

        googleMap.setOnMapClickListener(view -> {
            textViewStatus.setText(String.format(getResources().getString(R.string.weather_selected_status),
                    new DecimalFormat("##.##").format(weatherStatus)));
        });


    }



    private void weather() {

        mainAPI = RetrofitInstance.getRetrofit().create(MainAPI.class);

        Call<ResponseMain> call = mainAPI.getWeatherData(currentLocation.getLatitude() , currentLocation.getLongitude(),getResources().getString(R.string.open_weather_maps_app_id));
        call.enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful())
                {
                    if (response.body() != null) {
                        weatherStatus = response.body().getMain().getTemp()-273.15;//kelvin to celsius
                    }
                }
                else {
                    Log.e("Weather-app", "error: " );
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Log.e("Weather-app", "onFailure: "+ t.getMessage() );
            }
        });

    }
}