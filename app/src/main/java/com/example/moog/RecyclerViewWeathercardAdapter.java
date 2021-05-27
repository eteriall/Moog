package com.example.moog;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecyclerViewWeathercardAdapter extends RecyclerView.Adapter<RecyclerViewWeathercardAdapter.MyView> {

    // List with String type
    private List<String> list;

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView
            extends RecyclerView.ViewHolder {
        TextView city, time, temp,
                humidity, wind_value,
                pressure_value, weather_description,
                humidity_value, weather_comment, last_updated_time;
        ImageView weather_image;

        // Locate all elements

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view) {
            super(view);

            // initialise TextViews
            {
                weather_comment = view.findViewById(R.id.weather_comment);
                city = view.findViewById(R.id.city);
                weather_image = view.findViewById(R.id.weather_image);
                temp = view.findViewById(R.id.temperature);
                pressure_value = view.findViewById(R.id.pressure_value);
                wind_value = view.findViewById(R.id.wind_value);
                time = view.findViewById(R.id.time);
                weather_description = view.findViewById(R.id.weather_description);
                humidity_value = view.findViewById(R.id.humidity_value);
                last_updated_time = view.findViewById(R.id.last_updated_time);
            }
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public RecyclerViewWeathercardAdapter(List<String> horizontalList) {
        this.list = horizontalList;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_row,
                        parent,
                        false);

        // return itemView
        return new MyView(itemView);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final MyView holder,
                                 final int position) {
        setFadeAnimation(holder.itemView);
        holder.weather_comment.setText(list.get(position));
        WeatherApiInteraction.updateCard(holder, list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setDuration(300);
        view.startAnimation(anim2);
    }
}