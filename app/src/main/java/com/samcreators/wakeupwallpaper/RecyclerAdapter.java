package com.samcreators.wakeupwallpaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private int layout;
    private Context context;
    private List<Hit> list;
    private int where;

    public RecyclerAdapter(Context context, int layout, int where, List<Hit> list){
        this.context = context;
        this.layout = layout;
        this.list = list;
        this.where = where;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        if (layout == R.layout.categories_layout || layout == R.layout.categories_frag_layout){
            final String title;
            title = list.get(position).getTitle();
            holder.title.setText(title);
            Glide.with(context).load(list.get(position).getPreviewURL()).into(holder.imageBackground);
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,MoreItemActivity.class);
                    i.putExtra("title",title);
                    i.putExtra("extra","&q=" + title.toLowerCase());
                    context.startActivity(i);
                }
            });
        }
        else {
            String [] tags = list.get(position).getTags().split(",");
            String tagToTitle;
            if (tags.length >= 2) {
                String tag1 = tags[0];
                String tag2 = tags[1];
                tagToTitle = tag1.substring(0, 1).toUpperCase() + tag1.substring(1) + tag2.substring(0, 2).toUpperCase() + tag2.substring(2);
            }
            else {
                tagToTitle = tags[0];
            }

            holder.title.setText(tagToTitle);
            Glide.with(context).load(list.get(position).getWebformatURL()).into(holder.imageBackground);
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,FullViewActivity.class);
                    i.putExtra("previewUrl", list.get(position).getPreviewURL());
                    i.putExtra("url", list.get(position).getLargeImageURL());
                    i.putExtra("id", list.get(position).getId());
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageBackground;
            TextView title;
        View card;
        CardView cardView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBackground = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            card = itemView.findViewById(R.id.card);
            cardView = itemView.findViewById(R.id.card_view);
            if (layout == R.layout.categories_frag_layout) {
                cardView.setMinimumHeight(340);
                cardView.setRadius(170);
            }
            if (layout == R.layout.grid_layout){
                cardView.setMinimumHeight(650);
            }
        }
    }
}