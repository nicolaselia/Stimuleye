package com.theupswing.stimuleye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayData extends AppCompatActivity {
    /**
     * This is the section of the app that shows the list of visited places.
     * To edit the list items, you must edit the location_item.xml file and the LocationItem java file.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        final ArrayList<DataItem> locationItems = loadData();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final DataAdapter adapter = new DataAdapter(locationItems);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<DataItem> loadData() {
        ArrayList<DataItem> dataItems = new ArrayList<>();
        DatabaseHelper database = new DatabaseHelper(this);
        Cursor cursor = database.getAllData();

        int time;
        double diameter;
        int flash;
        while (cursor.moveToNext()) {
            time = cursor.getInt(cursor.getColumnIndex(database.COL_TIME));
            diameter = cursor.getDouble(cursor.getColumnIndex(database.COL_DIAM));
            flash = cursor.getInt(cursor.getColumnIndex(database.COL_FLASH));

            diameter = Math.round(diameter * 100.0) / 100.0; // rounds to two decimal places

            dataItems.add(new DataItem(time, diameter, flash));
        }

        return dataItems;
    }

}

