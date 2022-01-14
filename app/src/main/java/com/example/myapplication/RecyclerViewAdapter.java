package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder> {

    private ArrayList<RecyclerViewItem> arrayList;



    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent, false);
        RecyclerViewViewHolder recyclerViewViewHolder = new RecyclerViewViewHolder(view);
        return recyclerViewViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int position) {

        RecyclerViewItem recyclerViewItem = arrayList.get(position);
        holder.textViewSortSurname.setText(recyclerViewItem.getTextViewSortSurname());
        holder.textViewClub.setText(recyclerViewItem.getTextViewClub());
        holder.textViewAmplua.setText(recyclerViewItem.getTextViewAmplua());
        holder.textViewAvarageQuantityFO.setText(recyclerViewItem.getTextViewAvarageQuantityFO()+"");
        holder.textViewIndexQuality.setText(recyclerViewItem.getTextViewIndexQuality()+"");
        holder.textViewMainIndex.setText(recyclerViewItem.getTextViewMainIndex()+"");



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewSortSurname;
        public TextView textViewClub;
        public TextView textViewAmplua;
        public TextView textViewAvarageQuantityFO;
        public TextView textViewIndexQuality;
        public TextView textViewMainIndex;

        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSortSurname = itemView.findViewById(R.id.text_view_sort_surname);
            textViewClub = itemView.findViewById(R.id.text_view_club);
            textViewAmplua = itemView.findViewById(R.id.text_view_amplua);
            textViewAvarageQuantityFO = itemView.findViewById(R.id.text_view_avarage_quantity_FO);
            textViewIndexQuality = itemView.findViewById(R.id.text_view_index_quality);
            textViewMainIndex = itemView.findViewById(R.id.text_view_main_index);



        }



    }

    public RecyclerViewAdapter (ArrayList<RecyclerViewItem> arrayList){
        this.arrayList = arrayList;

    }


}
