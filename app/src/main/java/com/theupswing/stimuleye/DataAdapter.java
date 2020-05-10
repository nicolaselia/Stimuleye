package com.theupswing.stimuleye;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ExampleViewHolder> {
    private ArrayList<DataItem> mExampleList;
    private OnItemClickListener mListener; // make sure you import the one that you implemented below (heed the package name)

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView diameter;
        public TextView flash;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            diameter = itemView.findViewById(R.id.diameter);
            flash = itemView.findViewById(R.id.flash);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public DataAdapter(ArrayList<DataItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        DataItem currentItem = mExampleList.get(position);

        holder.time.setText(currentItem.getTime() + "");
        holder.diameter.setText(currentItem.getDiameter() + "");
        holder.flash.setText(currentItem.getFlash());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
