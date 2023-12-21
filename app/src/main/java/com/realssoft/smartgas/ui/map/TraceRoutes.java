package com.realssoft.smartgas.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StyleSpan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.realssoft.smartgas.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TraceRoutes {

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue request;
    private List<List<HashMap<String, String>>> routes;
    private Point coordinates;
    private Context context;

    public TraceRoutes(Context context) {
        this.context = context;
        routes = new ArrayList<>();
        coordinates = new Point();
        request= Volley.newRequestQueue(context);
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double startLatitude, Double startLongitude,
                               Double endLatitude, Double endLongitude) {
        coordinates.setStartLatitude(startLatitude);
        coordinates.setStartLongitude(startLongitude);
        coordinates.setEndLatitude(endLatitude);
        coordinates.setEndLongitude(endLongitude);
    }

    public int sizeRoutes() {
        return routes.size();
    }

    public List<HashMap<String, String>> getRoute(int i) {
        return routes.get(i);
    }

    public void webServiceObtenerRuta(GoogleMap mMap, int ancho, int alto, TextView tvTitle, TextView tvTime, TextView tvDistance,
                                      FirebaseFirestore mFirestore) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + coordinates.getStartLatitude() + "," + coordinates.getStartLongitude()
                +"&destination=" + coordinates.getEndLatitude() + "," + coordinates.getEndLongitude()
                + "&key=AIzaSyAjpcgrGZgEQnt-Ot8LQwI_tA7Ff5fCv9I";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jDistance = null;
                JSONArray jDuration = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");
                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                            coordinates.setDistance((String)((JSONObject)((JSONObject)jLegs.get(j)).get("distance")).get("text"));
                            coordinates.setDuration((String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text"));

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            routes.add(path);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    addMarketRoute(mMap, ancho, alto);
                    mFirestore.collection("Mérida")
                            .orderBy("name", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        GeoPoint geoPoint = document.getGeoPoint("localitation");
                                        if (geoPoint != null) {
                                            LatLng localization = new LatLng(geoPoint.getLatitude(),
                                                    geoPoint.getLongitude());
                                            if(localization.latitude == coordinates.getEndLatitude()
                                            && localization.longitude == coordinates.getEndLongitude()) {
                                                coordinates.setName(Objects.requireNonNull(document.getString("name"))
                                                        .toUpperCase());
                                                tvTitle.setText(coordinates.getName());
                                                tvTime.setText(coordinates.getDuration());
                                                tvDistance.setText("(" + coordinates.getDistance() + ")");
                                                tvTitle.setVisibility(View.VISIBLE);
                                                tvDistance.setVisibility(View.VISIBLE);
                                                tvTime.setVisibility(View.VISIBLE);
                                                return;
                                            }
                                        }
                                    }
                                }
                            });
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );
        request.add(jsonObjectRequest);
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void addMarketRoute(GoogleMap mMap, int ancho, int alto) {
        /////////////
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        for(int i=0;i<sizeRoutes();i++){

            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = getRoute(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(15);
            //Definimos el color de la Polilíneas
            ///lineOptions.color(Color.parseColor("#00afff"));
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        mMap.addPolyline(lineOptions);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.gas_station);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap scaledBitmap =Bitmap.createScaledBitmap(bitmap, 120, 120, false);

        LatLng origen = new LatLng(getCoordinates().getStartLatitude(), getCoordinates().getStartLongitude());

        LatLng destino = new LatLng(getCoordinates().getEndLatitude(), getCoordinates().getEndLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(destino)
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));

        LatLngBounds.Builder constructor = new LatLngBounds.Builder();
        constructor.include(origen);
        constructor.include(destino);
        LatLngBounds limites = constructor.build();
        int padding = (int) (alto * 0.18); // 25% de espacio (padding) superior e inferior

        CameraUpdate centrarmarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho+(ancho/12), alto+(alto/12), padding);

        mMap.animateCamera(centrarmarcadores);


    }

}
