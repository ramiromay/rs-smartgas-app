package com.realssoft.smartgas;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CloudFirebase
{
    private String name, direction;

    public String getCostMagna() {
        return costMagna;
    }



    private String costMagna;
    private String costPremium;
    private String costDiesel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CloudFirebase()
    {

    }

    public CloudFirebase(String name, String direction, String costMagna,
                         String costPremium, String costDiesel)
    {
        this.name = name;
        this.direction = direction;
        this.costMagna = costMagna;
        this.costPremium = costPremium;
        this.costDiesel = costDiesel;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setCostMagna(String costMagna) {
        this.costMagna = costMagna;
    }

    public String getCostPremium() {
        return costPremium;
    }

    public void setCostPremium(String costPremium) {
        this.costPremium = costPremium;
    }

    public String getCostDiesel() {
        return costDiesel;
    }

    public void setCostDiesel(String costDiesel) {
        this.costDiesel = costDiesel;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }



    public void addDocument(String collectionName)
    {
        Map<String, Object> gasStation = new HashMap<>();
        gasStation.put("name", name);
        gasStation.put("direction", direction);
        gasStation.put("costMagna", costMagna);
        gasStation.put("costPremium", costPremium);
        gasStation.put("costDiesel", costDiesel);
        db.collection("Mérida")
                .document(collectionName.toString())
                .set(gasStation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                /*if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Agregado a la bd", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No se agregado a la bd", Toast.LENGTH_LONG).show();
                }*/
            }
        });
    }

    public void getDocument()
    {
        db.collection("Mérida")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                name = document.get("Nombre").toString();
                                direction = document.get("Ubicacion").toString();
                                //System.out.println(document.getData());
                                System.out.println("IdDocumento: "+ document.getId() +" | Nombre: " + name +  " | " + "Direccioón: " + direction);
                            }
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }



}
