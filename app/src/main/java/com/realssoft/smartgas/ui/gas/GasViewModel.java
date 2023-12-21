package com.realssoft.smartgas.ui.gas;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class GasViewModel extends ViewModel {

    private MutableLiveData<ArrayList<GasStation>> gasStationLiveData;

    public GasViewModel() {
        gasStationLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<GasStation>> getGasStationMutableLiveData(){
        return gasStationLiveData;
    }

}
