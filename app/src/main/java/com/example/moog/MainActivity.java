package com.example.moog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String current_city = "";
    double lat, lon;
    Boolean DEBUG = false;

    TextView city, pressure_value, wind_value, humidity_value;
    RecyclerView recyclerView;
    ArrayList<String> source;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerViewWeathercardAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    public static Map<String, Drawable> imgs = new HashMap<String, Drawable>();

    protected void loadWeatherImages() {
        imgs.put("01d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._01d));
        imgs.put("02d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._02d));
        imgs.put("03d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._03d));
        imgs.put("04d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._04d));
        imgs.put("09d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._09d));
        imgs.put("10d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._10d));
        imgs.put("11d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._11d));
        imgs.put("13d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._13d));
        imgs.put("50d", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._50d));
        imgs.put("01n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._01n));
        imgs.put("02n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._02n));
        imgs.put("03n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._03n));
        imgs.put("04n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._04n));
        imgs.put("09n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._09n));
        imgs.put("10n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._10n));
        imgs.put("11n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._11n));
        imgs.put("13n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._13n));
        imgs.put("50n", AppCompatResources.getDrawable(getApplicationContext(), R.drawable._50n));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Remove header from application
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set layout
        setContentView(R.layout.activity_main);

        loadWeatherImages();

        pressure_value = findViewById(R.id.pressure_value);
        wind_value = findViewById(R.id.wind_value);
        humidity_value = findViewById(R.id.humidity_value);


        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("", "Location permission isn't provided");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        city = findViewById(R.id.city);

        Locale.setDefault(new Locale("ru"));

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        recyclerView
                = (RecyclerView) findViewById(
                R.id.weather_cards_recycler);
        RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext());
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        adapter = new RecyclerViewWeathercardAdapter(source);
        HorizontalLayout
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);

        // Set adapter on recycler view
        recyclerView.setAdapter(adapter);
        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(Integer.MAX_VALUE / 2);
    }

    public void AddItemsToRecyclerViewArrayList() {
        // Adding items to ArrayList
        source = new ArrayList<>();
        source.add("Moscow");
        source.add("Monaco");
        source.add("New Jersey");
        source.add("Paris");
    }

    public void nextCard(View v) {
        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(Integer.MAX_VALUE / 2 + 1);
    }

    public void previousCard(View v) {
        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(Integer.MAX_VALUE / 2 - 1);
    }

    public static String formatDate(long date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }


    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    double toCelcius(double Kelvin) {
        return (Kelvin - 273.15);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            if (DEBUG) {
                Toast.makeText(
                        getApplicationContext(),
                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                                + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                Log.v("", "Longitude: " + loc.getLongitude());
                Log.v("", "Latitude: " + loc.getLatitude());
            }

            lat = loc.getLatitude();
            lon = loc.getLongitude();

            // Use geocoder to get city name
            String cityName = null;
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    return;
                }
            }
            current_city = cityName;
            city.setText(cityName.toUpperCase());
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}

