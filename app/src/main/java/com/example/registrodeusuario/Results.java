package com.example.registrodeusuario;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class Results extends AppCompatActivity {

    private BarChart barChart;
    private HorizontalBarChart horizontalBarChart;
    private UserDatabaseHelper dbHelper;
    private static final String TAG = "Results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        barChart = findViewById(R.id.barChart);
        horizontalBarChart = findViewById(R.id.horizontalBarChart);
        dbHelper = new UserDatabaseHelper(this);

        try {
            setupBarChart();
            setupHorizontalBarChart();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up charts", e);
        }
    }

    private void setupBarChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Cursor cursor = dbHelper.getProposalsWithStatus(1);
        if (cursor != null && cursor.moveToFirst()) {
            int index = 0;
            do {
                try {
                    String proposalTitle = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_TITLE));
                    int proposalVotes = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_VOTOS));
                    barEntries.add(new BarEntry(index, proposalVotes));
                    labels.add(proposalTitle);
                    index++;
                } catch (Exception e) {
                    Log.e(TAG, "Error processing cursor data", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e(TAG, "Cursor is null or empty");
        }

        Log.d(TAG, "Bar Entries: " + barEntries.size());
        Log.d(TAG, "Labels: " + labels.size());

        if (barEntries.isEmpty()) {
            Log.e(TAG, "No data to display in the chart");
            return;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Votos por Propuestas");
        barDataSet.setColor(Color.parseColor("#FFA500")); // Naranja

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(45f);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);

        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate();
    }

    private void setupHorizontalBarChart() {
        ArrayList<BarEntry> barEntriesDuracion = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        String[] durationCategories = {"0-1 años", "1-2 años", "2-3 años", "3-4 años", "4-5 años"};
        int[] proposalCounts = new int[durationCategories.length];

        Cursor cursor = dbHelper.getProposalsWithStatus(1);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_DURATION));
                    int durationIndex = getDurationIndex(duration);
                    if (durationIndex != -1) {
                        proposalCounts[durationIndex]++;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing cursor data", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e(TAG, "Cursor is null or empty");
        }

        for (int i = 0; i < proposalCounts.length; i++) {
            barEntriesDuracion.add(new BarEntry(i, proposalCounts[i]));
            labels.add(durationCategories[i]);
        }

        Log.d(TAG, "Horizontal Bar Entries: " + barEntriesDuracion.size());
        Log.d(TAG, "Labels: " + labels.size());

        if (barEntriesDuracion.isEmpty()) {
            Log.e(TAG, "No data to display in the chart");
            return;
        }

        BarDataSet barDataSetDuracion = new BarDataSet(barEntriesDuracion, "Cantidad de Propuestas por Duración");
        barDataSetDuracion.setColor(Color.parseColor("#FFA500")); // Naranja

        BarData barData = new BarData(barDataSetDuracion);
        horizontalBarChart.setData(barData);

        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(45f);

        YAxis yAxis = horizontalBarChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);

        horizontalBarChart.getAxisRight().setEnabled(false);
        horizontalBarChart.invalidate();
    }

    private int getDurationIndex(String duration) {
        switch (duration) {
            case "0-1 años":
                return 0;
            case "1-2 años":
                return 1;
            case "2-3 años":
                return 2;
            case "3-4 años":
                return 3;
            case "4-5 años":
                return 4;
            default:
                return -1;
        }
    }
}