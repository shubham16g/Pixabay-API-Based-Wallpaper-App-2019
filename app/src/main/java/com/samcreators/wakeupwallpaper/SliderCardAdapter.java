package com.samcreators.wakeupwallpaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.List;

public class SliderCardAdapter extends PagerAdapter {

    Context context;
    List<Hit> list;

    public SliderCardAdapter(Context context, List<Hit> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_card_layout,container,false);
        final ImageView imageView = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.title);
        View card = view.findViewById(R.id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,FullViewActivity.class);
                i.putExtra("previewUrl", list.get(position).getPreviewURL());
                i.putExtra("url", list.get(position).getLargeImageURL());
                i.putExtra("id", list.get(position).getId());
                context.startActivity(i);
            }
        });
        Glide.with(context).asBitmap().load(list.get(position).getWebformatURL()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        String [] tags = list.get(position).getTags().split(",");
        String tag1 = tags[0].substring(0, 1).toUpperCase() + tags[0].substring(1);
        String tag2 = tags[1].substring(0, 2).toUpperCase() + tags[1].substring(2);
        String tagToTitle = tag1 + " " + tag2;
        textView.setText(tagToTitle);
        container.addView(view);
        return view;
    }
}
