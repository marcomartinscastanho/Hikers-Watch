package com.martinscastanho.marco.hikerswatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView addressTextView;
    Integer ADDRESS_MAX_RESULTS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeTextView = findViewById(R.id.latituteTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        accuracyTextView = findViewById(R.id.accuracyTextView);
        altitudeTextView = findViewById(R.id.altitudeTextView);
        addressTextView = findViewById(R.id.addressTextView);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setTextViews(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation != null){
                    setTextViews(lastKnownLocation);
                }
            }
        }
    }

    public void setTextViews(Location location){
        latitudeTextView.setText(String.format("Latitude: %f", location.getLatitude()));
        longitudeTextView.setText(String.format("Longitude: %f", location.getLongitude()));
        accuracyTextView.setText(String.format("Accuracy: %.1f", location.getAccuracy()));
        altitudeTextView.setText(String.format("Altitude: %.1f", location.getAltitude()));
        String addresses = "Address:\r\n";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), ADDRESS_MAX_RESULTS);
            if(addressList != null && !addressList.isEmpty()){
                for(int i=0; i<addressList.size(); i++){
                    String address="";
                    if(addressList.get(i).getThoroughfare() != null){
                        address += addressList.get(i).getThoroughfare();
                    }
                    if(addressList.get(i).getFeatureName() != null && addressList.get(i).getFeatureName() != addressList.get(i).getThoroughfare()){
                        if(!address.isEmpty()){
                            address += ", ";
                        }
                        address += addressList.get(i).getFeatureName();
                    }
                    if(addressList.get(i).getLocality() != null && addressList.get(i).getLocality() != addressList.get(i).getFeatureName()){
                        if(!address.isEmpty()){
                            address += ", ";
                        }
                        address += addressList.get(i).getLocality();
                    }
                    if(addressList.get(i).getAdminArea() != null && addressList.get(i).getAdminArea() != addressList.get(i).getLocality()){
                        if(!address.isEmpty()){
                            address += ", ";
                        }
                        address += addressList.get(i).getAdminArea();
                    }
                    if(addressList.get(i).getCountryName() != null && addressList.get(i).getCountryName() != addressList.get(i).getAdminArea()){
                        if(!address.isEmpty()){
                            address += ", ";
                        }
                        address += addressList.get(i).getCountryName();
                    }
                    addresses += "- " + address + ";\r\n";
                }
            } else {
                addresses += "We couldn't find any address near you.";
            }
            addressTextView.setText(addresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
