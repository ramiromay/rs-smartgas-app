package com.realssoft.smartgas.ui.gas;

import android.annotation.SuppressLint;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.realssoft.smartgas.HomeActivity;
import com.realssoft.smartgas.R;
import com.realssoft.smartgas.databinding.FragmentGasBinding;
import com.realssoft.smartgas.util.Option;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GasFragment extends Fragment {

    private GasViewModel gasViewModel;
    private FragmentGasBinding binding;
    private ArrayList<GasStation> gasStationArrayList;
    private FirebaseFirestore mFirestore;
    private RecyclerView recyclerView;
    private GasStationAdapter gasStationAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MaterialButton materialButtonMagna;
    private MaterialButton materialButtonPremium;
    private MaterialButton materialButtonDiesel;
    private Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gasViewModel =
                new ViewModelProvider(this).get(GasViewModel.class);
        binding = FragmentGasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setConfigurationIni();
        setLayoutRecyclerView();
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
        handler.postDelayed(() -> {
            //Do something after 100ms
            getDataOrderBy(Option.OPTION_MAGNA);
        }, 0);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        mFirestore = null;
        super.onDestroyView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getDataOrderBy(int option) {
        String ORDER_BY = "";
        String OPTION_ORDER_BY = "";
        switch (option){
            case Option.OPTION_MAGNA:
                ORDER_BY = "costMagna";
                OPTION_ORDER_BY = "Magna";
                break;
            case Option.OPTION_PREMIUM:
                ORDER_BY = "costPremium";
                OPTION_ORDER_BY = "Premium";
                break;
            case Option.OPTION_DIESEL:
                ORDER_BY = "costDiesel";
                OPTION_ORDER_BY = "Diesel";
                break;
        }
        HomeActivity.tvStatus.setText(OPTION_ORDER_BY);
        mFirestore.collection("Mérida")
                .orderBy(ORDER_BY, Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task -> {
                    gasStationArrayList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            GasStation gasStation = new GasStation();
                            gasStation.setName(Objects.requireNonNull(document
                                    .get("name")).toString().toUpperCase());
                            gasStation.setDirection(Objects.requireNonNull(document
                                    .get("direction")).toString());
                            switch (option){
                                case Option.OPTION_MAGNA:
                                    gasStation.setCost(Objects.requireNonNull(document
                                            .get("costMagna")).toString());
                                    break;
                                case Option.OPTION_PREMIUM:
                                    gasStation.setCost(Objects.requireNonNull(document
                                            .get("costPremium")).toString());
                                    break;
                                case Option.OPTION_DIESEL:
                                    gasStation.setCost(Objects.requireNonNull(document
                                            .get("costDiesel")).toString());
                                    break;
                            }
                            gasStation.setImg(R.drawable.gas_station);
                            getServices(gasStation, document);
                            if (!gasStation.getCost().equals("0")) {
                                gasStationArrayList.add(gasStation);
                            }
                        }
                        stopShimmerOff();
                        gasStationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error getting documents.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        setAdapterRecyclerView();
    }

    @SuppressWarnings("unchecked")
    protected void getServices(GasStation gasStation, @NonNull QueryDocumentSnapshot document) {
        Map<String, Object> services =  new ObjectMapper()
                .convertValue(document.get("services"), Map.class);
        if(services != null) {
            if (Objects.equals(services.get("oxxo"), true)) {
                gasStation.setServiceOxxo(R.drawable.logo_oxxo);
            } else {
                gasStation.setServiceOxxo(R.drawable.logo_oxxo_gray);
            }
            if (Objects.equals(services.get("seven"), true)) {
                gasStation.setService7Eleven(R.drawable.logo_7_eleven);
            } else {
                gasStation.setService7Eleven(R.drawable.logo_7_eleven_gray);
            }
            if (Objects.equals(services.get("goMart"), true)) {
                gasStation.setServiceGoMart(R.drawable.logo_go_mart);
            } else {
                gasStation.setServiceGoMart(R.drawable.logo_go_mart_gray);
            }
        } else {
            gasStation.setServiceOxxo(R.drawable.logo_oxxo);
            gasStation.setService7Eleven(R.drawable.logo_7_eleven);
            gasStation.setServiceGoMart(R.drawable.logo_go_mart);
        }
    }

    private void setConfigurationIni() {
        getBinding();
        setOnClickListenerButton();
        HomeActivity.tvStatus.setText(null);
        mFirestore = FirebaseFirestore.getInstance();
        gasStationArrayList = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    private void setAdapterRecyclerView() {
        gasStationAdapter = new GasStationAdapter(getContext(), gasStationArrayList);
        recyclerView.setAdapter(gasStationAdapter);
    }

    private void setLayoutRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setOnClickListenerButton() {
        materialButtonMagna.setOnClickListener(view -> {
            startShimmerOn();
            handler.postDelayed(() -> {
                //Do something after 100ms
                getDataOrderBy(Option.OPTION_MAGNA);
            }, 0);
        });
        materialButtonPremium.setOnClickListener(view -> {
            startShimmerOn();
            handler.postDelayed(() -> {
                //Do something after 100ms
                getDataOrderBy(Option.OPTION_PREMIUM);
            }, 0);
        });
        materialButtonDiesel.setOnClickListener(view  -> {
            startShimmerOn();
            handler.postDelayed(() -> {
                //Do something after 100ms
                getDataOrderBy(Option.OPTION_DIESEL);
            }, 0);
        });
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

    private void getBinding() {
        materialButtonMagna = binding.idETMagna;
        materialButtonPremium = binding.idETPremium;
        materialButtonDiesel = binding.idETDiesel;
        recyclerView = binding.idRVGas;
        shimmerFrameLayout = binding.idSFPreviewGasStation;
    }

}
