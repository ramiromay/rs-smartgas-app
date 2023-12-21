package com.realssoft.smartgas.ui.gas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.realssoft.smartgas.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GasStationAdapter extends RecyclerView.Adapter<GasStationAdapter.ViewHolder> {

    protected Context context;
    private final ArrayList<GasStation> gasStationsArrayList;

    public GasStationAdapter(Context context, ArrayList<GasStation> gasStations) {
        this.context = context;
        this.gasStationsArrayList = gasStations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_layout_gas_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GasStation gasStation = gasStationsArrayList.get(position);
        holder.gasNameTV.setText(gasStation.getName());
        holder.gasDirectionTV.setText(gasStation.getDirection());
        holder.gasCostTV.setText(gasStation.getCost());
        holder.gasIV.setImageResource(gasStation.getImg());
        holder.oxxoCIV.setImageResource(gasStation.getServiceOxxo());
        holder.sevenElevenCIV.setImageResource(gasStation.getService7Eleven());
        holder.goMartCIV.setImageResource(gasStation.getServiceGoMart());
    }

    @Override
    public int getItemCount() {
        return gasStationsArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView gasIV;
        private final TextView gasNameTV;
        private final TextView gasDirectionTV;
        private final TextView gasCostTV;
        private final CircleImageView oxxoCIV;
        private final CircleImageView sevenElevenCIV;
        private final CircleImageView goMartCIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gasIV = itemView.findViewById(R.id.idIVGasImage);
            gasNameTV = itemView.findViewById(R.id.idTVGasName);
            gasDirectionTV = itemView.findViewById(R.id.idTVGasDirection);
            gasCostTV = itemView.findViewById(R.id.idTVGasCost);
            oxxoCIV = itemView.findViewById(R.id.civLogoOxxo);
            sevenElevenCIV = itemView.findViewById(R.id.civLogo7Eleven);
            goMartCIV = itemView.findViewById(R.id.civLogoGoMart);
        }
    }

}
