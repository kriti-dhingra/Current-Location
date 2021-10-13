 package com.example.currentlocation;

 import android.Manifest;
 import android.annotation.SuppressLint;
 import android.content.Context;
 import android.content.Intent;
 import android.content.pm.PackageManager;
 import android.location.Location;
 import android.location.LocationManager;
 import android.os.Bundle;
 import android.os.Looper;
 import android.provider.Settings;
 import android.view.View;
 import android.widget.Button;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.core.app.ActivityCompat;

 import com.google.android.gms.location.FusedLocationProviderClient;
 import com.google.android.gms.location.LocationCallback;
 import com.google.android.gms.location.LocationRequest;
 import com.google.android.gms.location.LocationResult;
 import com.google.android.gms.location.LocationServices;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;

 public class MainActivity extends AppCompatActivity {
    //Initialize variable
    Button btlocation;
    TextView tvlatitude,tvlongitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        btlocation = findViewById(R.id.bt_location);
        tvlatitude = findViewById(R.id.tv_latitude);
        tvlongitude = findViewById(R.id.tv_longitutde);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this);

        btlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check condition
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED){
                    //when both permission are granted
                    //call method
                    getCurrentLocation();
                }
                else{
                    //when permission is not granted
                    // Request Permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                            ,100);
                }
            }
        });
    }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1]
         == PackageManager.PERMISSION_GRANTED)){
             getCurrentLocation();
         }else{
             Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
         }
     }

     @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        //Initial Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );

        //check condition
        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)){
            //when location service is enabled
            //get last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location = task.getResult();
                    //check condition
                    if(location != null){
                        //when location is not null
                        //set long and latitude
                        tvlatitude.setText(String.valueOf(location.getLatitude()));
                        tvlongitude.setText(String.valueOf(location.getLongitude()));
                    } else{
                        //when location result is null
                        // Initialize request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        //Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                //super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                tvlatitude.setText(String.valueOf(location1.getLatitude()));
                                tvlongitude.setText(String.valueOf(location1.getLongitude()));
                            }
                        };
                        //requested location update
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                }
            });
        }else{
            //when location service is not enabled
            // open location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}