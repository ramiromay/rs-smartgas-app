package com.realssoft.smartgas.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.android.SphericalUtil;
import com.realssoft.smartgas.HomeActivity;
import com.realssoft.smartgas.R;
import com.realssoft.smartgas.databinding.FragmentMapBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private FragmentMapBinding binding;
    private FirebaseFirestore mFirestore;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private TraceRoutes traceRoutes;
    private ArrayList<Marker> markerArrayList;
    private FusedLocationProviderClient fusedLocationClient;

    private FloatingActionButton fbaMyLocation;
    private FloatingActionButton fbaAllGasStation;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvDistance;

    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result.containsValue(false)) {
                    //Permission granted

                } else {
                    //permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //show permission snackbar
                    } else {
                        //display error dialog
                    }
                }

            });


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        tvTitle = rootView.findViewById(R.id.idTVtitleInfo);
        tvTime = rootView.findViewById(R.id.idTVDurationInfo);
        tvDistance = rootView.findViewById(R.id.idTVDistanceInfo);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        initializeSetup();
        return rootView;
    }

    private void goneInfo() {
        tvTitle.setVisibility(View.GONE);
        tvDistance.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
    }

    private void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);
        bottomSheetDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        //mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        updateMapMerida();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean traceRouteMarker(LatLng latLng) {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PermissionChecker.PERMISSION_GRANTED) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Solicitud de permisos")
                        .setMessage("Necesita conceder permisos de ubicacion para poder usar esta función")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateMapMerida();
                            }
                        }).show();
            } else {
                //Permission already granted
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return false;
                }
                mMap.clear();
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            traceRoutes.setCoordinates(location.getLatitude(), location.getLongitude(),
                                    latLng.latitude, latLng.longitude);
                            int ancho = getResources().getDisplayMetrics().widthPixels;
                            int alto = getResources().getDisplayMetrics().heightPixels;
                            traceRoutes.webServiceObtenerRuta(mMap, ancho, alto, tvTitle, tvTime, tvDistance, mFirestore);
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initializeSetup() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        if (HomeActivity.tvStatus != null) {
            HomeActivity.tvStatus.setText(R.string.app_name);
        }
        mFirestore = FirebaseFirestore.getInstance();
        context = getContext();
        traceRoutes = new TraceRoutes(context);
        markerArrayList = new ArrayList<>();
        getBinding();
        setOnClickFABs();

    }

    private void getBinding() {
        fbaMyLocation = binding.idFABMyLocation;
        fbaAllGasStation = binding.idFABAllGasStation;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setOnClickFABs() {
        fbaMyLocation.setOnClickListener((view) -> {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            } else {
                //Permission already granted
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return;
                }
                goneInfo();
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.gas_station);
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    Bitmap scaledBitmap =Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                                    mMap.clear();
                                    Circle circle = mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .radius(5000)
                                            .strokeColor(Color.rgb(0, 136, 255))
                                            .fillColor(Color.argb(20, 0, 136, 255)));
                                    for (Marker marker : markerArrayList) {
                                        if (SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition()) < 5000) {
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(marker.getPosition())
                                                    .icon(BitmapDescriptorFactory
                                                            .fromBitmap(scaledBitmap)));

                                        }
                                    }
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.getCenter(), 12.1f));
                                }
                            }
                        });

            }
        });
        fbaAllGasStation.setOnClickListener((view) ->{
            updateMapMerida();
        });
    }

    private void updateMapMerida() {
        goneInfo();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.gas_station);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap scaledBitmap =Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        mMap.clear();
        if( markerArrayList.size() == 0) {
            mFirestore.collection("Mérida")
                    .orderBy("name", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geoPoint = document.getGeoPoint("localitation");
                                if (geoPoint != null) {
                                    LatLng localization = new LatLng(geoPoint.getLatitude(),
                                            geoPoint.getLongitude());
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.Q)
                                        @Override
                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                            return traceRouteMarker(marker.getPosition());
                                        }
                                    });
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(localization)
                                            .icon(BitmapDescriptorFactory
                                                    .fromBitmap(scaledBitmap)));
                                    markerArrayList.add(marker);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Error getting documents.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            for (Marker marker : markerArrayList) {
                mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition())
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
            }
        }

        LatLng merida = new LatLng(21.0039129,-89.6313468);
        //mMap.addMarker(new MarkerOptions().position(sydney)
        // .title("Marker Title").snippet("Marker Description"));
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(merida).zoom(11.5f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}