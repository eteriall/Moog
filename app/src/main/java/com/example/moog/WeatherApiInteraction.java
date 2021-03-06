package com.example.moog;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApiInteraction {
    public static String API = "294d139869b6097d30eeba8c6a5f351c";
    public static String BaseUrl = "http://api.openweathermap.org/";

    public static void updateBottom(View bottomcard, String q) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(q, API, Locale.getDefault().getLanguage());

        call.enqueue(new Callback<WeatherResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    TextView pressure_value = bottomcard.findViewById(R.id.pressure_value);
                    TextView humidity_value = bottomcard.findViewById(R.id.humidity_value);
                    TextView wind_value = bottomcard.findViewById(R.id.wind_value);
                    pressure_value.setText(String.valueOf((int) weatherResponse.main.pressure));
                    humidity_value.setText(String.valueOf((int) weatherResponse.main.humidity) + "%");
                    wind_value.setText(String.valueOf((int) weatherResponse.wind.speed) + " ????/??");
                    Log.w("", String.valueOf(weatherResponse.main.pressure));
                } else {
                    Log.e("ERROR", String.valueOf(response.code()) + " " + q);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.w("", t.getMessage());
            }
        });
    }


    static double toCelcius(double Kelvin) {
        return (Kelvin - 273.15);
    }

    static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void _updateCard(final RecyclerViewWeathercardAdapter.WeatherCardView h, WeatherResponse weatherResponse) {
        int temperature_n = (int) toCelcius(weatherResponse.main.temp);
        int pressure_n = (int) weatherResponse.main.pressure;

        // Updating labels with new data
        h.temp.setText(temperature_n + "??");
        /* h.pressure_value.setText(String.valueOf(pressure_n));
        h.humidity_value.setText(String.valueOf((int) weatherResponse.main.humidity) + "%");
        h.wind_value.setText((int) weatherResponse.wind.speed + " ????/??"); */
        h.weather_description.setText(capitalize(weatherResponse.weather.get(0).description));

        // Update "last updated" label
        Calendar cal = Calendar.getInstance();

        Integer year = cal.get(Calendar.YEAR);
        Integer month_i = cal.get(Calendar.MONTH) + 1;
        Integer day_i = cal.get(Calendar.DAY_OF_MONTH) + 1;

        // Todo: put weatherResponse.weather.get(0).icon in weather_image
        h.weather_image.setImageDrawable(MainActivity.imgs.get(weatherResponse.weather.get(0).icon));
        String date =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
                        .format(LocalDate.of(year, month_i, day_i));

        // Capitalize
        date = capitalize(date);

        h.last_updated_time.setText(date);

    }


    public static void updateCard(RecyclerViewWeathercardAdapter.WeatherCardView h, String q) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(q, API, Locale.getDefault().getLanguage());


        // Getting response
        call.enqueue(new Callback<WeatherResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    _updateCard(h, weatherResponse);
                } else {
                    Log.w("ERROR", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.w("", t.getMessage());
            }
        });
    }
}
