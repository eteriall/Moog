package com.example.moog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.*;
import com.google.gson.*;
import com.example.moog.R;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String CITY = "Москва";
    String API = "294d139869b6097d30eeba8c6a5f351c";
    public static String BaseUrl = "http://api.openweathermap.org/";
    ImageView weather_image;
    TextView city, time, temp, humidity, wind_value,
            pressure_value, weather_description, humidity_value,
            last_updated_time;
    String current_city = "";
    double lat;
    double lon;
    Boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Remove header from application
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set layout
        setContentView(R.layout.activity_main);

        // Locate all elements
        {
            city = findViewById(R.id.city);
            weather_image = findViewById(R.id.weather_image);
            temp = findViewById(R.id.temperature);
            pressure_value = findViewById(R.id.pressure_value);
            wind_value = findViewById(R.id.wind_value);
            time = findViewById(R.id.time);
            weather_description = findViewById(R.id.weather_description);
            humidity_value = findViewById(R.id.humidity_value);
            last_updated_time = findViewById(R.id.last_updated_time);
        }


        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("", "Location permission isn't provided");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }

        Locale.setDefault(Locale.CHINA);

        // Get user location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    public static String formatDate(long date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    void getCurrentData() {

        // Sending Request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(String.valueOf(lat), String.valueOf(lon), API, Locale.getDefault().getLanguage());
        call.enqueue(new Callback<WeatherResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    int temperature_n = (int) toCelcius(weatherResponse.main.temp);
                    int pressure_n = (int) weatherResponse.main.pressure;

                    // Updating labels with new data
                    temp.setText(temperature_n + "º");
                    pressure_value.setText(String.valueOf(pressure_n));
                    humidity_value.setText(String.valueOf((int) weatherResponse.main.humidity) + "%");
                    wind_value.setText((int) weatherResponse.wind.speed + " км/ч");
                    weather_description.setText(capitalize(weatherResponse.weather.get(0).description));

                    // Update "last updated" label
                    Calendar cal = Calendar.getInstance();

                    Integer year = cal.get(Calendar.YEAR);
                    Integer month_i = cal.get(Calendar.MONTH) + 1;
                    Integer day_i = cal.get(Calendar.DAY_OF_MONTH) + 1;

                    String date =
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
                                    .format(LocalDate.of(year, month_i, day_i));

                    // Capitalize
                    date = capitalize(date);

                    last_updated_time.setText(date);

                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            city.setText("");

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
            getCurrentData();
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

