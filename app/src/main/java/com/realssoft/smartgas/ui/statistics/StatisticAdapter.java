package com.realssoft.smartgas.ui.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.realssoft.smartgas.R;

import java.util.ArrayList;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    protected Context context;
    private final ArrayList<Statistic> statisticArrayList;

    public StatisticAdapter(Context context, ArrayList<Statistic> statisticArrayList) {
        this.context = context;
        this.statisticArrayList = statisticArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_layout_statistic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Statistic statistic = statisticArrayList.get(position);
        holder.titleCategoryTV.setText(statistic.getTitleCategory());
        holder.oneGasStationTV.setText(statistic.getNameGasStationOne());
        holder.twoGasStationTV.setText(statistic.getNameGasStationTwo());
        holder.graphHBC.setData(statistic.getBarData());
    }

    @Override
    public int getItemCount() {
        return statisticArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView titleCategoryTV;
        private final TextView oneGasStationTV;
        private final TextView twoGasStationTV;
        private final HorizontalBarChart graphHBC;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleCategoryTV = itemView.findViewById(R.id.idTVTitleStatistic);
            oneGasStationTV = itemView.findViewById(R.id.idTVOneGasStation);
            twoGasStationTV = itemView.findViewById(R.id.idTVTwoGasStation);
            graphHBC = itemView.findViewById(R.id.idHBCGraph);
            setUpAxisXY();
            setUpGraph();
        }

        private void setUpAxisXY() {
            XAxis xl = graphHBC.getXAxis();
            xl.setDrawGridLines(false);
            xl.setDrawLabels(false);
            xl.setAxisMaximum(2f);
            xl.setEnabled(false);

            YAxis yl = graphHBC.getAxisLeft();
            yl.setAxisMinimum(0f);
            yl.setTextSize(11.5f);
            yl.setAxisMaximum(24f);
        }

        private void setUpGraph() {
            graphHBC.animateY(2000);
            graphHBC.getDescription().setEnabled(false);
            graphHBC.setDrawBarShadow(false);
            graphHBC.setPinchZoom(false);
            graphHBC.getAxisRight().setEnabled(false);
            graphHBC.getLegend().setEnabled(false);
            graphHBC.invalidate();
        }

    }

}