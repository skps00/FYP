package com.calendar.fyp;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PieChartFragment extends Fragment {
    private DBHelper dbHelper;
    private LocalDateTime endDate = LocalDateTime.now().withHour(23).withSecond(59).withMinute(59).withNano(0);
    private LocalDateTime startDate = LocalDateTime.now().withHour(0).withSecond(0).withMinute(0).withNano(0).minusDays(7);
    private String TAG = "PieChart";
    private PieChart piechart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart, container, false);
        piechart = view.findViewById(R.id.ItisPieChart);
        piechart.setNoDataText(getString(R.string.no_data));
        piechart.setNoDataTextColor(Color.BLACK);
        Paint paint =  piechart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(40f);
        dbHelper = new DBHelper(getContext());
//        Log.v(TAG, "test Pie Chat 101: " + startDate.withHour(0).withMinute(0).withSecond(0).withSecond(0).getDayOfWeek());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText(R.string.select_your_date);

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        Button selectDate = view.findViewById(R.id.select_date_button);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getParentFragmentManager(), "datePicker");
            }
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 处理选择的日期范围
            startDate = Instant.ofEpochMilli(selection.first).atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(0).withSecond(0).withMinute(0).withNano(0);
            endDate= Instant.ofEpochMilli(selection.second).atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(23).withSecond(59).withMinute(59).withNano(0);
            UpdatePieChart(view);
            // ...
        });


        UpdatePieChart(view);
        return view;

    }


    public void UpdatePieChart(View view){

        ArrayList<PieEntry> entries = new ArrayList<>();
        List<String> categorys = Arrays.asList("work", "sport", "study", "diet", "entertainment", "other");

        for (String category:categorys) {
            int result = dbHelper.getEventsByDateTimeAndCalegory(startDate, endDate, category).size();
            if (result != 0){
                entries.add(new PieEntry(result,category));
            }
        }
        TextView Title = view.findViewById(R.id.TitleOfPieChart);
        Title.setText(getString(R.string.status) + startDate.toLocalDate() + getString(R.string.to) + endDate.toLocalDate());

        if (entries.size() == 0){
            piechart.setVisibility(View.GONE);
            view.findViewById(R.id.No_data).setVisibility(View.VISIBLE);
            return;
        }else {
            piechart.setVisibility(View.VISIBLE);
            view.findViewById(R.id.No_data).setVisibility(View.GONE);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Category");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setSelectionShift(5f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setDrawValues(true);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(11f);

        Legend legend = piechart.getLegend();
        legend.setEnabled(false);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextSize(12f);

        piechart.setDrawHoleEnabled(false);
        piechart.setData(pieData);
        piechart.setCenterTextColor(Color.WHITE);
        piechart.setDrawCenterText(true);
        piechart.setRotationEnabled(true);
        piechart.setUsePercentValues(true);
//        piechart.setExtraOffsets(0, 50, 0, 0);
        piechart.setHighlightPerTapEnabled(true);
        piechart.setExtraOffsets(5,5,5,5);

        piechart.getDescription().setEnabled(false);
        piechart.animateY(1000);
        piechart.highlightValue(null);


        piechart.invalidate();
    }

}
