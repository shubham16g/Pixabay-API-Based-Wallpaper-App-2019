package com.samcreators.wakeupwallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoreItemActivity extends AppCompatActivity {

    List<Hit> list = new ArrayList<>();
    private boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private static final String URL = new Config().getAPI_URL();
    private int lastPage = 2;
    RecyclerView moreItemRecycler;
    androidx.appcompat.widget.Toolbar moreItemToolbar;
    RecyclerAdapter recyclerAdapter;
    GridAdapter gridAdapter;
    String extra;
    GridLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_item);

        moreItemRecycler = findViewById(R.id.more_item_recycler);
        moreItemToolbar = findViewById(R.id.more_item_toolbar);

        String title = getIntent().getStringExtra("title");
        moreItemToolbar.setTitle(title);
        manager = new GridLayoutManager(this, GridLayoutManager.chooseSize(2, 2, 0));
        moreItemRecycler.setLayoutManager(manager);

        moreItemToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        moreItemToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (title.equals("DOWNLOADS") || title.equals("GALLERY")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (getApplicationContext().checkCallingOrSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS, 1);
                    finish();
                } else {
                    manager = new GridLayoutManager(this, GridLayoutManager.chooseSize(3, 3, 0));
                    ArrayList<String> imageUrlList;
                    if (title.equals("DOWNLOADS")) {
                        imageUrlList = getDownloadsFromSdcard();
                    } else {
                        imageUrlList = getGalleryList();
                    }
                    gridAdapter = new GridAdapter(this, R.layout.grid_gallery_layout, imageUrlList);
                    moreItemRecycler.setAdapter(gridAdapter);
                }
            } else {
                manager = new GridLayoutManager(this, GridLayoutManager.chooseSize(3, 3, 0));
                ArrayList<String> imageUrlList;
                if (title.equals("DOWNLOADS")) {
                    imageUrlList = getDownloadsFromSdcard();
                } else {
                    imageUrlList = getGalleryList();
                }
                gridAdapter = new GridAdapter(this, R.layout.grid_gallery_layout, imageUrlList);
                moreItemRecycler.setAdapter(gridAdapter);
            }
        } 
        
        else {
            manager = new GridLayoutManager(this, GridLayoutManager.chooseSize(2, 2, 0));
            recyclerAdapter = new RecyclerAdapter(this, R.layout.grid_layout, R.layout.activity_more_item, list);
            moreItemRecycler.setAdapter(recyclerAdapter);
            extra = getIntent().getStringExtra("extra");
            fetchData(1);

            moreItemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true;
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    currentItems = manager.getChildCount();
                    totalItems = manager.getItemCount();
                    scrolledOutItems = manager.findFirstVisibleItemPosition();

                    if (isScrolling && currentItems + scrolledOutItems == totalItems) {
                        isScrolling = false;
                        fetchData(lastPage);
                        lastPage++;
                    }
                }
            });
        }

        moreItemRecycler.setLayoutManager(manager);

    }

    
    private ArrayList<String> getGalleryList() {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA};
        cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
        }
        return listOfAllImages;
    }

    private void fetchData(int page) {
        StringRequest request = new StringRequest(URL + extra + "&page=" + page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CODE", response);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                ResultFormat result = gson.fromJson(response, ResultFormat.class);
                List<Hit> hit = result.getHits();
                for (int i = 0; i < hit.size(); i++) {
                    list.add(new Hit(hit.get(i).getLargeImageURL(), hit.get(i).getId(), hit.get(i).getTags(), hit.get(i).getWebformatURL(), hit.get(i).getPreviewURL()));
                }
                recyclerAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(MoreItemActivity.this));
        queue.add(request);
    }


    public ArrayList<String> getDownloadsFromSdcard() {
        ArrayList<String> downloadsList = new ArrayList<>();
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "Wakeup Wallpaper");

        if (file.isDirectory()) {
            File[] listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                downloadsList.add(listFile[i].getAbsolutePath());
            }
        }
        return downloadsList;
    }

    @Override
    protected void onStart() {
        super.onStart();

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }
}
