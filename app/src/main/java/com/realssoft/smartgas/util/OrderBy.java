package com.realssoft.smartgas.util;

import androidx.annotation.NonNull;

import com.realssoft.smartgas.ui.statistics.CostGasStation;

import java.util.ArrayList;
import java.util.Comparator;

public class OrderBy implements Comparator<CostGasStation> {

    public void costMagna(@NonNull ArrayList<CostGasStation> costGasStations) {
        costGasStations.sort(Comparator.comparing(CostGasStation::getCostMagna));
    }

    public void costPremium(@NonNull ArrayList<CostGasStation> costGasStations) {
        costGasStations.sort(Comparator.comparing(CostGasStation::getCostPremium));
    }

    public void costDiesel(@NonNull ArrayList<CostGasStation> costGasStations) {
        costGasStations.sort(Comparator.comparing(CostGasStation::getCostDiesel));
    }

    @Override
    public int compare(CostGasStation costGasStation, CostGasStation t1) {
        return 0;
    }

}
