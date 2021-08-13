package com.example.MVMcR_MA_QCR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CarDetailsAdapter extends RecyclerView.Adapter<CarDetailsAdapter.CarViewHolder> {

    List<Questions_main> carDetails;
    Context context;

    public CarDetailsAdapter(List<Questions_main> carDetails, Context context) {
        this.context= context;
        this.carDetails = carDetails;
    }

    public List<Questions_main> getCarDetails() {
        return carDetails;
    }

    public void setCarDetails(List<Questions_main> carDetails) {
        this.carDetails = carDetails;
    }

    @NonNull
    @NotNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.car_details_item,parent,false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CarViewHolder holder, int position) {
        String qrcode=carDetails.get(position).getQr_code();
        String[] s=carDetails.get(position).getQr_code().split("_");

        holder.vin.setText(s[0]+"\n"+getModel(qrcode));
        holder.vinPosition.setText(position+1+"");
    }

    @Override
    public int getItemCount() {
        return  carDetails.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder{
        TextView vin,vinPosition;

        public CarViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            vin=itemView.findViewById(R.id.txtVin);
            vinPosition=itemView.findViewById(R.id.vinPosition);
        }
    }
    private String getModel(String  qr_code){

        String[] a = qr_code.split("_");
        String model_name = a[1].charAt(0)+""+a[1].charAt(1)+""+a[1].charAt(2);
        return model_name;
    }
}
