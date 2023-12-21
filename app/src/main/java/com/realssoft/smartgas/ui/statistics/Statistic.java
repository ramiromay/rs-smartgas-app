package com.realssoft.smartgas.ui.statistics;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class Statistic {

    private String titleCategory = "";
    private String nameGasStationOne = "";
    private String nameGasStationTwo = "";
    private BarData barData = null;

    public Statistic() {}

    public String getTitleCategory() {
        return titleCategory;
    }

    public void setTitleCategory(String titleCategory) {
        this.titleCategory = titleCategory;
    }

    public String getNameGasStationOne() {
        return nameGasStationOne;
    }

    public void setNameGasStationOne(String nameGasStationOne) {
        this.nameGasStationOne = nameGasStationOne;
    }

    public String getNameGasStationTwo() {
        return nameGasStationTwo;
    }

    public void setNameGasStationTwo(String nameGasStationTwo) {
        this.nameGasStationTwo = nameGasStationTwo;
    }

    public BarData getBarData() {
        return barData;
    }

    public void setBarData(ArrayList<IBarDataSet> barData) {
        this.barData = new BarData(barData);
        this.barData.setBarWidth(0.9f);
    }

}
