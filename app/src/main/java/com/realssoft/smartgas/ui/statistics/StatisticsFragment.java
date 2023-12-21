package com.realssoft.smartgas.ui.statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.realssoft.smartgas.HomeActivity;
import com.realssoft.smartgas.R;
import com.realssoft.smartgas.databinding.FragmentStatisticsBinding;
import com.realssoft.smartgas.util.Option;

import java.util.ArrayList;
import java.util.Objects;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private FragmentStatisticsBinding binding;
    private FirebaseFirestore mFirestore;
    private StatisticAdapter statisticAdapter;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;

    private ArrayList<Statistic> statisticArrayList;
    private ArrayList<CostGasStation> costGasStationArrayList;
    private ArrayList<IBarDataSet> iBarDataSetArrayList1;
    private ArrayList<IBarDataSet> iBarDataSetArrayList2;
    private ArrayList<IBarDataSet> iBarDataSetArrayList3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if(HomeActivity.tvStatus != null){
            HomeActivity.tvStatus.setText(R.string.app_name);
        }
        setConfigurationIni();
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        stopShimmerOff();
        super.onStop();
    }

    @Override
    public void onStart() {
        startShimmerOn();
        super.onStart();
    }

    @Override
    public void onResume() {
        handler.postDelayed(this::getDataFirebase, 0);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getDataFirebase() {
        mFirestore.collection("Mérida").get()
                .addOnCompleteListener(task -> {
                    costGasStationArrayList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CostGasStation costGasStation = new CostGasStation();
                            costGasStation.setId(document.getId());
                            costGasStation.setName(Objects.requireNonNull(document.get("name"))
                                    .toString().toUpperCase());
                            System.out.println(costGasStation.getName());
                            costGasStation.setCostMagna(Objects.requireNonNull(document
                                    .get("costMagna")).toString());
                            costGasStation.setCostPremium(Objects.requireNonNull(document
                                    .get("costPremium")).toString());
                            costGasStation.setCostDiesel(Objects.requireNonNull(document
                                    .get("costDiesel")).toString());
                            costGasStationArrayList.add(costGasStation);
                        }
                        stopShimmerOff();
                        cleanArrayList();
                        getStatistics();
                        statisticAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error getting documents.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        setAdapterRecyclerView();
    }

    private void getDataGasStation(@NonNull ArrayList<CostGasStation> costGasStationArrayList, int option) {
        int pos = 0;
        Statistic statistic = new Statistic();
        Option.getOptionCost(costGasStationArrayList, option);
        for (CostGasStation costGasStation : costGasStationArrayList) {
            if (Double.parseDouble(costGasStation.getOptionCost(option)) != 0) {
                if (pos == 0) {
                    statistic.setTitleCategory(Option.getOptionCategory(option));
                    statistic.setNameGasStationOne(costGasStation.getName().toUpperCase());
                    addIBarDataSet(costGasStation,1, option, statistic);
                } else if (pos == 1) {
                    statistic.setNameGasStationTwo(costGasStation.getName().toUpperCase());
                    addIBarDataSet(costGasStation,0, option, statistic);
                    statisticArrayList.add(statistic);
                    return;
                }
                pos++;
            }
        }
    }

    private void addIBarDataSet(@NonNull CostGasStation costGasStationArrayList, int pos,
                                int option, Statistic statistic) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(pos,
                Float.parseFloat(costGasStationArrayList.getOptionCost(option)), option));
        BarDataSet barDataSet = new BarDataSet(barEntries, null);
        if (pos == 1) {
            barDataSet.setColors(Color.parseColor("#2b83fa"));
        } else {
            barDataSet.setColors(Color.parseColor("#fc0002"));
        }
        barDataSet.setHighlightEnabled(false);
        Utils.init(getContext());
        barDataSet.setValueTextSize(Utils.convertDpToPixel(4f));
        switch (option) {
            case Option.OPTION_MAGNA:
                iBarDataSetArrayList1.add(barDataSet);
                statistic.setBarData(iBarDataSetArrayList1);
                break;
            case Option.OPTION_PREMIUM:
                iBarDataSetArrayList2.add(barDataSet);
                statistic.setBarData(iBarDataSetArrayList2);
                break;
            case Option.OPTION_DIESEL:
                iBarDataSetArrayList3.add(barDataSet);
                statistic.setBarData(iBarDataSetArrayList3);
                break;
        }
    }

    private void cleanArrayList() {
        statisticArrayList.clear();
        iBarDataSetArrayList1.clear();
        iBarDataSetArrayList2.clear();
        iBarDataSetArrayList3.clear();
    }

    private void getStatistics() {
        getDataGasStation(costGasStationArrayList, Option.OPTION_MAGNA);
        getDataGasStation(costGasStationArrayList, Option.OPTION_PREMIUM);
        getDataGasStation(costGasStationArrayList, Option.OPTION_DIESEL);
    }

    private void setConfigurationIni() {
        mFirestore = FirebaseFirestore.getInstance();
        handler = new Handler(Looper.getMainLooper());
        getBinding();
        initArrayList();
        initRecyclerView();
    }

    private void initArrayList() {
        statisticArrayList = new ArrayList<>();
        costGasStationArrayList = new ArrayList<>();
        iBarDataSetArrayList1 = new ArrayList<>();
        iBarDataSetArrayList2 = new ArrayList<>();
        iBarDataSetArrayList3 = new ArrayList<>();
    }

    private void startShimmerOn() {
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
    }

    private void stopShimmerOff() {
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmer();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getBinding() {
        recyclerView = binding.idRVStatistic;
        shimmerFrameLayout = binding.idSFPreviewStatistic;
    }

    private void setAdapterRecyclerView() {
        statisticAdapter = new StatisticAdapter(getContext(), statisticArrayList);
        recyclerView.setAdapter(statisticAdapter);
    }

}