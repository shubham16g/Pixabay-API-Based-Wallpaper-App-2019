package com.samcreators.wakeupwallpaper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {


    private Context context;
    private ArrayList<String> list;
    private int layout;

    public GridAdapter(Context context, int layout, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @NonNull
    @Override
    public GridAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.GridViewHolder holder, final int position) {
        Glide.with(context).load(list.get(position)).into(holder.imageBackground);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FullViewActivity.class);
                i.putExtra("uri", list.get(position));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBackground;
        View card;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBackground = itemView.findViewById(R.id.image);
            card = itemView.findViewById(R.id.card);
            card.setMinimumHeight(310);
        }
    }
}