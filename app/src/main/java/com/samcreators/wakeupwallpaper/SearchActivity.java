package com.samcreators.wakeupwallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    List<Hit> list = new ArrayList<>();
    private boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private static final String URL = new Config().getAPI_URL();
    private int lastPage = 2;
    RecyclerView searchRecycler;
    androidx.appcompat.widget.Toolbar searchToolbar;
    RecyclerAdapter adapter;
    String extra;
    GridLayoutManager manager;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchRecycler = findViewById(R.id.search_recycler);
        searchToolbar = findViewById(R.id.search_toolbar);

        searchView = findViewById(R.id.search_bar);

        String title = getIntent().getStringExtra("query");
        searchToolbar.setTitle(title);

        extra = getIntent().getStringExtra("extra");

        searchToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView.setQuery(title, true);
        searchView.setFocusable(false);
        fetchData(1);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                list.clear();
                adapter.notifyDataSetChanged();
                extra = "&q=" + searchView.getQuery().toString();
                extra = extra.replaceAll("\\s", "+");
                fetchData(1);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        manager = new GridLayoutManager(this, GridLayoutManager.chooseSize(2, 2, 0));
        adapter = new RecyclerAdapter(this, R.layout.grid_layout, R.layout.activity_more_item, list);
        searchRecycler.setLayoutManager(manager);
        searchRecycler.setAdapter(adapter);



        searchRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void fetchData(int page) {
        MainActivity.hideKeyboard(this);
        StringRequest request = new StringRequest(URL + extra + "&page=" + page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                ResultFormat result = gson.fromJson(response, ResultFormat.class);
                List<Hit> hit = result.getHits();
                for (int i = 0; i < hit.size(); i++) {
                    list.add(new Hit(hit.get(i).getLargeImageURL(), hit.get(i).getId(), hit.get(i).getTags(), hit.get(i).getWebformatURL(), hit.get(i).getPreviewURL()));
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(SearchActivity.this));
        queue.add(request);
    }


    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }
}
