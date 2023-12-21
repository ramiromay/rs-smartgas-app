package com.realssoft.smartgas.util;

import androidx.annotation.Nullable;


import com.realssoft.smartgas.ui.statistics.CostGasStation;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class Option {

    public static final int OPTION_MAGNA = 0;
    public static final int OPTION_PREMIUM = 1;
    public static final int OPTION_DIESEL = 2;

    @Nullable
    @Contract(pure = true)
    public static String getOptionCategory(int option) {
        switch (option) {
            case OPTION_MAGNA:
                return "Magna";
            case OPTION_PREMIUM:
                return "Premium";
            case OPTION_DIESEL:
                return "Diesel";
        }
        return null;
    }

    public static void getOptionCost(ArrayList<CostGasStation> costGasStations, int option) {
        OrderBy orderBy = new OrderBy();
        switch (option) {
            case OPTION_MAGNA:
                orderBy.costMagna(costGasStations);
                break;
            case OPTION_PREMIUM:
                orderBy.costPremium(costGasStations);
                break;
            case OPTION_DIESEL:
                orderBy.costDiesel(costGasStations);
                break;
        }
    }

}
