package com.example.moog;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewWeathercardAdapter extends RecyclerView.Adapter<RecyclerViewWeathercardAdapter.WeatherCardView> {

    int p1, p2;

    private List<String> list;

    public class WeatherCardView
            extends RecyclerView.ViewHolder {
        TextView city, time, temp,
                humidity, wind_value,
                pressure_value, weather_description,
                humidity_value, weather_comment, last_updated_time;
        ImageView weather_image;

        // Locate all elements

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public WeatherCardView(View view) {
            super(view);

            // initialise TextViews
            {
                weather_comment = view.findViewById(R.id.weather_comment);
                city = view.findViewById(R.id.city);
                weather_image = view.findViewById(R.id.weather_image);
                temp = view.findViewById(R.id.temperature);
                time = view.findViewById(R.id.time);
                weather_description = view.findViewById(R.id.weather_description);
                last_updated_time = view.findViewById(R.id.last_updated_time);
            }
        }

    }

    public RecyclerViewWeathercardAdapter(List<String> horizontalList) {
        this.list = horizontalList;
    }


    @Override
    public WeatherCardView onCreateViewHolder(ViewGroup parent,
                                              int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_row,
                        parent,
                        false);

        // return itemView
        return new WeatherCardView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final WeatherCardView holder,
                                 final int position) {
        setFadeAnimation(holder.itemView);
        holder.weather_comment.setText(list.get(position % list.size()));
        WeatherApiInteraction.updateCard(holder, list.get(position % list.size()));
        int[] originalPos = new int[2];
        holder.itemView.getLocationInWindow(originalPos);
        int x = originalPos[0];
        int y = originalPos[1];
        Log.i("", String.valueOf(x));
    }


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setDuration(300);
        view.startAnimation(anim2);


    }
}