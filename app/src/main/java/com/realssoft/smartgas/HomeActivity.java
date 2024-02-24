package com.realssoft.smartgas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.realssoft.smartgas.databinding.ActivityMainBinding;
import com.realssoft.smartgas.ui.dialog.RequestLocationFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static CircleImageView circleImageViewProfile;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvStatus;
    private FirebaseAnalytics mFirebaseAnalytics;

    private ActivityMainBinding binding;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private FloatingActionButton fbaMyLocation;
    private Activity activity = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        tvStatus = findViewById(R.id.idTVStatus);


        circleImageViewProfile = findViewById(R.id.profile_image);
//        circleImageViewProfile.setOnClickListener(view ->);
//                Toast.makeText(getApplicationContext(),
//                        "Presiono la imagen de perfil!", Toast.LENGTH_SHORT).show());


        //setCloudFirebase();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void showDialogRequestLocation() {
        RequestLocationFragment.display(getSupportFragmentManager());
        Toast.makeText(getApplicationContext(), "Ventana fullscreem", Toast.LENGTH_SHORT).show();
    }

    /*public void setCloudFirebase()
    {
        try
        {
            CloudFirebase cloudFirebase = new CloudFirebase();
            InputStream inputStream = this.getResources().openRawResource(R.raw.gasolineras);
            BufferedReader lectura = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            StringBuilder collectionName = new StringBuilder();
            StringBuilder cost = new StringBuilder();
            int cont = 1;

            while ((line = lectura.readLine()) != null)
            {
                collectionName.append("Gasolinera_").append(cont);
                String[] documents = line.split(">");
                cloudFirebase.setName(documents[0].trim());
                cloudFirebase.setCostMagna(cost.append(documents[1].trim())
                        .deleteCharAt(0)
                        .toString());
                cost.setLength(0);
                cloudFirebase.setCostPremium(cost.append(documents[2].trim())
                        .deleteCharAt(0)
                        .toString());
                cost.setLength(0);
                cloudFirebase.setCostDiesel(cost.append(documents[3].trim())
                        .deleteCharAt(0)
                        .toString());
                cost.setLength(0);
                cloudFirebase.setDirection(documents[4].trim());
                cloudFirebase.addDocument(collectionName.toString());
                System.out.println(collectionName.toString());
                collectionName.setLength(0);
                cont ++;

            }
            inputStream.close();
            lectura.close();
        }
        catch (Exception e)
        {
            System.out.println("Algo salio mal");
            e.printStackTrace();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    createLocationRequest();
                    Toast.makeText(getApplicationContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    showDialogRequestLocation();
                    Toast.makeText(getApplicationContext(), "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if(resultCode == Activity.RESULT_OK)
                {

                    Toast.makeText(getApplicationContext(), "Se encendio la ubicacion", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    finish();
                }
        }
    }

}